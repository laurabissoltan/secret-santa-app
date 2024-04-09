package kz.hackathon.secretsantaapp.dto.invitation;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequest {
    private String name; // not used, I do not understand why it is even here in the design

    @Email(message = "Email должен быть валидным")
    private String email;
}
