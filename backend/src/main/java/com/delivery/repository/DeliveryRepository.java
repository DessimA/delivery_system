package com.delivery.repository;

import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByStatusAndCourierIsNull(DeliveryStatus status);
    List<Delivery> findByCourier(User courier);
}