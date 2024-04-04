package kz.hackathon.secretsantaapp.service;

import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.model.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.User;
import kz.hackathon.secretsantaapp.repository.PasswordResetTokenRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService {

    private final UserRepository repository;

    public User create(User user) {
        if (repository.existsByEmail(user.getUsername())) {
            throw new RuntimeException("username exists already");
        }

        return repository.save(user);
    }

    public User update(User user) {
        return repository.save(user);
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

    //////////reset

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

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


}
