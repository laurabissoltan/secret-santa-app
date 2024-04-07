package kz.hackathon.secretsantaapp.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationDetails;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationLinkResponse;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationRequest;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.repository.InvitationRepository;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import kz.hackathon.secretsantaapp.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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
    private GameUserService gameUserService;

    @PostMapping("/send")
    public ResponseEntity<?> sendInvitations(@RequestParam UUID gameId, @RequestBody List<InvitationRequest> invitationRequests ) {
        List<String> emails = invitationRequests.stream().map(InvitationRequest::getEmail).collect(Collectors.toList());
        invitationService.sendInvitations(gameId, emails);
        return ResponseEntity.ok("Invitations sent.");
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptIndividualInvitation(@RequestParam("code") String invitationCode) {
        UUID userId = customUserDetailService.getCurrentUser().getId();
        Invitation invitation = invitationRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found."));

        if (gameUserService.isParticipant(invitation.getGame().getId(), userId)) {
            return new ResponseEntity<>("You are already a participant in this game.", HttpStatus.BAD_REQUEST);
        }

        gameUserService.createGameUser(invitation.getGame().getId(), new ArrayList<>(Arrays.asList(userId)));
        invitationRepository.save(invitation);

        return new ResponseEntity<>(HttpStatus.OK);
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
