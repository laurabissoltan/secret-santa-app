package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.game.CreateGameRequest;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.gameUser.Status;
import kz.hackathon.secretsantaapp.model.user.Role;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.GameUserRepository;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/games")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    GameUserService gameUserService;


    @PostMapping("/create-game")
    @Operation(summary = "создание игры, нам дополнительно нужно чтоб фронт создал и вернул идентификтор по требованиям (ex. 45LK245 )")
    public ResponseEntity<?> createGame(@Valid @RequestBody CreateGameRequest request, BindingResult result) {
        User currentUser = customUserDetailService.getCurrentUser();

        if (request.getPriceLimitChecked() && request.getMaxPrice() == null) {
            result.addError(new FieldError("createGameRequest", "maxPrice", "Надо указать максимальную стоимость подарка"));
        }

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Game game = new Game();
        game.setName(request.getName());
        game.setMaxPrice(request.getMaxPrice());
        game.setUniqueIdentifier(request.getUniqueIdentifier());
        game.setCreator(currentUser);

        Game newGame = gameService.createGame(game, currentUser.getId());

        gameUserService.createGameUser(newGame.getId(), Collections.singletonList(currentUser.getEmail()));

     //   newGame.setStatus(Status.IN_PROCESS);
        GameResponse response = new GameResponse(
                newGame.getId(),
                newGame.getName(),
                newGame.getMaxPrice(),
                (int) gameUserService.getParticipantCountByGameId(newGame.getId()),
                newGame.getCreator().getId(),
                Role.ORGANISER
           //     newGame.getStatus()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Autowired
    GameUserRepository gameUserRepository;

    @GetMapping("/mygames")
    @Operation(summary = "список игр у данного авторизованного пользователя")
    public ResponseEntity<List<GameResponse>> getMyGames() {
        User currentUser = customUserDetailService.getCurrentUser();

        List<GameUser> userGames = gameUserRepository.findByUserId(currentUser.getId());


        List<GameResponse> responses = new ArrayList<>();
        userGames.forEach(gameUser -> {
            Role userRole = (gameUser.getGame().getCreator().getId().equals(currentUser.getId())) ? Role.ORGANISER : Role.PARTICIPANT;
            int participantCount = (int) gameUserService.getParticipantCountByGameId(gameUser.getGame().getId());

            // int participantCount = gameUserService.getParticipantCountByGameId(game.getId());
            responses.add(new GameResponse(
                    gameUser.getGame().getId(),
                    gameUser.getGame().getName(),
                //    gameUser.getGame().getUniqueIdentifier(),
                    gameUser.getGame().getMaxPrice(),
                    participantCount,
                    gameUser.getGame().getCreator().getId(),
                    userRole));
        });
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "жеребьевка, люди которые не заполнили контактные данные и вишлист убираются со списока после жеребьевки")
    @PostMapping("/{gameId}/reshuffle")
    public ResponseEntity<?> reshuffleParticipants(@PathVariable UUID gameId) {
        try {
            gameUserService.reshuffle(gameId);
            return ResponseEntity.ok("Жеребьевка завершена");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка во время жеребьевки: " + e.getMessage());
        }
    }

}
