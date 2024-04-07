package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private EmailService emailService;
    public void sendInvitations(UUID gameId, List<String> emails) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        String groupInvitationLink = generateShareableLink(gameId);

        for (String email : emails) {
            emailService.sendEmail(email, "Ваш приглашают участвовать в игре Secret Santa!",
                    "Пожалуйста перейдите по ссылке для участия: " + groupInvitationLink);
        }
    }

    public String generateShareableLink(UUID gameId) {
        Optional<Invitation> existingGroupInvitation = invitationRepository.findByGameIdAndGroupInvitationTrue(gameId);

        String invitationCode;
        if (existingGroupInvitation.isPresent()) {
            invitationCode = existingGroupInvitation.get().getInvitationCode();
        } else {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            Invitation invitation = new Invitation();
            invitation.setGame(game);
            invitation.setInvitationCode(UUID.randomUUID().toString());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setGroupInvitation(true);
            invitationRepository.save(invitation);

            invitationCode = invitation.getInvitationCode();
        }

        return "https://localhost:8080/accept-invitation?code=" + invitationCode;
    }


}