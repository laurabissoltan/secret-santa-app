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
        passwordResetTokenRepository.save(myToken);

        sendResetTokenEmail(user.getEmail(), token);
    }

    private void sendResetTokenEmail(String email, String token) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject("Password Reset Request");
        emailMessage.setText("To reset your password, click the link below:\n" + "http://localhost:8080/reset-password?token=" + token);
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



    @Transactional
    public void deleteUserAccount(UUID userId) {
        // Unlink from GameUser entities
        deleteUserInGameUser(userId);
        // Unlink from Wishlist entities
        deleteUserInTheWishList(userId);
        // Unlink from Game entities
        deleteUserAndUnlinkGames(userId);

        // Finally, delete the user
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }
 /*   public void deleteUser(UUID userId) {
        deleteUserAndUnlinkGames(userId);
        deleteUserInTheWishList(userId);
        deleteUserInGameUser(userId);
    }*/

    public void deleteUserInGameUser(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<GameUser> gameUsers = gameUserRepository.findByUserId(userId);

        for (GameUser gameUser : gameUsers) {
            gameUser.setUser(null);
            gameUserRepository.save(gameUser);
        }
      //  userRepository.delete(user);
    }


    public void deleteUserInTheWishList(UUID userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        for (Wishlist wishlist : wishlists) {
            wishlist.setUser(null);
            wishlistRepository.save(wishlist);
        }
    }

    public void deleteUserAndUnlinkGames(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Game> games = gameRepository.findByCreatorId(userId);
        games.forEach(game -> {
            game.setCreator(null);
            gameRepository.save(game);
        });
    }


}
