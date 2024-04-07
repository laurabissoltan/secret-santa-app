package kz.hackathon.secretsantaapp.dto.invitation;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitationRequest {
    private String name; // not used, I do not understand why it is even here in the design
    private String email;
}
