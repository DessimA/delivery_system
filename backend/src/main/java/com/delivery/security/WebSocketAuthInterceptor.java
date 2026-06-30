package com.delivery.security;

import com.delivery.model.Order;
import com.delivery.model.User;
import com.delivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Pattern ORDER_TOPIC_PATTERN = Pattern.compile("^/topic/orders/(\\d+)$");
    private static final Pattern QUEUE_PATTERN = Pattern.compile("^/queue/.*$");

    private final OrderRepository orderRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }

        String destination = accessor.getDestination();
        if (destination == null) {
            return message;
        }

        if (QUEUE_PATTERN.matcher(destination).matches()) {
            return message;
        }

        Matcher matcher = ORDER_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            return message;
        }

        Long orderId = Long.parseLong(matcher.group(1));
        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Authentication required for order topics");
        }

        User user = (User) auth.getPrincipal();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        boolean isCustomer = Objects.equals(order.getCustomerId(), user.getId());
        boolean isCourier = order.getDelivery() != null
                && order.getDelivery().getCourier() != null
                && Objects.equals(order.getDelivery().getCourier().getId(), user.getId());
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!isCustomer && !isCourier && !isAdmin) {
            throw new SecurityException("Not authorized to subscribe to this order topic");
        }

        return message;
    }
}
