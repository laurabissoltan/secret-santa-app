package kz.hackathon.secretsantaapp.dto.game;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.gameUser.Status;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.user.User;
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
