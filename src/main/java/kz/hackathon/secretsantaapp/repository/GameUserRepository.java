package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameUserRepository extends JpaRepository<GameUser, UUID> {
    List<GameUser> findByGameId(UUID gameId);
    Optional<GameUser> findByGameIdAndUserId(UUID gameId, UUID userId);

    int countByGameId(UUID gameId);


    @Query("SELECT COUNT(gu) FROM GameUser gu WHERE gu.game.id = :gameId AND gu.invitationStatus = :status")
    long countByGameIdAndInvitationStatus(UUID gameId, InvitationStatus status);
   // int countByGameIdAndInvitationStatus(UUID gameId, InvitationStatus invitationStatus);

    List<GameUser> findByGameIdAndInvitationStatus(UUID gameId, InvitationStatus invitationStatus);
}
