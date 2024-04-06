package kz.hackathon.secretsantaapp.dto.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GameResponse {
    private UUID id;
    private String name;
    private String uniqueIdentifier;
    private Integer maxPrice; // Can be null to indicate no limit
    private UUID creatorId;


}
