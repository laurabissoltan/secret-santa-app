package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.game.CreateGameRequest;
import kz.hackathon.secretsantaapp.dto.game.CreateGameUserRequest;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.dto.game.GameUserResponse;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.gameUser.Status;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.user.Role;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.EmailService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gameuser")
@SecurityRequirement(name = "bearerAuth")
public class GameUserController {
    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @PostMapping("/{gameId}/{userId}")
    public ResponseEntity<?> updateGameUser(@PathVariable UUID gameId, @PathVariable UUID userId,
                                            @RequestBody String userName, @RequestBody String email, @RequestBody String phoneNumber) {
        gameUserService.updateGameUser(gameId, userId, userName, email, phoneNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> createGameUsers(@PathVariable UUID gameId, @Valid @RequestBody List<CreateGameUserRequest> requests) {
        List<String> emails = new ArrayList<>();
        requests.forEach(createGameUserRequest -> {
            emails.add(createGameUserRequest.getEmail());
        });
        gameUserService.createGameUser(gameId, emails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> listGameUsers(@PathVariable UUID gameId) {
        List<GameUser> gameUsers = gameUserService.getGamesUserByGameId(gameId);
        List<GameUserResponse> responses = new ArrayList<>();
        gameUsers.forEach(gameUser -> {
            responses.add(new GameUserResponse(
                    gameUser.getId(), gameUser.getGame().getName(), gameUser.getUser().getEmail(), gameUser.getGiftee().getEmail(),
                    gameUser.getStatus(), gameUser.getFeedback(), gameUser.getInvitationStatus(), gameUser.getUserName(),
                    gameUser.getEmail(), gameUser.getPhoneNumber()));
        });
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> sendEmailOrganizer(@PathVariable UUID gameId) {
        gameUserService.sendEmailOrganizer(gameId, customUserDetailService.getCurrentUser().getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
