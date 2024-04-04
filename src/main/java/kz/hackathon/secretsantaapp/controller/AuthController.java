package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.hackathon.secretsantaapp.dto.registration.AuthenticationRequest;
import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.dto.registration.RegisterRequest;
import kz.hackathon.secretsantaapp.dto.resetPassword.ResetPasswordRequest;
import kz.hackathon.secretsantaapp.model.PasswordResetToken;
import kz.hackathon.secretsantaapp.model.User;
import kz.hackathon.secretsantaapp.repository.PasswordResetTokenRepository;
import kz.hackathon.secretsantaapp.service.AuthenticationService;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name="Authentication controller")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody @Valid RegisterRequest request) {
      //  return ResponseEntity.ok(authenticationService.signUp(request));
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

        // return ResponseEntity.ok(authenticationService.signIn(request));
    }


    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

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

/*
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            JwtAuthenticationResponse jwtResponse = authenticationService.resetPassword(token, newPassword);
            return ResponseEntity.ok(jwtResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while resetting the password.");
        }
    }
*/

   // @PostMapping("/account-settings")

}
