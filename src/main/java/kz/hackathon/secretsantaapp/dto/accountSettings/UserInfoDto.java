package kz.hackathon.secretsantaapp.dto.accountSettings;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserInfoDto {
    private UUID userId;
    private String email;
    private String login;
}
