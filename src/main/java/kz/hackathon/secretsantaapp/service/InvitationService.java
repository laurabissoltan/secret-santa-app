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

        for (String email : emails) {
            boolean invitationExists = invitationRepository.findByGameIdAndEmail(gameId, email).isPresent();

            if (invitationExists) {
                continue;
            }

            Invitation invitation = new Invitation();
            invitation.setGame(game);
            invitation.setEmail(email);
            invitation.setInvitationCode(UUID.randomUUID().toString());
            invitation.setStatus(InvitationStatus.PENDING);
            invitationRepository.save(invitation);

            String invitationLink = "https://localhost:8080/accept-invitation?code=" + invitation.getInvitationCode();
            emailService.sendEmail(email, "You're invited to join a Secret Santa game!", "Please click the link to join: " + invitationLink);
        }
    }

    public String generateShareableLink(UUID gameId) {
        String invitationCode = UUID.randomUUID().toString();
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));

        Invitation invitation = new Invitation();
        invitation.setGame(game);
        invitation.setInvitationCode(invitationCode);
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        return "https://localhost:8080/accept-invitation?code=" + invitationCode;
    }

// for one each time case
/*    public void sendInvitation(UUID gameId, String email) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        Invitation invitation = new Invitation();
        invitation.setGame(game);
        invitation.setEmail(email);
        invitation.setInvitationCode(UUID.randomUUID().toString());
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        String invitationLink = "https://localhost:8080/accept-invitation?code=" + invitation.getInvitationCode();
        emailService.sendEmail(email, "You're invited to join a Secret Santa game!", "Please click the link to join: " + invitationLink);
    }*/




}