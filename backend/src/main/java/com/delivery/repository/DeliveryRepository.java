package com.delivery.repository;

import com.delivery.model.Delivery;
import com.delivery.model.DeliveryStatus;
import com.delivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByStatusAndCourierIsNull(DeliveryStatus status);
    List<Delivery> findByCourier(User courier);

    @Modifying
    @Query("UPDATE Delivery d SET d.status = 'ACEITA', d.courier = :courier WHERE d.id = :id AND d.status = 'PENDENTE' AND d.courier IS NULL")
    int acceptAtomically(@Param("id") Long id, @Param("courier") User courier);

    Optional<Delivery> findByIdAndCourierIsNull(Long id);
}