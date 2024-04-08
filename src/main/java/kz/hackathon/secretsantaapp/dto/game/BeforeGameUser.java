package kz.hackathon.secretsantaapp.dto.game;

import kz.hackathon.secretsantaapp.model.gameUser.Status;
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
public class BeforeGameUser {
    private UUID id;
    private String game;
    private String user;
  //  private String giftee;
    private Status status;
    private String feedback;
    private InvitationStatus invitationStatus;
    private String userName;
    private String email;
    private String phoneNumber;
}
