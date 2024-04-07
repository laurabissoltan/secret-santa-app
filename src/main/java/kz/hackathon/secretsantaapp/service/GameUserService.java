package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.GameUserRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GameUserService {
    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public List<GameUser> createGameUser(UUID gameId, List<UUID> usersId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        List<GameUser> gameUserList = new ArrayList<>();

        usersId.forEach(userId->{
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            GameUser gameUser = new GameUser();
            gameUser.setUser(user);
            gameUser.setGame(game);
            gameUserList.add(gameUser);
        });

        return gameUserRepository.saveAll(gameUserList);
    }

    public List<GameUser> getGamesUserByGameId(UUID gameId){
        return gameUserRepository.findByGameId(gameId);
    }

    public GameUser getGameUserByGameIdAndUserId(UUID gameId, UUID userId){
        return gameUserRepository.findByGameIdAndUserId(gameId, userId).orElse(null);
    }

    public void reshuffle(UUID gameId){
        List<GameUser> gameUsers = getGamesUserByGameId(gameId);

        List<User> users = new ArrayList<>();

        gameUsers.forEach(gameUser -> {
            users.add(gameUser.getUser());
        });

        List<User> userList = new ArrayList<>(users);
        Collections.shuffle(userList);

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i) == userList.get(i)) {
                if (i + 1 < users.size()){
                    User receiver = userList.get(i + 1);
                    userList.set(i + 1, userList.get(i));
                    userList.set(i , receiver);
                }else {
                    User receiver = userList.get(1);
                    userList.set(1, userList.get(i));
                    userList.set(i , receiver);
                }
            }
        }

        for (int i = 0; i < gameUsers.size(); i++) {
            gameUsers.get(i).setGiftee(userList.get(i));
        }
        gameUserRepository.saveAll(gameUsers);
    }

    public boolean isParticipant(UUID gameId, UUID userId) {
        return gameUserRepository.findByGameIdAndUserId(gameId, userId).isPresent();
    }

    public int getParticipantCountByGameId(UUID gameId) {
        // Assuming you have a repository method to count participants by gameId
        return gameUserRepository.countByGameId(gameId);
    }
}
