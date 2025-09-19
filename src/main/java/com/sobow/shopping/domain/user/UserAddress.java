package com.sobow.shopping.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserAddress {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public UserAddress(String cityName, String streetName, String streetNumber, String postCode) {
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.postCode = postCode;
    }
    
    // ---- Identifier ----------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ---- Basic columns -------------------------------------
    @Column(nullable = false)
    private String cityName;
    
    @Column(nullable = false)
    private String streetName;
    
    @Column(nullable = false)
    private String streetNumber;
    
    @Column(nullable = false)
    private String postCode;
    
    // ---- Associations --------------------------------------
    @OneToOne(optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;
    
    // ---- Domain methods ------------------------------------
    public void linkTo(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
