package com.moveup.service;

import com.moveup.model.User;
import com.moveup.model.Instructor;
import com.moveup.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender emailSender;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.email.from:noreply@moveup.com}")
    private String fromEmail;
    
    @Value("${app.email.support:support@moveup.com}")
    private String supportEmail;
    
    // Send verification email
    public void sendVerificationEmail(User user) {
        try {
            String verificationUrl = frontendUrl + "/verify-email?token=" + user.getVerificationToken();
            
            String subject = "Verifica il tuo account MoveUp";
            String body = buildVerificationEmailBody(user.getFirstName(), verificationUrl);
            
            sendHtmlEmail(user.getEmail(), subject, body);
            logger.info("Verification email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }
    
    // Send password reset email
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            
            String subject = "Reset della password - MoveUp";
            String body = buildPasswordResetEmailBody(user.getFirstName(), resetUrl);
            
            sendHtmlEmail(user.getEmail(), subject, body);
            logger.info("Password reset email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }
    
    // Send booking confirmation email
    public void sendBookingConfirmationEmail(Booking booking, User user, Instructor instructor) {
        try {
            String subject = "Prenotazione confermata - MoveUp";
            String body = buildBookingConfirmationEmailBody(booking, user, instructor);
            
            sendHtmlEmail(user.getEmail(), subject, body);
            logger.info("Booking confirmation email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send booking confirmation email to: {}", user.getEmail(), e);
        }
    }
    
    // Send booking cancellation email
    public void sendBookingCancellationEmail(Booking booking, User user, Instructor instructor) {
        try {
            String subject = "Prenotazione cancellata - MoveUp";
            String body = buildBookingCancellationEmailBody(booking, user, instructor);
            
            sendHtmlEmail(user.getEmail(), subject, body);
            logger.info("Booking cancellation email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send booking cancellation email to: {}", user.getEmail(), e);
        }
    }
    
    // Send welcome email
    public void sendWelcomeEmail(User user) {
        try {
            String subject = "Benvenuto in MoveUp!";
            String body = buildWelcomeEmailBody(user.getFirstName());
            
            sendHtmlEmail(user.getEmail(), subject, body);
            logger.info("Welcome email sent to: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }
    
    // Send instructor verification email
    public void sendInstructorVerificationEmail(Instructor instructor) {
        try {
            String verificationUrl = frontendUrl + "/instructor/verify-email?token=" + instructor.getVerificationToken();
            
            String subject = "Verifica il tuo account Istruttore - MoveUp";
            String body = buildInstructorVerificationEmailBody(instructor.getFirstName(), verificationUrl);
            
            sendHtmlEmail(instructor.getEmail(), subject, body);
            logger.info("Instructor verification email sent to: {}", instructor.getEmail());
            
        } catch (Exception e) {
            logger.error("Failed to send instructor verification email to: {}", instructor.getEmail(), e);
        }
    }
    
    // Generic method to send HTML emails
    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        emailSender.send(message);
    }
    
    // Generic method to send simple text emails
    private void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        emailSender.send(message);
    }
    
    // Email body builders
    private String buildVerificationEmailBody(String firstName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #1E88E5; text-align: center;">Benvenuto in MoveUp!</h1>
                    <p>Ciao %s,</p>
                    <p>Grazie per esserti registrato su MoveUp! Per completare la registrazione, clicca sul link sottostante per verificare il tuo indirizzo email:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #1E88E5; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Verifica Email</a>
                    </div>
                    <p>Se non hai creato questo account, puoi ignorare questa email.</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, verificationUrl);
    }
    
    private String buildPasswordResetEmailBody(String firstName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #1E88E5; text-align: center;">Reset Password</h1>
                    <p>Ciao %s,</p>
                    <p>Hai richiesto di reimpostare la password per il tuo account MoveUp. Clicca sul link sottostante per impostare una nuova password:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #1E88E5; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Reset Password</a>
                    </div>
                    <p>Questo link scadrà tra 24 ore. Se non hai richiesto questo reset, puoi ignorare questa email.</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, resetUrl);
    }
    
    private String buildBookingConfirmationEmailBody(Booking booking, User user, Instructor instructor) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #43A047; text-align: center;">Prenotazione Confermata!</h1>
                    <p>Ciao %s,</p>
                    <p>La tua prenotazione è stata confermata con successo!</p>
                    <div style="background-color: #f9f9f9; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3>Dettagli della prenotazione:</h3>
                        <p><strong>Istruttore:</strong> %s %s</p>
                        <p><strong>Data:</strong> %s</p>
                        <p><strong>Ora:</strong> %s</p>
                        <p><strong>Importo:</strong> €%.2f</p>
                    </div>
                    <p>Ti aspettiamo per la lezione!</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(
                user.getFirstName(),
                instructor.getFirstName(),
                instructor.getLastName(),
                booking.getScheduledDate().toLocalDate(),
                booking.getScheduledTime(),
                booking.getTotalAmount()
            );
    }
    
    private String buildBookingCancellationEmailBody(Booking booking, User user, Instructor instructor) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #F44336; text-align: center;">Prenotazione Cancellata</h1>
                    <p>Ciao %s,</p>
                    <p>La tua prenotazione è stata cancellata.</p>
                    <div style="background-color: #f9f9f9; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3>Dettagli della prenotazione cancellata:</h3>
                        <p><strong>Istruttore:</strong> %s %s</p>
                        <p><strong>Data:</strong> %s</p>
                        <p><strong>Ora:</strong> %s</p>
                    </div>
                    <p>Se hai bisogno di assistenza, contatta il nostro supporto all'indirizzo %s</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(
                user.getFirstName(),
                instructor.getFirstName(),
                instructor.getLastName(),
                booking.getScheduledDate().toLocalDate(),
                booking.getScheduledTime(),
                supportEmail
            );
    }
    
    private String buildWelcomeEmailBody(String firstName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #1E88E5; text-align: center;">Benvenuto in MoveUp!</h1>
                    <p>Ciao %s,</p>
                    <p>Il tuo account è stato verificato con successo! Ora puoi iniziare a prenotare lezioni con i nostri migliori istruttori.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #1E88E5; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Inizia a Esplorare</a>
                    </div>
                    <p>Grazie per aver scelto MoveUp per il tuo percorso sportivo!</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, frontendUrl);
    }
    
    private String buildInstructorVerificationEmailBody(String firstName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px;">
                    <h1 style="color: #43A047; text-align: center;">Benvenuto Istruttore!</h1>
                    <p>Ciao %s,</p>
                    <p>Grazie per esserti registrato come istruttore su MoveUp! Per completare la registrazione, clicca sul link sottostante:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #43A047; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Verifica Account</a>
                    </div>
                    <p>Una volta verificato, il nostro team esaminerà la tua candidatura.</p>
                    <p>Il team di MoveUp</p>
                </div>
            </body>
            </html>
            """.formatted(firstName, verificationUrl);
    }
}