package io.github.e1i2.user.adapter

interface MailSender {
    suspend fun sendEmailAsync(subject: String, content: String, to: String)
}