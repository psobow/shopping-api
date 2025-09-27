package com.sobow.shopping.domain.user.repo;

import com.sobow.shopping.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN FETCH u.authorities
        WHERE u.email = :email
        """)
    Optional<User> findByEmailWithAuthorities(String email);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String newEmail, long id);
}
