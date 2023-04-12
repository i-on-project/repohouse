package com.isel.leic.ps.ion_classcode.http.services

import com.isel.leic.ps.ion_classcode.utils.Either
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
typealias EmailResponse = Either<EmailServiceError, Response>

/**
 * Error codes for the services
 */
sealed class EmailServiceError {
    object SendEmailError : EmailServiceError()
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

            Either.Right(sendGrid.api(request))
        } catch (ex: IOException) {
            Either.Left(EmailServiceError.SendEmailError)
        }
    }
}
