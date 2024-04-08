package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.dao.DataIntegrityViolationException;
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
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Данный логин уже существует в системе. Пожалуйста придумайте другой логин");
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
    public ResponseEntity<String> requestResetPassword(@RequestParam("email") String userEmail) {
        User user = userService.getByUsername(userEmail);
        if(user != null) {
            userService.createPasswordResetTokenForUser(user);
         //   return ResponseEntity.notFound().body("Пользователь с таким email не найден в системе.");
        }

        return ResponseEntity.ok("Если ваш email зарегистрирован в нашей системе, на него будет отправлена ссылка для восстановления аккаунта.");
    }

    @Operation(summary = "восстановаление пароля, принимает три значения, временный токен (это не access и не рефреш) берется со ссылки который был отправлен по почте")
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody ResetPasswordRequest resetRequest) {
        try {
            if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Пароли не совпадают.");
            }
         //   PasswordResetToken myToken = passwordResetTokenRepository.findByToken(token);
            authenticationService.resetPassword(token, resetRequest.getNewPassword());

            return ResponseEntity.ok("Пароль успешно восстанавлен. Пожалуйста залогиньтесь заново");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка во время восстанавления пароля");
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
