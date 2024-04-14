package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.invitation.Invitation;
import kz.hackathon.secretsantaapp.model.password.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import kz.hackathon.secretsantaapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService{

    private final UserRepository repository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public User create(User user) {
        if (repository.existsByEmail(user.getUsername())) {
            throw new RuntimeException("Email exists already");
        }

        return repository.save(user);
    }

    public void update(User user) {
        repository.save(user);
    }

    public User getByUsername(String username) {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }


    public UserDetailsService userDetailsService(){
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return getByUsername(username);
    }

    public boolean isAuthorized(String ownerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = repository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        return String.valueOf(user.getId()).equals(ownerId);
    }


    public void createPasswordResetTokenForUser(final User user) {
        final String token = UUID.randomUUID().toString();
        final PasswordResetToken myToken = new PasswordResetToken();
        myToken.setUser(user);
        myToken.setToken(token);
        myToken.setExpiryDate(LocalDateTime.now().plusHours(2));
        myToken.setDeactivated(false);
        passwordResetTokenRepository.save(myToken);

        sendResetTokenEmail(user.getEmail(), token);
    }


    private void sendResetTokenEmail(String email, String token) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject("Password Reset Request");
        emailMessage.setText("To reset your password, click the link below:\n" + "http://localhost:8080/reset-password/" + token);
        mailSender.send(emailMessage);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private InvitationRepository invitationRepository;



    @Transactional
    public void deleteUserAccount(UUID userId) {
        // удалить все игры который этот пользователь организатор
        deleteUserAndUnlinkGames(userId);

        passwordResetTokenRepository.deletePasswordResetTokenByUserId(userId);


      //  passwordResetTokenRepository.deletePasswordResetTokenByUserId(userId);
        // Finally, delete the user
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);
    }

    public void deleteGameUserInGame(UUID gameId) {// удалить gameUser этого gameId
        List<GameUser> gameUsers = gameUserRepository.findByGameId(gameId);
        // по цикл удаляем всех wish list для этого gameUser

        gameUsers.forEach(gameUser -> {
            deleteUserInTheWishList(gameUser.getUser().getId());
            // удаляем сам  gameUser
            gameUserRepository.delete(gameUser);
        });
    }

    public void deleteUserInTheWishList(UUID userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        wishlistRepository.deleteAll(wishlists);
    }

    public void deleteUserAndUnlinkGames(UUID userId) {
        List<Game> games = gameRepository.findByCreatorId(userId);
        invitationRepository.deleteByUserId(userId);

     //   User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        games.forEach(game -> {
            //invitations

            // если есть GameUsers для этого игры тогда удаляем их
            deleteGameUserInGame(game.getId());
            // удаляем game
            gameRepository.delete(game);

        });
    }


}
