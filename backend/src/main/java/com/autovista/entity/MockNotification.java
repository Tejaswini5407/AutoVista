package com.autovista.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "mock_notifications")
public class MockNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private TestDriveBooking booking;

    @Column(name = "booking_ref", nullable = false, length = 50)
    private String bookingRef;

    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Column(name = "recipient_email", length = 150)
    private String recipientEmail;

    @Column(name = "recipient_phone", length = 50)
    private String recipientPhone;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(nullable = false, length = 30)
    private String channel;

    @Column(length = 255)
    private String subject;

    @Column(name = "message_body", nullable = false, columnDefinition = "TEXT")
    private String messageBody;

    @Column(nullable = false, length = 30)
    private String status = "DELIVERED_SIMULATED";

    @Column(name = "sent_at", nullable = false, updatable = false)
    private OffsetDateTime sentAt = OffsetDateTime.now();

    public MockNotification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TestDriveBooking getBooking() { return booking; }
    public void setBooking(TestDriveBooking booking) { this.booking = booking; }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }
}
