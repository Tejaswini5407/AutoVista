package com.autovista.mapper;

import com.autovista.dto.response.*;
import com.autovista.entity.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarMapper {

    /**
     * Mandatory Requirement #7: Mask vehicle registration numbers.
     * Raw registration numbers MUST NEVER be exposed in unmasked API responses.
     * e.g., "UP32 BK 8921" -> "UP32 •• ••••"
     */
    public String maskRegistrationNumber(String raw) {
        if (raw == null || raw.isBlank()) {
            return "UP32 •• ••••";
        }
        String clean = raw.trim();
        String[] parts = clean.split("[\\s-]+");
        if (parts.length >= 4) {
            return parts[0] + parts[1] + " •• ••••";
        }
        if (clean.length() > 4) {
            return clean.substring(0, 4) + " •• ••••";
        }
        return clean + " •• ••••";
    }

    public CarVariantResponse toVariantResponse(CarVariant variant) {
        if (variant == null) return null;
        CarVariantResponse res = new CarVariantResponse();
        res.setId(variant.getId());
        if (variant.getCarModel() != null) {
            res.setCarModelId(variant.getCarModel().getId());
            res.setBrand(variant.getCarModel().getBrand());
            res.setModelName(variant.getCarModel().getModelName());
            res.setBodyType(variant.getCarModel().getBodyType());
            res.setYear(variant.getCarModel().getYear());
            res.setHeroImageUrl(variant.getCarModel().getHeroImageUrl());
        }
        res.setVariantName(variant.getVariantName());
        res.setFuelType(variant.getFuelType());
        res.setTransmission(variant.getTransmission());
        res.setEngineSpecs(variant.getEngineSpecs());
        res.setPowerSpecs(variant.getPowerSpecs());
        res.setTorqueSpecs(variant.getTorqueSpecs());
        res.setSeatingCapacity(variant.getSeatingCapacity());
        res.setExShowroomPrice(variant.getExShowroomPrice());
        res.setTagline(variant.getTagline());
        res.setFeatures(parseCommaSeparated(variant.getFeatures()));
        res.setColors(parseCommaSeparated(variant.getColors()));
        res.setIsAvailable(variant.getIsAvailable());
        return res;
    }

    public CarModelResponse toModelResponse(CarModel model) {
        if (model == null) return null;
        CarModelResponse res = new CarModelResponse();
        res.setId(model.getId());
        res.setBrand(model.getBrand());
        res.setModelName(model.getModelName());
        res.setBodyType(model.getBodyType());
        res.setYear(model.getYear());
        res.setDescription(model.getDescription());
        res.setHeroImageUrl(model.getHeroImageUrl());
        res.setIsActive(model.getIsActive());
        if (model.getVariants() != null) {
            res.setVariants(model.getVariants().stream()
                    .map(this::toVariantResponse)
                    .collect(Collectors.toList()));
        } else {
            res.setVariants(Collections.emptyList());
        }
        return res;
    }

    public UsedCarResponse toUsedCarResponse(UsedCar usedCar) {
        if (usedCar == null) return null;
        UsedCarResponse res = new UsedCarResponse();
        res.setId(usedCar.getId());
        if (usedCar.getShowroom() != null) {
            res.setShowroomId(usedCar.getShowroom().getId());
            res.setShowroomName(usedCar.getShowroom().getName());
            res.setShowroomCity(usedCar.getShowroom().getCity());
        }
        res.setBrand(usedCar.getBrand());
        res.setModelName(usedCar.getModelName());
        res.setVariantName(usedCar.getVariantName());
        res.setManufacturingYear(usedCar.getManufacturingYear());
        res.setPrice(usedCar.getPrice());
        res.setOdometerKm(usedCar.getOdometerKm());

        // Ensure registration is masked
        res.setMaskedRegistrationNumber(maskRegistrationNumber(usedCar.getRawRegistrationNumber()));

        res.setFuelType(usedCar.getFuelType());
        res.setTransmission(usedCar.getTransmission());
        res.setOwnersCount(usedCar.getOwnersCount());
        res.setInsuranceValidity(usedCar.getInsuranceValidity());
        res.setInspectionPointsPassed(usedCar.getInspectionPointsPassed());
        res.setStatus(usedCar.getStatus() != null ? usedCar.getStatus().name() : "AVAILABLE");
        res.setHeroImageUrl(usedCar.getHeroImageUrl());
        res.setFeatures(parseCommaSeparated(usedCar.getFeatures()));
        res.setTagline(usedCar.getTagline());
        return res;
    }

    public com.autovista.dto.response.AdminUsedCarResponse toAdminUsedCarResponse(UsedCar usedCar) {
        if (usedCar == null) return null;
        UsedCarResponse base = toUsedCarResponse(usedCar);
        com.autovista.dto.response.AdminUsedCarResponse res = new com.autovista.dto.response.AdminUsedCarResponse();
        res.setId(base.getId());
        res.setShowroomId(base.getShowroomId());
        res.setShowroomName(base.getShowroomName());
        res.setShowroomCity(base.getShowroomCity());
        res.setBrand(base.getBrand());
        res.setModelName(base.getModelName());
        res.setVariantName(base.getVariantName());
        res.setManufacturingYear(base.getManufacturingYear());
        res.setPrice(base.getPrice());
        res.setOdometerKm(base.getOdometerKm());
        res.setMaskedRegistrationNumber(base.getMaskedRegistrationNumber());
        res.setFuelType(base.getFuelType());
        res.setTransmission(base.getTransmission());
        res.setOwnersCount(base.getOwnersCount());
        res.setInsuranceValidity(base.getInsuranceValidity());
        res.setInspectionPointsPassed(base.getInspectionPointsPassed());
        res.setStatus(base.getStatus());
        res.setHeroImageUrl(base.getHeroImageUrl());
        res.setFeatures(base.getFeatures());
        res.setTagline(base.getTagline());
        res.setRawRegistrationNumber(usedCar.getRawRegistrationNumber());
        return res;
    }

    public CityPriceResponse toCityPriceResponse(CityPrice price) {
        if (price == null) return null;
        CityPriceResponse res = new CityPriceResponse();
        res.setId(price.getId());
        if (price.getCarVariant() != null) {
            res.setCarVariantId(price.getCarVariant().getId());
            res.setVariantName(price.getCarVariant().getVariantName());
            res.setExShowroomPrice(price.getCarVariant().getExShowroomPrice());
        }
        res.setCity(price.getCity());
        res.setState(price.getState());
        res.setRtoTaxPercent(price.getRtoTaxPercent());
        res.setRtoTaxAmount(price.getRtoTaxAmount());
        res.setInsuranceAmount(price.getInsuranceAmount());
        res.setAccessoriesAmount(price.getAccessoriesAmount());
        res.setHandlingCharges(price.getHandlingCharges());
        res.setTcsAmount(price.getTcsAmount());
        res.setTotalOnRoadPrice(price.getTotalOnRoadPrice());
        res.setPriceValidUntil(price.getPriceValidUntil());
        res.setDisclaimer(price.getDisclaimer());
        return res;
    }

    public com.autovista.dto.response.MockNotificationResponse toNotificationResponse(com.autovista.entity.MockNotification notif) {
        if (notif == null) return null;
        com.autovista.dto.response.MockNotificationResponse res = new com.autovista.dto.response.MockNotificationResponse();
        res.setId(notif.getId());
        res.setBookingRef(notif.getBookingRef());
        res.setRecipientName(notif.getRecipientName());
        res.setRecipientEmail(notif.getRecipientEmail());
        res.setRecipientPhone(notif.getRecipientPhone());
        res.setNotificationType(notif.getNotificationType());
        res.setChannel(notif.getChannel());
        res.setSubject(notif.getSubject());
        res.setMessageBody(notif.getMessageBody());
        res.setStatus(notif.getStatus());
        res.setSentAt(notif.getSentAt());
        return res;
    }

    public com.autovista.dto.response.BookingResponse toBookingResponse(com.autovista.entity.TestDriveBooking booking) {
        if (booking == null) return null;
        com.autovista.dto.response.BookingResponse res = new com.autovista.dto.response.BookingResponse();
        res.setId(booking.getId());
        res.setBookingRef(booking.getBookingRef());

        if (booking.getShowroom() != null) {
            res.setShowroomId(booking.getShowroom().getId());
            res.setShowroomName(booking.getShowroom().getName());
            res.setShowroomCity(booking.getShowroom().getCity());
            res.setShowroomAddress(booking.getShowroom().getAddress());
        }

        if (booking.getCarVariant() != null) {
            res.setVehicleType("NEW");
            res.setCarVariantId(booking.getCarVariant().getId());
            if (booking.getCarVariant().getCarModel() != null) {
                res.setVehicleBrand(booking.getCarVariant().getCarModel().getBrand());
                res.setVehicleModel(booking.getCarVariant().getCarModel().getModelName());
                res.setVehicleYear(booking.getCarVariant().getCarModel().getYear());
            }
            res.setVehicleVariant(booking.getCarVariant().getVariantName());
            res.setVehicleDisplayName(String.format("%s %s %s (%d New)",
                    res.getVehicleBrand() != null ? res.getVehicleBrand() : "",
                    res.getVehicleModel() != null ? res.getVehicleModel() : "",
                    res.getVehicleVariant() != null ? res.getVehicleVariant() : "",
                    res.getVehicleYear() != null ? res.getVehicleYear() : 2026).trim());
        } else if (booking.getUsedCar() != null) {
            res.setVehicleType("USED");
            res.setUsedCarId(booking.getUsedCar().getId());
            res.setVehicleBrand(booking.getUsedCar().getBrand());
            res.setVehicleModel(booking.getUsedCar().getModelName());
            res.setVehicleVariant(booking.getUsedCar().getVariantName());
            res.setVehicleYear(booking.getUsedCar().getManufacturingYear());
            res.setVehicleDisplayName(String.format("%s %s %s (%d Pre-Owned)",
                    res.getVehicleBrand(),
                    res.getVehicleModel(),
                    res.getVehicleVariant(),
                    res.getVehicleYear()).trim());
        }

        res.setCustomerName(booking.getCustomerName());
        res.setCustomerPhone(booking.getCustomerPhone());
        res.setCustomerEmail(booking.getCustomerEmail());
        res.setBookingDate(booking.getBookingDate());
        res.setTimeSlot(booking.getTimeSlot());
        res.setDriveMode(booking.getDriveMode());
        res.setDoorstepAddress(booking.getDoorstepAddress());
        res.setConsentGiven(booking.getConsentGiven());
        res.setStatus(booking.getStatus());
        res.setCreatedAt(booking.getCreatedAt());
        res.setUpdatedAt(booking.getUpdatedAt());

        return res;
    }

    public com.autovista.dto.response.LeadResponse toLeadResponse(com.autovista.entity.Lead lead) {
        if (lead == null) return null;
        com.autovista.dto.response.LeadResponse res = new com.autovista.dto.response.LeadResponse();
        res.setId(lead.getId());
        res.setLeadRef(lead.getLeadRef());
        if (lead.getUser() != null) {
            res.setUserId(lead.getUser().getId());
        }
        if (lead.getShowroom() != null) {
            res.setShowroomId(lead.getShowroom().getId());
            res.setShowroomName(lead.getShowroom().getName());
            res.setShowroomCity(lead.getShowroom().getCity());
        }
        res.setCustomerName(lead.getCustomerName());
        res.setCustomerPhone(lead.getCustomerPhone());
        res.setCustomerEmail(lead.getCustomerEmail());
        res.setCustomerCity(lead.getCustomerCity());

        if (lead.getInterestedCarVariant() != null) {
            res.setCarVariantId(lead.getInterestedCarVariant().getId());
            if (lead.getInterestedCarVariant().getCarModel() != null) {
                res.setCarModelName(lead.getInterestedCarVariant().getCarModel().getModelName());
            }
            res.setCarVariantName(lead.getInterestedCarVariant().getVariantName());
            if (lead.getInterestedCarVariant().getExShowroomPrice() != null) {
                res.setVehiclePriceFormatted("₹" + String.format("%,.2f", lead.getInterestedCarVariant().getExShowroomPrice()));
            }
        } else if (lead.getInterestedUsedCar() != null) {
            res.setUsedCarId(lead.getInterestedUsedCar().getId());
            res.setUsedCarTitle(String.format("%s %s %s (%d Pre-Owned)",
                    lead.getInterestedUsedCar().getBrand(),
                    lead.getInterestedUsedCar().getModelName(),
                    lead.getInterestedUsedCar().getVariantName(),
                    lead.getInterestedUsedCar().getManufacturingYear()));
            if (lead.getInterestedUsedCar().getPrice() != null) {
                res.setUsedCarPriceFormatted("₹" + String.format("%,.2f", lead.getInterestedUsedCar().getPrice()));
            }
        }

        res.setEnquiryType(lead.getEnquiryType());
        res.setEnquiryMessage(lead.getEnquiryMessage());
        res.setStatedBudget(lead.getStatedBudget());
        res.setPurchaseTimeline(lead.getPurchaseTimeline());
        res.setPreferredFuel(lead.getPreferredFuel());
        res.setPreferredTransmission(lead.getPreferredTransmission());
        res.setConsentGiven(lead.getConsentGiven());
        res.setOptOut(lead.getOptOut());
        res.setStatus(lead.getStatus());

        if (lead.getLastAssignedConsultant() != null) {
            res.setAssignedConsultantId(lead.getLastAssignedConsultant().getId());
            res.setAssignedConsultantName(lead.getLastAssignedConsultant().getFullName());
            res.setAssignedConsultantEmail(lead.getLastAssignedConsultant().getEmail());
        }
        res.setAssignedAt(lead.getAssignedAt());
        if (lead.getAssignedAt() != null) {
            res.setResponseDueBy(lead.getAssignedAt().plusMinutes(5));
        }

        res.setAiSummary(lead.getAiSummary());
        res.setAiDraftReply(lead.getAiDraftReply());
        res.setConsultantApproved(lead.getConsultantApproved());
        res.setSentVia(lead.getSentVia());

        if (lead.getStatusHistory() != null && !lead.getStatusHistory().isEmpty()) {
            res.setStatusHistory(lead.getStatusHistory().stream()
                    .map(this::toLeadStatusHistoryResponse)
                    .collect(Collectors.toList()));
        }

        if (lead.getCommunications() != null && !lead.getCommunications().isEmpty()) {
            res.setCommunications(lead.getCommunications().stream()
                    .map(this::toLeadCommunicationResponse)
                    .collect(Collectors.toList()));
        }

        res.setCreatedAt(lead.getCreatedAt());
        res.setUpdatedAt(lead.getUpdatedAt());
        return res;
    }

    public LeadStatusHistoryResponse toLeadStatusHistoryResponse(LeadStatusHistory history) {
        if (history == null) return null;
        LeadStatusHistoryResponse res = new LeadStatusHistoryResponse();
        res.setId(history.getId());
        if (history.getLead() != null) {
            res.setLeadId(history.getLead().getId());
        }
        if (history.getChangedByUser() != null) {
            res.setChangedByUserId(history.getChangedByUser().getId());
            res.setChangedByUserName(history.getChangedByUser().getFullName());
        }
        res.setFromStatus(history.getFromStatus());
        res.setToStatus(history.getToStatus());
        res.setNotes(history.getNotes());
        res.setCreatedAt(history.getCreatedAt());
        return res;
    }

    public LeadCommunicationResponse toLeadCommunicationResponse(LeadCommunication comm) {
        if (comm == null) return null;
        LeadCommunicationResponse res = new LeadCommunicationResponse();
        res.setId(comm.getId());
        if (comm.getLead() != null) {
            res.setLeadId(comm.getLead().getId());
        }
        if (comm.getConsultant() != null) {
            res.setConsultantId(comm.getConsultant().getId());
            res.setConsultantName(comm.getConsultant().getFullName());
        }
        res.setDraftContent(comm.getDraftContent());
        res.setApprovedContent(comm.getApprovedContent());
        res.setChannel(comm.getChannel());
        res.setDeliveryStatus(comm.getDeliveryStatus());
        res.setIsApproved(comm.getIsApproved());
        res.setApprovedAt(comm.getApprovedAt());
        res.setSentAt(comm.getSentAt());
        return res;
    }

    public LeadAssignmentResponse toLeadAssignmentResponse(LeadAssignment assignment) {
        if (assignment == null) return null;
        LeadAssignmentResponse res = new LeadAssignmentResponse();
        res.setId(assignment.getId());
        if (assignment.getLead() != null) {
            res.setLeadId(assignment.getLead().getId());
            res.setLeadRef(assignment.getLead().getLeadRef());
        }
        if (assignment.getConsultant() != null) {
            res.setConsultantId(assignment.getConsultant().getId());
            res.setConsultantName(assignment.getConsultant().getFullName());
            res.setConsultantEmail(assignment.getConsultant().getEmail());
        }
        res.setAssignedAt(assignment.getAssignedAt());
        res.setResponseDueBy(assignment.getResponseDueBy());
        res.setRespondedAt(assignment.getRespondedAt());
        res.setSlaMet(assignment.getSlaMet());
        res.setStatus(assignment.getStatus());
        res.setNotes(assignment.getNotes());
        return res;
    }

    private List<String> parseCommaSeparated(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
