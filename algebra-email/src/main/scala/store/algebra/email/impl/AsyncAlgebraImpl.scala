package store.algebra.email.impl

import java.util.Properties

import cats.implicits._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import javax.mail.internet._
import javax.mail._
import store.algebra.email._
import store.core.entity._
import store.effects._

/**
  * @author Daniel Incicau, daniel.incicau@busymachines.com
  * @since 20/08/2018
  */
final class AsyncAlgebraImpl[F[_]](config: EmailConfig)(implicit val F: Async[F])
    extends EmailAlgebra[F] {

  private[this] lazy val session: Session = {
    val props: Properties = System.getProperties

    props.setProperty("mail.smtp.from", config.from)

    props.put("mail.smtp.host",            config.smtpHost)
    props.put("mail.smtp.port",            config.smtpPort.toString)
    props.put("mail.smtp.user",            config.user)
    props.put("mail.smtp.password",        config.password)
    props.put("mail.smtp.starttls.enable", config.startTLS.toString)
    props.put("mail.smtps.auth",           config.auth.toString)
    props.put("mail.imap.host",            config.imapHost)
    props.put("mail.imap.port",            config.imapPort.toString)
    props.put("mail.imap.user",            config.user)
    props.put("mail.imap.password",        config.password)

    Session.getInstance(props, null)
  }

  private val logger: SelfAwareStructuredLogger[F] =
    Slf4jLogger.unsafeCreate[F]

  override def sendEmail(to: Email, subject: Subject, content: Content): F[Unit] =
    for {
      message   <- composeMimeMessage(to, Email(config.from).unsafeGet(), subject, content)
      transport <- F.delay(session.getTransport("smtp"))
      _         <- F.delay(transport.connect(config.smtpHost, config.user, config.password)).onError(cleanupErr(transport))
      _         <- logger.info("Connected to SMTP server")
      _         <- F.delay(transport.sendMessage(message, message.getAllRecipients)).onError(cleanupErr(transport))
      _         <- logger.info(s"Sent email to: ${to.emailStr}")
      _         <- cleanup(transport)
    } yield ()

  override def receiveMail(from: Email, subject: Subject, content: Content): F[Unit] =
    for {
      message   <- composeMimeMessage(Email(config.from).unsafeGet(), from, subject, content)
      transport <- F.delay(session.getTransport("smtp"))
      _         <- F.delay(transport.connect(config.smtpHost, config.user, config.password)).onError(cleanupErr(transport))
      _         <- logger.info("Connected to SMTP server")
      _         <- F.delay(transport.sendMessage(message, message.getAllRecipients)).onError(cleanupErr(transport))
      _         <- logger.info(s"Received an email")
      _         <- cleanup(transport)
    } yield ()

  private def composeMimeMessage(to: Email, from: Email, subject: Subject, content: Content): F[MimeMessage] = F.pure {
    val message: MimeMessage = new MimeMessage(session)

    message.setFrom(new InternetAddress(from.emailStr))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.emailStr))
    message.setSubject(subject)
    message.setContent(content, "text/html")
    message.saveChanges()
    message
  }

  private def cleanupErr(transport: Transport): PartialFunction[Throwable, F[Unit]] = {
    case scala.util.control.NonFatal(e) =>
      logger.warn(e)("Failed to send email.") >>
        cleanup(transport)
  }

  private def cleanup(transport: Transport): F[Unit] = F.delay(transport.close())
}
