package com.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"order", "courier"})
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private User courier;

    private String originAddress;
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "estimated_time")
    private Integer estimatedTimeMinutes;

    @Column(name = "delivery_value")
    private BigDecimal fee;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    public void start() {
        this.status = DeliveryStatus.PENDENTE;
        this.createdAt = LocalDateTime.now();
    }
}