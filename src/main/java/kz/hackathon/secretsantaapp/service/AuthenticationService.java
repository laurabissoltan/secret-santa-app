package kz.hackathon.secretsantaapp.service;

import jakarta.transaction.Transactional;
import kz.hackathon.secretsantaapp.dto.registration.AuthenticationRequest;
import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.dto.registration.RegisterRequest;
import kz.hackathon.secretsantaapp.model.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.Role;
import kz.hackathon.secretsantaapp.model.User;
import kz.hackathon.secretsantaapp.repository.PasswordResetTokenRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_PARTICIPANT)
                .build();

        user = customUserDetailService.create(user); // Ensure you're using the returned user instance, which now has an ID

        // Generate both access and refresh tokens for the new user
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
/*        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_PARTICIPANT)
                .build();

        customUserDetailService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);*/
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
 /*       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        var user = customUserDetailService
                .userDetailsService()
                .loadUserByUsername(request.getEmail());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);*/
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordResetDate(LocalDateTime.now()); // Update last password reset date
        userRepository.save(user);

        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete
        );
    }
/*        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }*/
}
