package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByCreatorId(UUID creatorId);

}
