package kz.hackathon.secretsantaapp.model.gameUser;

import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.invitation.InvitationStatus;
import kz.hackathon.secretsantaapp.model.user.User;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="game_user")
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giftee_id", referencedColumnName = "id")
    private User giftee;

    private Status status;
    private String feedback;
    private InvitationStatus invitationStatus;
    private String userName;
    private String email;
    private String phoneNumber;
}
