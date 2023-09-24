package com.tuturing.api.shared.scheduler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailMessage
import com.tuturing.api.shared.jms.SendEmailProducer
import com.tuturing.api.shared.service.SettingService
import java.io.BufferedReader
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.util.*
import javax.validation.constraints.NotEmpty
import kotlin.math.ceil
import kotlin.math.pow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private const val SENDGRID_WARMING_SCHEDULER_LOCK_KEY = "warmingSchedule:scheduler-lock"
private const val FIRST_WARMING_SCHEDULER_TIME_OFFSET_IN_MINUTES = 0

@Lazy(false)
@Component
@ConditionalOnProperty(prefix = "tuturing.sendgrid.warmingSchedule", value = ["scheduler-enabled"], havingValue = "true")
class SendgridWarmingScheduler(
    @Autowired private val settingsService: SettingService,
    @Autowired private val redisTemplate: StringRedisTemplate,
    @Autowired private val sendEmailProducer: SendEmailProducer,
    @NotEmpty @Value("\${tuturing.cdn-base-url}") private val cdnBaseUrl: String,
    @Value("classpath:json/sendgridRecipientsList.json") private val sendgridRecipientsList: Resource,
    @NotEmpty @Value("\${tuturing.sendgrid.warmingSchedule.lock-duration-seconds}") private val sendgridWarmingLockSeconds: Long
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "\${tuturing.sendgrid.warmingSchedule.schedule}")
    fun triggerSendgridWarmingSchedule() {
        logger.debug("Triggering Sendgrid warming schedule")

        // in the unlikely case, this helps prevent us from deleting somebody else's lock
        val rand = UUID.randomUUID().toString()
        val hasSetLock = redisTemplate.opsForValue()
            .setIfAbsent(SENDGRID_WARMING_SCHEDULER_LOCK_KEY, rand, Duration.ofSeconds(sendgridWarmingLockSeconds))
            ?: false

        logger.debug("Triggering Sendgrid warming schedule, lock result: {}", hasSetLock)

        // If we are not able to set the lock, somebody else has it
        if (!hasSetLock) return

        // convert time now to local date time as createdAt in setting entity is in DateTime format
        val now = LocalDate.now()

        // Find start date and set max days for scheduler to run (28 days after start date)
        val firstSync = LocalDate.parse(settingsService.findByKeyOrDefault("scheduler:last_trigger", now.atTime(0, FIRST_WARMING_SCHEDULER_TIME_OFFSET_IN_MINUTES).toString())
            .createdAt!!
            .toLocalDate()
            .toString())

        val maxDays = 21 // 3 weeks times 7 days each week
        val daysSinceFirstSync = Period.between(firstSync, now).days

        val jsonString = sendgridRecipientsList.inputStream.bufferedReader().use(BufferedReader::readText)
        var jsonTextList = jacksonObjectMapper().readValue<List<EmailAddress>>(jsonString)

        val emailVariables = listOf(
            Pair("Booking with tuturing", "d-487ec727c3f54654a3e65ef6ea8c30c2"),
            Pair("Booking with tuturing", "d-f942bff2424f4390980797126c49251c"),
            Pair("tuturing Password Change", "d-1677ea044e9d405f95a9e5abe8cf451d"),
            Pair("tuturing Registration Complete", "d-487ec727c3f54654a3e65ef6ea8c30c2")
        )

        // Repeat scheduler job for each week
        if (daysSinceFirstSync < maxDays) {
            val emailsToSend = ceil(48 * 1.25.pow(daysSinceFirstSync.toDouble()) / 24)

            repeat(emailsToSend.toInt()) {
                sendEmail(jsonTextList.random(), emailVariables.random())
            }
        }

        val lockValue = redisTemplate.opsForValue().get(SENDGRID_WARMING_SCHEDULER_LOCK_KEY)
        if (lockValue == rand) {
            redisTemplate.delete(SENDGRID_WARMING_SCHEDULER_LOCK_KEY)
        }
    }
    private fun sendEmail(recipient: EmailAddress, pair: Pair<String, String>) {
        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("cdn_url", cdnBaseUrl)
        emailTemplateVariables.put("company", "tuturing")
        emailTemplateVariables.put("first_name", "Traveler")
        sendEmailProducer.sendEmail(SendEmailMessage(
            "tuturing <noreply@tuturing.com>",
            listOf(recipient),
            pair.first,
            pair.second,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))
    }
}
