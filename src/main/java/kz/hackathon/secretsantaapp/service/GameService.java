package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.user.Role;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    public Game createGame(Game game, UUID creatorId) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + creatorId));

        game.setCreator(creator);
        creator.setRole(Role.ORGANISER);
/*        if (game.getUniqueIdentifier() == null || game.getUniqueIdentifier().isEmpty()) {
            game.setUniqueIdentifier(UUID.randomUUID().toString());
        }*/

        return gameRepository.save(game);
    }

    public Optional<Game> getGameById(UUID gameId) {
        return gameRepository.findById(gameId);
    }

    public List<Game> getGamesByCreatorId(UUID creatorId) {
        return gameRepository.findByCreatorId(creatorId);
    }



}
