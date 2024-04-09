package kz.hackathon.secretsantaapp.dto.wshlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponse {
    private UUID id;
    private UUID gameId;
    private UUID userId;
    private String email;
    private String description;
}
