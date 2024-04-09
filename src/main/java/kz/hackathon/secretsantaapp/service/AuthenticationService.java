package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kz.hackathon.secretsantaapp.dto.accountSettings.ChangePasswordRequest;
import kz.hackathon.secretsantaapp.dto.accountSettings.UpdateLoginEmailRequest;
import kz.hackathon.secretsantaapp.dto.registration.AuthenticationRequest;
import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.dto.registration.RegisterRequest;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.password.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.user.Role;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationResponse signUp(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PARTICIPANT)
                .build();

        user = customUserDetailService.create(user);

        final UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public JwtAuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        final UserDetails userDetails = customUserDetailService.loadUserByUsername(request.getEmail());
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken.isDeactivated() || resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordResetDate(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setDeactivated(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public void updateLoginEmail(UpdateLoginEmailRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = customUserDetailService.getByUsername(currentUsername);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        boolean detailsUpdated = false;

        if (StringUtils.hasText(request.getNewLogin()) && !user.getLogin().equals(request.getNewLogin())) {
            user.setLogin(request.getNewLogin());
            detailsUpdated = true;
        }

        if (StringUtils.hasText(request.getNewEmail()) && !user.getEmail().equals(request.getNewEmail())) {
            user.setEmail(request.getNewEmail());
            detailsUpdated = true;
        }

        if (detailsUpdated) {
            customUserDetailService.update(user);
        } else {
            throw new IllegalArgumentException("No updates provided or the new details are the same as the current ones.");
        }
    }

    public void changePassword(ChangePasswordRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = customUserDetailService.getByUsername(currentUsername);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current password");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
       // user.setLastPasswordResetDate(LocalDateTime.now());
        customUserDetailService.update(user);

        // Refreshing the security context
        UserDetails updatedUserDetails = customUserDetailService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
        userRepository.delete(user);
    }

    @Autowired
    GameUserRepository gameUserRepository;
    @Autowired
    GameRepository gameRepository;
    WishlistRepository wishlistRepository;
   // UserRepository userRepository;
    @Autowired
    InvitationRepository invitationRepository;





}

