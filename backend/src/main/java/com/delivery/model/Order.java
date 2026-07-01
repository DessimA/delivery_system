package com.delivery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"products", "delivery"})
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    private String status;

    @Column(name = "delivery_fee")
    private Float deliveryFee;

    @Column(name = "total_value")
    private Float totalValue;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Delivery delivery;

    public void calculateTotal() {
        double productsTotal = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        this.totalValue = (float) (productsTotal + (deliveryFee != null ? deliveryFee : 0));
    }
}
