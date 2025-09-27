package com.sobow.shopping.domain.user.repo;

import com.sobow.shopping.domain.user.UserAuthority;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<UserAuthority, Long> {
    
    Set<UserAuthority> findAllByUserId(Long userId);
    
}
