package kz.hackathon.secretsantaapp.dto.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min=2, max=50, message = "Length must be between 2 and 50 characters")
    @NotBlank(message = "Name cannot be blank")
    private String login;


    @Size(min=2, max=50, message = "Length must be between 2 and 50 characters")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    private String password;



}
