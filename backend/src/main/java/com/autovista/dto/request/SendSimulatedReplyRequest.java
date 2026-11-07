package com.autovista.dto.request;

public class SendSimulatedReplyRequest {

    private String channel = "WHATSAPP_SIMULATED"; // WHATSAPP_SIMULATED, EMAIL_SIMULATED, SMS_SIMULATED

    public SendSimulatedReplyRequest() {}

    public SendSimulatedReplyRequest(String channel) {
        this.channel = channel;
    }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
