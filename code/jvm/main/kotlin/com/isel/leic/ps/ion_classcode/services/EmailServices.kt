package com.isel.leic.ps.ion_classcode.services

import com.isel.leic.ps.ion_classcode.utils.Result
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.Response
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
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
    companion object {
        private val SENDGRID_API_KEY: String = System.getenv("SENDGRID_CLASSCODE_API_KEY")
        val sendGrid = SendGrid(SENDGRID_API_KEY)
        val FROM = Email("i-on-classcode@outlook.pt")
        const val SUBJECT = "i-on ClassCode - OTP"
        const val CONTENT_TYPE = "text/plain"
        const val ENDPOINT = "mail/send"
        const val BASE_URI = "https://api.sendgrid.com"
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
            CONTENT_TYPE,
            "Welcome to i-on ClassCode, $name! \nPlease verify your identity through" +
                " the following otp:\n $otp",
        )
        val mail = Mail(FROM, SUBJECT, to, content)
        val request = Request()
        return try {
            request.method = Method.POST
            request.baseUri = BASE_URI
            request.endpoint = ENDPOINT
            request.body = mail.build()

            Result.Success(sendGrid.api(request))
        } catch (ex: IOException) {
            Result.Problem(EmailServiceError.SendEmailError)
        }
    }
}
