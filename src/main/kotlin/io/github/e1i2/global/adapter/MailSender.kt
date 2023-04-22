package io.github.e1i2.global.adapter

interface MailSender {
    suspend fun sendEmailAsync(subject: String, content: String, to: String)
}