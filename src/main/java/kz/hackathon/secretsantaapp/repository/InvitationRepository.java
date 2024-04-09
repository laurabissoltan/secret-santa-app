package kz.hackathon.secretsantaapp.repository;

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


    Optional<Invitation> findByGameIdAndGroupInvitationTrue(UUID gameId);

    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);


}
