package com.bloque3.payment_service.messages.producers;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class PaymentFailedProducer {

    private  StreamBridge streamBridge;

    public PaymentFailedProducer( StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public boolean sendMessage(PaymentFailedProducer event) {
        return streamBridge.send("paymentFailed-out-0", event);
    }
    
}
