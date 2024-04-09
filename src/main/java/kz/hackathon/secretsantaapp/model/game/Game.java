package kz.hackathon.secretsantaapp.model.game;

import jakarta.persistence.*;
import kz.hackathon.secretsantaapp.model.base.BaseEntityAudit;
import kz.hackathon.secretsantaapp.model.user.User;
import lombok.*;

@Entity
@Table(name="game")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Game extends BaseEntityAudit {
    @Column(name="name", nullable = false)
    private String name;

/*    @Column(name="unique_identifier")
    private String uniqueIdentifier;*/

    @Column(name="max_price")
    private Integer maxPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_id", referencedColumnName = "id")
    private User creator;

    private Currency currency;

    private Status status;
}
