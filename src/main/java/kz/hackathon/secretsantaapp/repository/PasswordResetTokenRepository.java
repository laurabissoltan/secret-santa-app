package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);

}
