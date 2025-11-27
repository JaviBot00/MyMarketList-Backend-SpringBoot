package com.hotguy.mymarketlist.dto;

import java.util.List;

public class FamilyAccountResponse {
    private Long id;
    private String email;
    private String password;
    private List<ProfileResponse> profiles;

    // Constructor por defecto requerido por Jackson
    public void FamilyAccountRequest() {
    }

    public void FamilyAccountRequest(FamilyAccountResponse fA) {
        this.id = fA.getId();
        this.email = fA.getEmail();
        this.password = "********";
        this.profiles = fA.getProfiles();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ProfileResponse> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileResponse> profiles) {
        this.profiles = profiles;
    }
}