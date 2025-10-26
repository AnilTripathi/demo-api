package com.myhealth.repository;

import com.myhealth.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
    Optional<UserToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(UUID userId);
    void deleteByRefreshToken(String refreshToken);
}