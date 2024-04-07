package kz.hackathon.secretsantaapp.dto.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameUserRequest {
    private String name;
    private String email;
}
