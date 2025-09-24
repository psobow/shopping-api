package com.sobow.shopping.domain.user;

import com.sobow.shopping.domain.user.requests.shared.UpdateUserAddressDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_addresses")
public class UserAddress {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public UserAddress(String cityName, String streetName, String streetNumber, String postCode) {
        this.cityName = cityName;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.postCode = postCode;
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    
    public void updateFrom(UpdateUserAddressDto patch) {
        Objects.requireNonNull(patch, "User address patch must not be null");
        if (patch.cityName() != null) this.cityName = patch.cityName();
        if (patch.streetName() != null) this.streetName = patch.streetName();
        if (patch.streetNumber() != null) this.streetNumber = patch.streetNumber();
        if (patch.postCode() != null) this.postCode = patch.postCode();
    }
}
