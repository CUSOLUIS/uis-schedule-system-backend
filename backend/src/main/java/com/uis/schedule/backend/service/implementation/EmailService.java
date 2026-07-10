package com.uis.schedule.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

// Servicio encargado únicamente de construir y enviar correos
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // URL base del frontend, se lee desde application.properties
    @Value("${app.invitation.base-url}")
    private String baseUrl;

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
            throw new RuntimeException("Error al enviar el correo a: " + toEmail, e);
        }
    }
}
