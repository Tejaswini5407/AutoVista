package com.autovista.repository;

import com.autovista.entity.MockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockNotificationRepository extends JpaRepository<MockNotification, Long> {

    List<MockNotification> findByBookingIdOrderBySentAtDesc(Long bookingId);

    List<MockNotification> findByBookingRefOrderBySentAtDesc(String bookingRef);

    List<MockNotification> findByRecipientEmailOrderBySentAtDesc(String recipientEmail);

    List<MockNotification> findAllByOrderBySentAtDesc();
}
