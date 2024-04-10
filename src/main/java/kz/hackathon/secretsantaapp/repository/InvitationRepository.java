package kz.hackathon.secretsantaapp.repository;

import jakarta.transaction.Transactional;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByInvitationCode(String invitationCode);

    List<Invitation> findByGameId(UUID gameId);

    Optional<Invitation> findByGameIdAndGroupInvitationTrue(UUID gameId);


    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Invitation i WHERE i.game.id = :gameId")
    void deleteByGameId(@Param("gameId") UUID gameId);


}
