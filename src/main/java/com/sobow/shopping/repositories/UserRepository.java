package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.entities.User;
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
        WHERE u.username = :username
        """)
    Optional<User> findByUsernameWithAuthorities(String username);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
