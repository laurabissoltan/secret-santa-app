package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.registration.AuthenticationRequest;
import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.dto.registration.RefreshTokenRequest;
import kz.hackathon.secretsantaapp.dto.registration.RegisterRequest;
import kz.hackathon.secretsantaapp.dto.resetPassword.ResetPasswordRequest;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.service.AuthenticationService;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name="authentication-controller")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {
    @Autowired
    private final AuthenticationService authenticationService;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody @Valid RegisterRequest request) {
        try {
            var response = authenticationService.signUp(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> signIn(@RequestBody @Valid AuthenticationRequest request) {
        try {
            var response = authenticationService.signIn(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public String requestResetPassword(@RequestParam("email") String userEmail) {
        User user = userService.getByUsername(userEmail);
        userService.createPasswordResetTokenForUser(user);
        return "Reset password link sent to email";
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        try {
            if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match.");
            }
            authenticationService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
            return ResponseEntity.ok("Password successfully reset. Please log in again.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while resetting the password.");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        UserDetails userDetails = userService.loadUserByUsername(jwtService.extractUserName(requestRefreshToken));
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
    }

}
