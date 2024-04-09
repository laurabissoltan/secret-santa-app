package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.game.Status;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.GameUserRepository;
import kz.hackathon.secretsantaapp.repository.InvitationRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameUserService {
    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private InvitationRepository invitationRepository;

    public void createGameUser(UUID gameId, List<String> emails) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        List<GameUser> gameUserList = new ArrayList<>();

        emails.forEach(email->{
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
            GameUser gameUser = new GameUser();
            gameUser.setUser(user);
            gameUser.setGame(game);
            gameUser.setInvitationStatus(InvitationStatus.PENDING);
            gameUserList.add(gameUser);
        });

        gameUserRepository.saveAll(gameUserList);
    }

    public List<GameUser> getGamesUserByGameId(UUID gameId){
        return gameUserRepository.findByGameId(gameId);
    }

    public List<GameUser> getGamesUserByGameIdAndInvitationStatus(UUID gameId, InvitationStatus invitationStatus){
        return gameUserRepository.findByGameIdAndInvitationStatus(gameId, invitationStatus);
    }

    public long countAcceptedInvitations(UUID gameId) {
        return gameUserRepository.countByGameIdAndInvitationStatus(gameId, InvitationStatus.ACCEPTED);
    }

    public void reshuffle(UUID gameId){
        List<GameUser> gameUsers = getGamesUserByGameIdAndInvitationStatus(gameId, InvitationStatus.ACCEPTED);

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

        List<GameUser> gameUsersPending = getGamesUserByGameIdAndInvitationStatus(gameId, InvitationStatus.PENDING);
        gameUserRepository.deleteAll(gameUsersPending);

        gameRepository.findById(gameId).ifPresent(game -> {
            game.setStatus(Status.MATCHING_COMPLETED);
            gameRepository.save(game);
        });

        invitationRepository.deleteByGameId(gameId);

    }

    public boolean isParticipant(UUID gameId, UUID userId) {
        return gameUserRepository.findByGameIdAndUserId(gameId, userId).isPresent();
    }

    public long getParticipantCountByGameId(UUID gameId) {
        return gameUserRepository.countByGameIdAndInvitationStatus(gameId, InvitationStatus.ACCEPTED);
    }

    public void updateGameUser(UUID gameId, UUID userId,
                               String userName, String email, String phoneNumber){
        GameUser gameUser = gameUserRepository.findByGameIdAndUserId(gameId, userId).orElse(null);
        assert gameUser != null;
        gameUser.setUserName(userName);
        gameUser.setEmail(email);
        gameUser.setPhoneNumber(phoneNumber);
        gameUserRepository.save(gameUser);
    }

    public void updateGameUserStatusAccepted(UUID gameId, UUID userId){
        GameUser gameUser = gameUserRepository.findByGameIdAndUserId(gameId, userId).orElse(null);
        assert gameUser != null;
        gameUser.setInvitationStatus(InvitationStatus.ACCEPTED);
        gameUserRepository.save(gameUser);
    }


    public void sendEmailOrganizer(UUID gameId, UUID userId){
        Game game =  gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with gameId: " + gameId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with userId: " + userId));
        String content = "Уважаемый организатор игры" + user.getEmail() + " хочет связаться с вами";
        emailService.sendEmail(game.getCreator().getEmail(), game.getName(), content);
    }



}
