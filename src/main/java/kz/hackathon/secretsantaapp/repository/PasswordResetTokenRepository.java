package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.password.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.user.id = :userId")
    void deletePasswordResetTokenByUserId(@Param("userId") UUID userId);

    List<PasswordResetToken> findAllByUserId(UUID userId);
  //  void delete(Optional<PasswordResetToken> existingToken);
    List<PasswordResetToken> findAllByExpiryDateBefore(LocalDateTime now);
}
