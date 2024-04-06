package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.game.CreateGameRequest;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.model.game.Game;
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

    @PostMapping
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

        // Creating a new game instance
        Game game = new Game();
        game.setName(request.getName());
        game.setMaxPrice(request.getMaxPrice());
        game.setCreator(currentUser);

        // Save the game and include the creator as the first participant
        Game newGame = gameService.createGame(game, currentUser.getId());

        // Assuming the creator also wants to participate in the game
        // Create a GameUser instance for the creator
        List<UUID> initialParticipantId = Collections.singletonList(currentUser.getId());
        gameUserService.createGameUser(newGame.getId(), initialParticipantId);

        // Prepare the response
        GameResponse response = new GameResponse(
                newGame.getId(),
                newGame.getName(),
                newGame.getUniqueIdentifier(),
                newGame.getMaxPrice(),
                newGame.getCreator().getId()
        );

        // Return the response entity
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


/*    @PostMapping
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
        Game newGame = gameService.createGame(game, currentUser.getId());

        GameResponse response = new GameResponse(newGame.getId(), newGame.getName(), newGame.getUniqueIdentifier(),
                newGame.getMaxPrice(), newGame.getCreator().getId()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }*/

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable UUID gameId) {
        Optional<Game> gameOpt = gameService.getGameById(gameId);
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            GameResponse response = new GameResponse(game.getId(), game.getName(),
                    game.getUniqueIdentifier(), game.getMaxPrice(), game.getCreator().getId()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/mygames")
    public ResponseEntity<List<GameResponse>> getMyGames() {
        User currentUser = customUserDetailService.getCurrentUser();
        List<Game> games = gameService.getGamesByCreatorId(currentUser.getId());
        List<GameResponse> responses = new ArrayList<>();
        games.forEach(game -> {
            responses.add(new GameResponse(
                    game.getId(), game.getName(), game.getUniqueIdentifier(),
                    game.getMaxPrice(), game.getCreator().getId()));
        });
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/reshuffle")
    public ResponseEntity<?> reshuffleParticipants(@PathVariable UUID gameId) {
        try {
            gameUserService.reshuffle(gameId);
            return ResponseEntity.ok("Participants reshuffled successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reshuffling participants: " + e.getMessage());
        }
    }

}
