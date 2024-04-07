package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.game.CreateGameRequest;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.user.Role;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import kz.hackathon.secretsantaapp.service.InvitationService;
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
    public ResponseEntity<?> createGame(@Valid @RequestBody CreateGameRequest request, BindingResult result) {
        User currentUser = customUserDetailService.getCurrentUser();

        if (request.getPriceLimitChecked() && request.getMaxPrice() == null) {
            result.addError(new FieldError("createGameRequest", "maxPrice", "Price is required when the price limit is checked."));
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
        game.setCreator(currentUser);

        //CREATOR IS NOT PARTICIPIANT UNTIL CLICKS OWN CARD
        Game newGame = gameService.createGame(game, currentUser.getId());

        GameResponse response = new GameResponse(
                newGame.getId(),
                newGame.getName(),
                newGame.getMaxPrice(),

                gameUserService.getParticipantCountByGameId(newGame.getId()),
                newGame.getCreator().getId(),
                Role.ORGANISER
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mygames")
    public ResponseEntity<List<GameResponse>> getMyGames() {
        User currentUser = customUserDetailService.getCurrentUser();
        List<Game> games = gameService.getGamesByCreatorId(currentUser.getId());

        List<GameResponse> responses = new ArrayList<>();
        games.forEach(game -> {
            Role userRole = (game.getCreator().getId().equals(currentUser.getId())) ? Role.ORGANISER : Role.PARTICIPANT;
            int participantCount = gameUserService.getParticipantCountByGameId(game.getId());
            responses.add(new GameResponse(
                    game.getId(), game.getName(),/* game.getUniqueIdentifier(),*/
                    game.getMaxPrice(), participantCount, game.getCreator().getId(), userRole));
        });
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

/*    @PostMapping("/{gameId}/reshuffle")
    public ResponseEntity<?> reshuffleParticipants(@PathVariable UUID gameId) {
        try {
            gameUserService.reshuffle(gameId);
            return ResponseEntity.ok("Participants reshuffled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reshuffling participants: " + e.getMessage());
        }
    }*/

}
