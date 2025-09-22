package com.sobow.shopping.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Locale;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "user_authorities",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uc_auth_user_authority",
            columnNames = {"user_id", "authority"}
        )
    }
)
public class UserAuthority {
    
    // ---- Construction (builder) ----------------------------
    @Builder
    public UserAuthority(String authority) {
        setAuthority(authority);
    }
    
    // ---- Identifier & Basic columns ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, updatable = false)
    private String authority;
    
    // ---- Associations --------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
    
    // ---- Domain methods ------------------------------------
    public void linkTo(User user) {
        this.user = user;
    }
    
    // ---- Setter methods ------------------------------------
    public void setAuthority(String authority) {
        String a = authority.trim().toUpperCase(Locale.ROOT);
        if (a.startsWith("ROLE_")) a = a.substring(5);
        this.authority = "ROLE_" + a;
    }
    
    // ---- Equality (proxy-safe) -----------------------------
    // Multiple UserAuthority objects are placed inside Set collection before persistance inside UserCreateRequestMapper.
    // We can't rely on ID.
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                                   ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                                   : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                                      ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                                      : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        
        UserAuthority that = (UserAuthority) o;
        return Objects.equals(this.authority, that.authority);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(this.authority);
    }
}
