package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameUserRepository extends JpaRepository<GameUser, UUID> {
    List<GameUser> findByGameId(UUID gameId);
    Optional<GameUser> findByGameIdAndUserId(UUID gameId, UUID userId);

    int countByGameId(UUID gameId);

}
