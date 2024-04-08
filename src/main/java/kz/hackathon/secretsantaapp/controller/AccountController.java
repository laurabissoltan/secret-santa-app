package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kz.hackathon.secretsantaapp.dto.accountSettings.ChangePasswordRequest;
import kz.hackathon.secretsantaapp.dto.accountSettings.UpdateLoginEmailRequest;
import kz.hackathon.secretsantaapp.dto.accountSettings.UserInfoDto;
import kz.hackathon.secretsantaapp.dto.registration.JwtAuthenticationResponse;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.service.AuthenticationService;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
@Tag(name="account-settings-controller")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    private final AuthenticationService authenticationService;
    private final CustomUserDetailService customUserDetailService;

    @PostMapping("/update-login-email")
    public ResponseEntity<?> updateLoginEmail(@RequestBody UpdateLoginEmailRequest request) {
        try {
            authenticationService.updateLoginEmail(request);

            return ResponseEntity.status(HttpStatus.OK).body("Данные обновлены. Требуется повторная авторизация");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authenticationService.changePassword(request);
            return ResponseEntity.ok().body("Пароль успешно обновлен");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount() {
        try {
            User currentUser = customUserDetailService.getCurrentUser();
            authenticationService.deleteUserByUsername(currentUser.getEmail());
            return ResponseEntity.ok().body("Аккаунт успешно удален");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
