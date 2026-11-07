package com.autovista.service;

import com.autovista.dto.response.MockNotificationResponse;
import com.autovista.entity.MockNotification;
import com.autovista.entity.TestDriveBooking;
import com.autovista.mapper.CarMapper;
import com.autovista.repository.MockNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MockNotificationService {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationService.class);

    private final MockNotificationRepository notificationRepository;
    private final CarMapper carMapper;

    public MockNotificationService(MockNotificationRepository notificationRepository, CarMapper carMapper) {
        this.notificationRepository = notificationRepository;
        this.carMapper = carMapper;
    }

    public List<MockNotificationResponse> dispatchBookingConfirmation(TestDriveBooking booking, String vehicleDisplayName) {
        List<MockNotification> notifications = new ArrayList<>();

        // 1. Simulated SMS
        MockNotification sms = new MockNotification();
        sms.setBooking(booking);
        sms.setBookingRef(booking.getBookingRef());
        sms.setRecipientName(booking.getCustomerName());
        sms.setRecipientPhone(booking.getCustomerPhone());
        sms.setRecipientEmail(booking.getCustomerEmail());
        sms.setNotificationType("BOOKING_CONFIRMED");
        sms.setChannel("SMS_SIMULATED");
        sms.setSubject("AutoVista VIP Test Drive Confirmed");
        sms.setMessageBody(String.format(
                "[AutoVista SIMULATED SMS] Dear %s, your VIP Test Drive appointment (Ref: %s) for %s is CONFIRMED on %s at %s. Showroom: %s. Thank you for choosing AutoVista.",
                booking.getCustomerName(),
                booking.getBookingRef(),
                vehicleDisplayName,
                booking.getBookingDate(),
                booking.getTimeSlot(),
                booking.getShowroom() != null ? booking.getShowroom().getName() : "AutoVista Lucknow Flagship"
        ));
        sms.setStatus("DELIVERED_SIMULATED");
        notifications.add(notificationRepository.save(sms));

        // 2. Simulated Email
        MockNotification email = new MockNotification();
        email.setBooking(booking);
        email.setBookingRef(booking.getBookingRef());
        email.setRecipientName(booking.getCustomerName());
        email.setRecipientPhone(booking.getCustomerPhone());
        email.setRecipientEmail(booking.getCustomerEmail());
        email.setNotificationType("BOOKING_CONFIRMED");
        email.setChannel("EMAIL_SIMULATED");
        email.setSubject("AutoVista VIP Test Drive Confirmation - " + booking.getBookingRef());
        email.setMessageBody(String.format(
                "[AutoVista SIMULATED EMAIL]\nDear %s,\n\nWe are delighted to confirm your VIP Test Drive appointment.\n\n" +
                "• Booking Reference: %s\n" +
                "• Vehicle: %s\n" +
                "• Date & Time: %s at %s\n" +
                "• Format: %s%s\n" +
                "• Facility: %s\n\n" +
                "A senior sales specialist has been assigned to your booking. Please carry a valid driving license.\n\nWarm regards,\nAutoVista Concierge Team",
                booking.getCustomerName(),
                booking.getBookingRef(),
                vehicleDisplayName,
                booking.getBookingDate(),
                booking.getTimeSlot(),
                booking.getDriveMode(),
                "DOORSTEP".equalsIgnoreCase(booking.getDriveMode()) ? " (Address: " + booking.getDoorstepAddress() + ")" : "",
                booking.getShowroom() != null ? booking.getShowroom().getName() + ", " + booking.getShowroom().getAddress() : "Shaheed Path, Gomti Nagar, Lucknow"
        ));
        email.setStatus("DELIVERED_SIMULATED");
        notifications.add(notificationRepository.save(email));

        // 3. Simulated WhatsApp
        MockNotification whatsapp = new MockNotification();
        whatsapp.setBooking(booking);
        whatsapp.setBookingRef(booking.getBookingRef());
        whatsapp.setRecipientName(booking.getCustomerName());
        whatsapp.setRecipientPhone(booking.getCustomerPhone());
        whatsapp.setRecipientEmail(booking.getCustomerEmail());
        whatsapp.setNotificationType("BOOKING_CONFIRMED");
        whatsapp.setChannel("WHATSAPP_SIMULATED");
        whatsapp.setSubject("AutoVista WhatsApp Alert");
        whatsapp.setMessageBody(String.format(
                "[AutoVista SIMULATED WHATSAPP] Namaste %s! Your test drive for %s is locked for %s at %s (Ref: %s). AutoVista looks forward to welcoming you.",
                booking.getCustomerName(),
                vehicleDisplayName,
                booking.getBookingDate(),
                booking.getTimeSlot(),
                booking.getBookingRef()
        ));
        whatsapp.setStatus("DELIVERED_SIMULATED");
        notifications.add(notificationRepository.save(whatsapp));

        log.info("[SIMULATED NOTIFICATION] Dispatched 3 simulated notification events for booking Ref: {}", booking.getBookingRef());

        return notifications.stream()
                .map(carMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<MockNotificationResponse> dispatchStatusUpdateNotification(
            TestDriveBooking booking, String oldStatus, String newStatus, String notes) {
        List<MockNotification> notifications = new ArrayList<>();

        String eventType = "STATUS_UPDATED";
        if ("CANCELLED".equalsIgnoreCase(newStatus)) {
            eventType = "BOOKING_CANCELLED";
        } else if ("REJECTED".equalsIgnoreCase(newStatus)) {
            eventType = "BOOKING_REJECTED";
        }

        MockNotification sms = new MockNotification();
        sms.setBooking(booking);
        sms.setBookingRef(booking.getBookingRef());
        sms.setRecipientName(booking.getCustomerName());
        sms.setRecipientPhone(booking.getCustomerPhone());
        sms.setRecipientEmail(booking.getCustomerEmail());
        sms.setNotificationType(eventType);
        sms.setChannel("SMS_SIMULATED");
        sms.setSubject("AutoVista Booking Update: " + newStatus);
        sms.setMessageBody(String.format(
                "[AutoVista SIMULATED SMS] Dear %s, your Test Drive booking (Ref: %s) status has been updated from %s to %s.%s",
                booking.getCustomerName(),
                booking.getBookingRef(),
                oldStatus,
                newStatus,
                notes != null && !notes.isBlank() ? " Note: " + notes : ""
        ));
        sms.setStatus("DELIVERED_SIMULATED");
        notifications.add(notificationRepository.save(sms));

        MockNotification email = new MockNotification();
        email.setBooking(booking);
        email.setBookingRef(booking.getBookingRef());
        email.setRecipientName(booking.getCustomerName());
        email.setRecipientPhone(booking.getCustomerPhone());
        email.setRecipientEmail(booking.getCustomerEmail());
        email.setNotificationType(eventType);
        email.setChannel("EMAIL_SIMULATED");
        email.setSubject("AutoVista Test Drive Status Update - " + booking.getBookingRef());
        email.setMessageBody(String.format(
                "[AutoVista SIMULATED EMAIL]\nDear %s,\n\nYour test drive appointment (Ref: %s) status is now %s.\nDate: %s at %s\n%s\n\nAutoVista Concierge",
                booking.getCustomerName(),
                booking.getBookingRef(),
                newStatus,
                booking.getBookingDate(),
                booking.getTimeSlot(),
                notes != null && !notes.isBlank() ? "Details: " + notes : ""
        ));
        email.setStatus("DELIVERED_SIMULATED");
        notifications.add(notificationRepository.save(email));

        log.info("[SIMULATED NOTIFICATION] Status update notification dispatched for Ref: {} ({})", booking.getBookingRef(), newStatus);

        return notifications.stream()
                .map(carMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MockNotificationResponse> getNotificationsByBookingId(Long bookingId) {
        return notificationRepository.findByBookingIdOrderBySentAtDesc(bookingId).stream()
                .map(carMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MockNotificationResponse> getNotificationsByBookingRef(String bookingRef) {
        return notificationRepository.findByBookingRefOrderBySentAtDesc(bookingRef).stream()
                .map(carMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MockNotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderBySentAtDesc().stream()
                .map(carMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }
}
