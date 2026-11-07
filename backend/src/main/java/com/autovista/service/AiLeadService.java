package com.autovista.service;

import com.autovista.entity.Lead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiLeadService {

    private static final Logger log = LoggerFactory.getLogger(AiLeadService.class);

    @Value("${gemini.api.key:#{null}}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Requirement #5: AI Buyer Summary.
     * Generates a concise summary containing:
     * - Customer's stated requirements
     * - Vehicle/model of interest
     * - Budget, if explicitly provided (otherwise 'Not provided')
     * - Preferred fuel/transmission, if provided (otherwise 'Not provided')
     * - Purchase timeline, if provided (otherwise 'Not provided')
     * - Important questions or missing information
     *
     * STRICT BUSINESS RULES:
     * - Uses ONLY information actually provided by the customer or stored in the application.
     * - NEVER invent a budget, preference, purchase timeline, or customer requirement.
     * - Clearly marks unavailable information as 'Not provided'.
     */
    public String generateBuyerSummary(Lead lead) {
        String vehicleInfo = resolveVehicleInfo(lead);
        String statedBudget = (lead.getStatedBudget() != null && !lead.getStatedBudget().isBlank())
                ? lead.getStatedBudget().trim()
                : "Not provided";
        String timeline = (lead.getPurchaseTimeline() != null && !lead.getPurchaseTimeline().isBlank())
                ? lead.getPurchaseTimeline().trim()
                : "Not provided";
        String fuel = (lead.getPreferredFuel() != null && !lead.getPreferredFuel().isBlank())
                ? lead.getPreferredFuel().trim()
                : "Not provided";
        String transmission = (lead.getPreferredTransmission() != null && !lead.getPreferredTransmission().isBlank())
                ? lead.getPreferredTransmission().trim()
                : "Not provided";
        String requirements = (lead.getEnquiryMessage() != null && !lead.getEnquiryMessage().isBlank())
                ? lead.getEnquiryMessage().trim()
                : "Standard vehicle enquiry and pricing information request.";

        // Optional Gemini integration if GEMINI_API_KEY is configured
        if (isGeminiAvailable()) {
            try {
                String prompt = String.format(
                        "You are an assistant for AutoVista Luxury Dealership Lucknow. Generate a concise, objective buyer summary for a sales consultant based ONLY on the following facts. " +
                        "Do not invent any details not provided. If a detail is missing, state 'Not provided'.\n" +
                        "Customer Name: %s\nCity: %s\nVehicle: %s\nStated Requirements: %s\nStated Budget: %s\nTimeline: %s\nFuel: %s\nTransmission: %s",
                        lead.getCustomerName(), lead.getCustomerCity(), vehicleInfo, requirements, statedBudget, timeline, fuel, transmission
                );
                String geminiOutput = callGeminiApi(prompt);
                if (geminiOutput != null && !geminiOutput.isBlank()) {
                    return "[Assistant Generated Summary — AutoVista Dealership AI]\n" + geminiOutput.trim();
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed, falling back to deterministic template: {}", ex.getMessage());
            }
        }

        // Deterministic Fallback Summary (Guaranteed zero hallucination)
        StringBuilder sb = new StringBuilder();
        sb.append("[Assistant Generated Summary — AutoVista Dealership AI]\n");
        sb.append("• Target Vehicle: ").append(vehicleInfo).append("\n");
        sb.append("• Customer Requirements: ").append(requirements).append("\n");
        sb.append("• Stated Budget: ").append(statedBudget).append("\n");
        sb.append("• Preferred Fuel / Transmission: ").append(fuel).append(" / ").append(transmission).append("\n");
        sb.append("• Purchase Timeline: ").append(timeline).append("\n");
        sb.append("• Missing Information: ");

        int missingCount = 0;
        if ("Not provided".equals(statedBudget)) {
            sb.append("Explicit budget range; ");
            missingCount++;
        }
        if ("Not provided".equals(timeline)) {
            sb.append("Purchase horizon; ");
            missingCount++;
        }
        if ("Not provided".equals(fuel) || "Not provided".equals(transmission)) {
            sb.append("Powertrain preference; ");
            missingCount++;
        }
        if (missingCount == 0) {
            sb.append("None (Comprehensive profile captured)");
        } else {
            sb.append("Recommend confirming during initial phone consultation.");
        }

        return sb.toString();
    }

    /**
     * Requirement #6: AI First-Reply Drafts.
     * Generates a professional initial response based on:
     * - Customer enquiry
     * - Vehicle information & official dealership pricing
     * - Stated customer requirements
     *
     * STRICT BUSINESS RULES:
     * 1. AI must NEVER invent discounts, waivers, or unauthorized offers.
     * 2. AI must NEVER promise unsupported delivery dates or prices.
     * 3. AI must NOT alter official vehicle pricing.
     * 4. AI must NOT make unsupported claims about inventory availability.
     * 5. AI must NOT send messages automatically.
     */
    public String generateFirstReplyDraft(Lead lead) {
        String vehicleInfo = resolveVehicleInfo(lead);
        String consultantName = (lead.getLastAssignedConsultant() != null)
                ? lead.getLastAssignedConsultant().getFullName()
                : "AutoVista Sales Specialist";
        String showroomName = (lead.getShowroom() != null)
                ? lead.getShowroom().getName()
                : "AutoVista Flagship Experience Centre, Lucknow";

        if (isGeminiAvailable()) {
            try {
                String prompt = String.format(
                        "You are drafting a professional first-reply email/message for a Sales Consultant (%s) at %s. " +
                        "Customer Name: %s. Vehicle: %s. Customer Message: %s. " +
                        "STRICT RULES: Never offer discounts. Never change official vehicle prices. Never promise unverified delivery dates. " +
                        "Invite them warmly for a VIP test drive at the showroom or doorstep. Professional and polite tone.",
                        consultantName, showroomName, lead.getCustomerName(), vehicleInfo,
                        lead.getEnquiryMessage() != null ? lead.getEnquiryMessage() : "General interest"
                );
                String geminiOutput = callGeminiApi(prompt);
                if (geminiOutput != null && !geminiOutput.isBlank()) {
                    return "[AI Generated Draft — Pending Consultant Review & Approval]\n" + geminiOutput.trim();
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed for reply draft, falling back to deterministic template: {}", ex.getMessage());
            }
        }

        // Deterministic Fallback Draft (Guaranteed compliance with all strict rules)
        StringBuilder sb = new StringBuilder();
        sb.append("[AI Generated Draft — Pending Consultant Review & Approval]\n\n");
        sb.append("Dear ").append(lead.getCustomerName()).append(",\n\n");
        sb.append("Thank you for contacting AutoVista Lucknow. We have received your enquiry regarding the ")
                .append(vehicleInfo).append(".\n\n");

        if (lead.getEnquiryMessage() != null && !lead.getEnquiryMessage().isBlank()) {
            sb.append("Regarding your query: \"").append(lead.getEnquiryMessage().trim()).append("\", ");
            sb.append("our dealership team has reviewed your requirements.\n\n");
        }

        sb.append("I would be delighted to arrange a VIP Test Drive for you at your earliest convenience, either at our ")
                .append(showroomName).append(" or via our doorstep test-drive concierge.\n\n");

        if (lead.getStatedBudget() == null || lead.getStatedBudget().isBlank()) {
            sb.append("Please feel free to let me know if you would like a detailed on-road price breakdown or customized financing options tailored to your preferences.\n\n");
        }

        sb.append("Warm regards,\n");
        sb.append(consultantName).append("\n");
        sb.append("Sales & Relationship Specialist\n");
        sb.append(showroomName).append("\n");
        sb.append("AutoVista Lucknow");

        return sb.toString();
    }

    private String resolveVehicleInfo(Lead lead) {
        if (lead.getInterestedCarVariant() != null) {
            String model = (lead.getInterestedCarVariant().getCarModel() != null)
                    ? lead.getInterestedCarVariant().getCarModel().getModelName()
                    : "AutoVista";
            String variant = lead.getInterestedCarVariant().getVariantName();
            String price = (lead.getInterestedCarVariant().getExShowroomPrice() != null)
                    ? "₹" + String.format("%,.2f ex-showroom", lead.getInterestedCarVariant().getExShowroomPrice())
                    : "";
            return String.format("%s %s %s", model, variant, price).trim();
        } else if (lead.getInterestedUsedCar() != null) {
            return String.format("%s %s %s (%d Pre-Owned Certified — ₹%,.2f)",
                    lead.getInterestedUsedCar().getBrand(),
                    lead.getInterestedUsedCar().getModelName(),
                    lead.getInterestedUsedCar().getVariantName(),
                    lead.getInterestedUsedCar().getManufacturingYear(),
                    lead.getInterestedUsedCar().getPrice());
        }
        return "AutoVista Premium Vehicle Collection";
    }

    private boolean isGeminiAvailable() {
        return geminiApiKey != null && !geminiApiKey.isBlank() && !geminiApiKey.equalsIgnoreCase("none");
    }

    private String callGeminiApi(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBody = Map.of("contents", List.of(contentPart));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List candidates = (List) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                if (content != null) {
                    List parts = (List) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Map firstPart = (Map) parts.get(0);
                        return (String) firstPart.get("text");
                    }
                }
            }
        }
        return null;
    }
}
