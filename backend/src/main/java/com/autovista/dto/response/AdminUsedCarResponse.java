package com.autovista.dto.response;

public class AdminUsedCarResponse extends UsedCarResponse {

    private String rawRegistrationNumber;

    public AdminUsedCarResponse() {}

    public String getRawRegistrationNumber() { return rawRegistrationNumber; }
    public void setRawRegistrationNumber(String rawRegistrationNumber) { this.rawRegistrationNumber = rawRegistrationNumber; }
}
