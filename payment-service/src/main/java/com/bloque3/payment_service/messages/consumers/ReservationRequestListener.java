package com.bloque3.payment_service.messages.consumers;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bloque3.payment_service.controllers.dtos.request.PaymentIntentRequestDTO;
import com.bloque3.payment_service.messages.events.ReservationRequestEvent;
import com.bloque3.payment_service.services.PaymentIntentService;

@Configuration
public class ReservationRequestListener {

    private PaymentIntentService paymentIntentService;

    public ReservationRequestListener(PaymentIntentService paymentIntentService) {
        this.paymentIntentService = paymentIntentService;
    }

    @Bean
    public Consumer<ReservationRequestEvent> reservationRequested(){
        return event-> {
            PaymentIntentRequestDTO paymentIntentRequestDTO = PaymentIntentRequestDTO.builder()
                .reservationId(event.id().toString())
                .amount(event.amount().doubleValue())
                .currency("COP")
                .build();
            paymentIntentService.create(paymentIntentRequestDTO);
        };
    }
}
