package com.bloque3.notification_service.utils.message_handler.impl;

import org.springframework.stereotype.Service;

import com.bloque3.notification_service.exceptions.NotificationSendException;
import com.bloque3.notification_service.utils.message_handler.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service("smtp")
public class SMTPMessageHandler implements MessageHandler{

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Mono<Void> send(String subject, String body, String to) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                
            } catch (Exception ex) {
                throw new NotificationSendException(
                    "Error enviando correo a " + to + ": " + ex.getMessage(),
                    ex
                );
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

}
