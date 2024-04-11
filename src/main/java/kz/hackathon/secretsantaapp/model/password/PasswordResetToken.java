package kz.hackathon.secretsantaapp.model.password;

import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private boolean deactivated;

}
