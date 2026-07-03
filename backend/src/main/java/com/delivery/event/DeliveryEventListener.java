package com.delivery.event;

import com.delivery.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final DeliveryMapper deliveryMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeliveryUpdated(DeliveryUpdatedEvent event) {
        if (event.delivery().getOrder() != null) {
            String topic = "/topic/orders/" + event.delivery().getOrder().getId();
            messagingTemplate.convertAndSend(topic, deliveryMapper.toResponseDTO(event.delivery()));
        }
    }
}
