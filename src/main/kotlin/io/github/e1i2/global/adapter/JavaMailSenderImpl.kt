package io.github.e1i2.global.adapter

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class JavaMailSenderImpl(
    private val javaMailSender: JavaMailSender
) : MailSender {
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun sendEmailAsync(subject: String, content: String, to: String) {
        GlobalScope.launch {
            val message = javaMailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(to)
            messageHelper.setFrom("feltpen.noreply <support@feltpen.site>")
            messageHelper.setSubject(subject)
            messageHelper.setText(content, true)

            javaMailSender.send(message)
        }
    }
}
