package com.bloque3.payment_service.messages.producers;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.bloque3.payment_service.messages.events.PaymentAuthorizedEvent;

@Service
public class PaymentAuthorizedProducer {

    private StreamBridge streamBridge;

    public PaymentAuthorizedProducer( StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public boolean sendMessage(PaymentAuthorizedEvent event) {
        return streamBridge.send("paymentAuthorized-out-0", event);
    }

}
