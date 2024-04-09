package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByCreatorId(UUID creatorId);

    @Modifying
    @Query("DELETE FROM Game g WHERE g.creator.id = :userId")
    void deleteByCreatorId(@Param("userId") UUID userId);

}
