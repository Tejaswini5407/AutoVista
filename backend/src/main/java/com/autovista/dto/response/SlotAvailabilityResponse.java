package com.autovista.dto.response;

import java.time.LocalDate;
import java.util.List;

public class SlotAvailabilityResponse {

    private Long showroomId;
    private String showroomName;
    private LocalDate date;
    private List<String> operationalSlots;
    private List<String> bookedSlots;
    private List<String> availableSlots;

    public SlotAvailabilityResponse() {}

    public SlotAvailabilityResponse(Long showroomId, String showroomName, LocalDate date,
                                    List<String> operationalSlots, List<String> bookedSlots,
                                    List<String> availableSlots) {
        this.showroomId = showroomId;
        this.showroomName = showroomName;
        this.date = date;
        this.operationalSlots = operationalSlots;
        this.bookedSlots = bookedSlots;
        this.availableSlots = availableSlots;
    }

    public Long getShowroomId() { return showroomId; }
    public void setShowroomId(Long showroomId) { this.showroomId = showroomId; }

    public String getShowroomName() { return showroomName; }
    public void setShowroomName(String showroomName) { this.showroomName = showroomName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<String> getOperationalSlots() { return operationalSlots; }
    public void setOperationalSlots(List<String> operationalSlots) { this.operationalSlots = operationalSlots; }

    public List<String> getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(List<String> bookedSlots) { this.bookedSlots = bookedSlots; }

    public List<String> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<String> availableSlots) { this.availableSlots = availableSlots; }
}
