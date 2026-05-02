package com.uis.schedule.backend.service.implementation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

// Servicio encargado únicamente de construir y enviar correos
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // URL base del frontend, se lee desde application.properties
    @Value("${app.invitation.base-url}")
    private String baseUrl;

    @Value("${app.password-reset.base-url}")
    private String passwordResetBaseUrl;

    @Value("${app.password-reset.expiration-hours:2}")
    private int passwordResetExpirationHours;

    // Envía el correo de invitación con el enlace único al destinatario
    public void sendInvitationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Invitación al Sistema de Horarios UIS");

            // Construye el enlace con el token
            String link = baseUrl + "/" + token;

            // Cuerpo del correo en HTML básico
            String body = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2>Has sido invitado al Sistema de Horarios UIS</h2>"
                    + "<p>Por favor haz clic en el siguiente enlace para completar tu registro:</p>"
                    + "<a href='" + link + "' style='background-color:#1a73e8; color:white; "
                    + "padding:10px 20px; text-decoration:none; border-radius:4px;'>"
                    + "Completar registro</a>"
                    + "<p style='color:gray; font-size:12px;'>Este enlace expira en 24 horas.</p>"
                    + "</div>";

            helper.setText(body, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new IllegalStateException("Error al enviar el correo a: " + toEmail, e);
        }
    }

    // Envía el correo con enlace de restablecimiento de contraseña
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Recuperación de contraseña - UIS Schedule");

            String link = passwordResetBaseUrl + "/" + token;

            String body = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2>Solicitud de recuperación de contraseña</h2>"
                    + "<p>Recibimos una solicitud para restablecer tu contraseña.</p>"
                    + "<p>Haz clic en el siguiente enlace para continuar:</p>"
                    + "<a href='" + link + "' style='background-color:#2e7d32; color:white; "
                    + "padding:10px 20px; text-decoration:none; border-radius:4px;'>"
                    + "Restablecer contraseña</a>"
                    + "<p style='color:gray; font-size:12px;'>Este enlace expira en "
                    + passwordResetExpirationHours + " horas.</p>"
                    + "<p style='color:gray; font-size:12px;'>Si no solicitaste este cambio, ignora este mensaje.</p>"
                    + "</div>";

            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Error al enviar correo de recuperación a: " + toEmail, e);
        }
    }
}
