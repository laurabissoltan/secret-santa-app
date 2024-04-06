package kz.hackathon.secretsantaapp.dto.invitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InvitationDetails {
    private UUID gameId;
    private String gameName;
    private String message;
}
