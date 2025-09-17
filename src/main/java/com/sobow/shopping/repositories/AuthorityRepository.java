package com.sobow.shopping.repositories;

import com.sobow.shopping.domain.entities.UserAuthority;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<UserAuthority, Long> {
    
    Set<UserAuthority> findAllByUserId(Long userId);
    
    void deleteAllByUserId(Long userId);
}
