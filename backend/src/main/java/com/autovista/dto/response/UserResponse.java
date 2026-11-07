package com.autovista.dto.response;

public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String city;

    public UserResponse() {}

    public UserResponse(Long id, String email, String fullName, String phone, String role, String city) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.city = city;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
