package kz.hackathon.secretsantaapp.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationDetails;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationLinkResponse;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.InvitationRepository;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameService;
import kz.hackathon.secretsantaapp.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private GameService gameService;

    @GetMapping("/validate")
    public ResponseEntity<?> validateInvitation(@RequestParam("code") String invitationCode) {
        Invitation invitation = invitationRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found."));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            return ResponseEntity.badRequest().body("This invitation has already been accepted or is invalid.");
        }

        return ResponseEntity.ok(new InvitationDetails(invitation.getGame().getId(), invitation.getGame().getName(), "Organizer invites you to the game"));
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendInvitations(@RequestParam UUID gameId, @RequestBody List<String> emails) {
        invitationService.sendInvitations(gameId, emails);
        return ResponseEntity.ok("Invitations sent.");
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<?> acceptInvitation(@RequestParam("code") String invitationCode) {
        UUID userId = customUserDetailService.getCurrentUser().getId();
        Invitation invitation = invitationRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found."));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            return ResponseEntity.badRequest().body("This invitation has already been accepted or declined.");
        }
        Game game = invitation.getGame();
        boolean isAlreadyParticipant = false;
        for (User participant: game.getParticipants()) {
            if (participant.getId().equals(userId)) {
                isAlreadyParticipant = true;
                break;
            }
        }
        if (isAlreadyParticipant) {
            return ResponseEntity.badRequest().body("You are already a participant in this game.");
        }
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        gameService.addParticipantToGame(game.getId(), userId);

        GameResponse response = new GameResponse(game.getId(), game.getName(),
                game.getUniqueIdentifier(), game.getMaxPrice(), game.getCreator().getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/generate-link")
    public ResponseEntity<InvitationLinkResponse> generateLink(@RequestParam UUID gameId) {
        try {
            String link = invitationService.generateShareableLink(gameId);
            return ResponseEntity.ok(new InvitationLinkResponse(link));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new InvitationLinkResponse(e.getMessage()));
        }
    }
}
