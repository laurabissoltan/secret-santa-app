package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.game.BeforeGameUser;
import kz.hackathon.secretsantaapp.dto.game.ContactInfoDto;
import kz.hackathon.secretsantaapp.dto.game.CreateGameUserRequest;
import kz.hackathon.secretsantaapp.dto.game.GameUserResponse;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.game.Status;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
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


    @Operation(summary = "добавление контактных данных для участия в игре")
    @PostMapping("/{gameId}/contact-info")
    public ResponseEntity<?> updateGameUser(@PathVariable UUID gameId,
                                            @RequestBody ContactInfoDto contactInfoDto) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();

        gameUserService.updateGameUser(gameId, userId,
                contactInfoDto.getUserName(), contactInfoDto.getEmail(), contactInfoDto.getPhoneNumber());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "добавление участников вручную вариант 2: организатор ищет в системе людей, которые уже есть и добавляет в игру")
    @PostMapping("/{gameId}/add-existing-users")
    public ResponseEntity<?> createGameUsers(@PathVariable UUID gameId, @Valid @RequestBody List<CreateGameUserRequest> requests) {
        List<String> emails = new ArrayList<>();
        requests.forEach(createGameUserRequest -> {
            emails.add(createGameUserRequest.getEmail());
        });
        gameUserService.createGameUser(gameId, emails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "список участников после жеребьевки")
    @GetMapping("/{gameId}/list-after-shuffle")
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

    @Operation(summary = "список участников до жеребьевки")
    @GetMapping("/{gameId}/list-before-shuffle")
    public ResponseEntity<?> listAllGameUsers(@PathVariable UUID gameId) {
        List<GameUser> gameUsers = gameUserService.getGamesUserByGameId(gameId);
        List<BeforeGameUser> responses = new ArrayList<>();
        gameUsers.forEach(gameUser -> {
            responses.add(new BeforeGameUser(
                    gameUser.getId(), gameUser.getGame().getName(), gameUser.getUser().getEmail(),
                    gameUser.getStatus(), gameUser.getFeedback(), gameUser.getInvitationStatus(), gameUser.getUserName(),
                    gameUser.getEmail(), gameUser.getPhoneNumber()));
        });
        return new ResponseEntity<>(responses,HttpStatus.OK);
    }


    @Operation(summary = "связаться с организатором, отправляется сообщение на почту организатору")
    @PostMapping("/{gameId}/send-email")
    public ResponseEntity<?> sendEmailOrganizer(@PathVariable UUID gameId) {
        gameUserService.sendEmailOrganizer(gameId, customUserDetailService.getCurrentUser().getId());
        GameUser gameUser = (GameUser) gameUserService.getGamesUserByGameId(gameId);
   //     gameUser.setStatus(Status.EMERGENCY_SITUATIONS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
