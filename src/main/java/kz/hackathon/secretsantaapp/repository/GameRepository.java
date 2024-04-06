package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByCreatorId(UUID creatorId);

}
