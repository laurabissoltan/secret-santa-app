package kz.hackathon.secretsantaapp.dto.accountSettings;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateLoginEmailRequest {
    private String newLogin;
    private String newEmail;
}