package kz.hackathon.secretsantaapp.model.invitation;


import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.game.Game;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Game game;

    private String email;
    private String invitationCode;
    private InvitationStatus status;

    private boolean groupInvitation = false;
}
