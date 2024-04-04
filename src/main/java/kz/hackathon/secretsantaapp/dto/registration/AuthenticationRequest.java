package kz.hackathon.secretsantaapp.dto.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
    @Size(min=2, max=50, message = "The length should be between 2 and 50 symbols")
    @NotBlank(message = "Not empty")
    @Email (message = "Email must be valid")
    private String email;

    @Size(max=255, message = "No more than 255 symbols")
    @NotBlank(message = "Not empty")
    private String password;
}
