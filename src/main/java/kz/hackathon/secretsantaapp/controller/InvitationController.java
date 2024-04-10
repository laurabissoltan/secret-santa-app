package kz.hackathon.secretsantaapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationLinkResponse;
import kz.hackathon.secretsantaapp.dto.invitation.InvitationRequest;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.user.User;
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

    @Operation(summary = "добавление вручную вариант 1: отправка ссылку на игру по почте")
    @PostMapping("/{gameId}/send")
    public ResponseEntity<?> sendInvitations(@PathVariable("gameId") UUID gameId, @RequestBody List<InvitationRequest> invitationRequests ) throws MessagingException {
        List<String> emails = invitationRequests.stream().map(InvitationRequest::getEmail).collect(Collectors.toList());
        invitationService.sendInvitations(gameId, emails);
        return ResponseEntity.ok("Приглашения были отправлены по почте");
    }


    @Operation(summary = "принятие ссылки, добавляется в базу gameuser, но не может участвовать пока не заполнит контактные данные и вишлист")
    @PostMapping("/accept/{invitationCode}")
    public ResponseEntity<?> acceptIndividualInvitation(@PathVariable("invitationCode") String invitationCode) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();
        Invitation invitation = invitationRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Приглашение не найдено."));

        /*if (gameUserService.isParticipant(invitation.getGame().getId(), userId)) {
            return new ResponseEntity<>("Вы уже являетесь участником игры.", HttpStatus.CONFLICT);
        }*/

        if (gameUserService.isParticipant(invitation.getGame().getId(), userId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Вы уже являетесь участником игры.");
            response.put("gameId", invitation.getGame().getId());
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        gameUserService.createGameUser(invitation.getGame().getId(), new ArrayList<>(Arrays.asList(user.getEmail())));
        invitationRepository.save(invitation);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invitation accepted successfully.");
        response.put("gameId", invitation.getGame().getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "генерация ссылки для игры")
    @GetMapping("/generate-link")
    public ResponseEntity<InvitationLinkResponse> generateLink(@RequestParam UUID gameId) {
        try {
            String link = invitationService.generateShareableLink(gameId);
            return ResponseEntity.ok(new InvitationLinkResponse(link));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new InvitationLinkResponse(e.getMessage()));
        }
    }

    @Operation(summary = "iOS генерация ссылки для игры")
    @GetMapping("/generate-link/ios")
    public ResponseEntity<InvitationLinkResponse> generateLinkIOS(@RequestParam UUID gameId) {
        try {
            String link = invitationService.generateShareableLinkIOS(gameId);
            return ResponseEntity.ok(new InvitationLinkResponse(link));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new InvitationLinkResponse(e.getMessage()));
        }
    }


}
