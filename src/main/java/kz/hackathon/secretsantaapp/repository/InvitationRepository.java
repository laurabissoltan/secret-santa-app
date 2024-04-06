package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByInvitationCode(String invitationCode);

    List<Invitation> findByGameId(UUID gameId);

    List<Invitation> findByEmailAndStatus(String email, InvitationStatus status);

    Optional<Invitation> findByGameIdAndEmail(UUID gameId, String email);

    Optional<Invitation> findByGameIdAndGroupInvitationTrue(UUID gameId);

}
