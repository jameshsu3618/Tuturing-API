package com.tuturing.api.configuration

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import javax.validation.constraints.NotEmpty
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HandlebarsConfiguration(
    @NotEmpty @Value("\${tuturing.handlebars.template-path}") val templatePath: String
) {
    @Bean
    fun handlebars(): Handlebars {
        return Handlebars(ClassPathTemplateLoader(templatePath))
    }

    @Bean
    @Throws(ParserConfigurationException::class)
    fun documentBuilder(): DocumentBuilder? {
        val fac = DocumentBuilderFactory.newInstance()
        fac.isNamespaceAware = false
        fac.isValidating = false
        fac.setFeature("http://xml.org/sax/features/namespaces", false)
        fac.setFeature("http://xml.org/sax/features/validation", false)
        fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
        fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        return fac.newDocumentBuilder()
    }
}
