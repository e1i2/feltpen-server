package io.github.e1i2.user.adapter

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class JavaMailSenderImpl(
    private val javaMailSender: JavaMailSender
) : MailSender {
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun sendEmailAsync(subject: String, content: String, to: String) {
        GlobalScope.launch {
            val message = SimpleMailMessage()
            message.setTo(to)
            message.from = "feltpen.noreply <support@feltpen.site>"
            message.subject = subject
            message.text = content

            javaMailSender.send(message)
        }
    }
}
