package com.tuturing.api.shared.jms

data class SendEmailMessage(
    var sender: String,
    var recipients: List<EmailAddress>,
    var subject: String,
    var templateName: String,
    var templateVariablesJson: String
)
