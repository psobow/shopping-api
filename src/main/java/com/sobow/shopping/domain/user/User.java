package com.sobow.shopping.domain.user;

import com.sobow.shopping.domain.user.requests.UserUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public User withAuthorities(Set<UserAuthority> authorities) {
        authorities.forEach(this::addAuthorityAndLink);
        return this;
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    // ---- Associations --------------------------------------
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserAuthority> authorities = new HashSet<>();
    
    // ---- Domain methods ------------------------------------
    public void updateFrom(UserUpdateRequest patch) {
        Objects.requireNonNull(patch, "User patch must not be null");
        
        if (patch.userProfile() != null) profile.updateFrom(patch.userProfile());
        
        if (patch.authorities() != null) {
            this.removeAllAuthorities();
            patch.authorities()
                 .stream()
                 .map(authRequest -> new UserAuthority(authRequest.authority()))
                 .forEach(this::addAuthorityAndLink);
        }
    }
    
    public void setProfileAndLink(UserProfile userProfile) {
        this.profile = userProfile;
        userProfile.linkTo(this);
    }
    
    public void removeProfile() {
        this.profile = null;
    }
    
    public void addAuthorityAndLink(UserAuthority userAuthority) {
        authorities.add(userAuthority);
        userAuthority.linkTo(this);
    }
    
    public void removeAuthority(UserAuthority userAuthority) {
        authorities.remove(userAuthority);
    }
    
    public void removeAllAuthorities() {
        for (UserAuthority authority : new HashSet<>(authorities)) {
            removeAuthority(authority);
        }
    }
    
    // ---- Setter methods ------------------------------------
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // ---- Equality (proxy-safe) -----------------------------
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass =
            o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                                           : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }
    
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
               ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
