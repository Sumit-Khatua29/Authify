package com.pm.authify_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Authify - Your Journey Starts Here!");
        String text = "Dear " + name + ",\n\n" +
                "Welcome to Authify! We are thrilled to have you on board.\n\n" +
                "Authify is your trusted solution for secure and seamless authentication. We are committed to providing you with the best experience possible.\n\n" +
                "To get started, simply log in to your dashboard and explore the features we have to offer.\n\n" +
                "If you have any questions or need assistance, our support team is always here to help.\n\n" +
                "Best regards,\n" +
                "The Authify Team";
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Authify - Password Reset Request");
        String text = "Hello,\n\n" +
                "We received a request to reset your password. Use the following OTP to proceed:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 15 minutes only. \n" +
                "If you did not initiate this request, please ignore this email.\n\n" +
                "Best regards,\n" +
                "The Authify Team";
        message.setText(text);
        javaMailSender.send(message);
    }

    public void sendVerifyOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Authify - Account Verification");
        String text = "Hello,\n\n" +
                "Thank you for signing up with Authify! To complete your registration, please verify your email address using the following OTP:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 24 hours. \n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "The Authify Team";
        message.setText(text);
        javaMailSender.send(message);
    }
}
