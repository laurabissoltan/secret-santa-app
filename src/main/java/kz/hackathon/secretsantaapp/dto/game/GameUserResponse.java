package kz.hackathon.secretsantaapp.dto.game;

import kz.hackathon.secretsantaapp.model.game.Status;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameUserResponse {
    private UUID id;
    private String game;
    private String user;
    private String giftee;
    private Status status;
    private String feedback;
    private InvitationStatus invitationStatus;
    private String userName;
    private String email;
    private String phoneNumber;
}
