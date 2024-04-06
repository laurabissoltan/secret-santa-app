package kz.hackathon.secretsantaapp.model.game;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import kz.hackathon.secretsantaapp.model.base.BaseEntityAudit;
import kz.hackathon.secretsantaapp.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="game")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Game extends BaseEntityAudit {
    @Column(name="name", nullable = false)
    private String name;

    @Column(name="unique_identifier", nullable = false, unique = true)
    private String uniqueIdentifier = UUID.randomUUID().toString();

    @Column(name="max_price")
    private Integer maxPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_id", referencedColumnName = "id")
    private User creator;

    private Currency currency;

    @ManyToMany
    @JoinTable(
            name = "game_participants",
            joinColumns = @JoinColumn(name="game_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> participants = new HashSet<>();
}
