package com.isel.leic.ps.ionClassCode.services

import com.isel.leic.ps.ionClassCode.utils.Result
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * Alias for the response of the services
 */
typealias EmailResponse = Result<EmailServiceError, Response>

/**
 * Error codes for the services
 */
sealed class EmailServiceError {
    object SendEmailError : EmailServiceError()
    object InvalidInput : EmailServiceError()
}

/**
 * Service to send emails
 */
@Component
class EmailService {

    private lateinit var sendGrid: SendGrid
    private val from = Email("i-on-classcode@outlook.pt")
    private val subject = "i-on ClassCode - OTP"
    private val contentType = "text/plain"
    private val endpoint = "mail/send"
    private val baseUri = "https://api.sendgrid.com"


    @PostConstruct
    fun init() {
        sendGrid = SendGrid(System.getenv("SENDGRID_CLASSCODE_API_KEY"))
    }

    /**
     * Method to send an email with the otp to the user
     */
    fun sendVerificationEmail(name: String, email: String, otp: Int): EmailResponse {
        if (name.isEmpty() || email.isEmpty() || otp < 0) {
            return Result.Problem(EmailServiceError.InvalidInput)
        }
        val to = Email(email)
        val content = Content(
            contentType,
            "Welcome to i-on ClassCode, $name! \nPlease verify your identity through" +
                " the following otp:\n $otp",
        )
        val mail = Mail(from, subject, to, content)
        val request = Request()
        return try {
            request.method = Method.POST
            request.baseUri = baseUri
            request.endpoint = endpoint
            request.body = mail.build()

            Result.Success(sendGrid.api(request))
        } catch (ex: IOException) {
            Result.Problem(EmailServiceError.SendEmailError)
        }
    }
}
