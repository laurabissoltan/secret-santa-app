package kz.hackathon.secretsantaapp.dto.game;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGameRequest {
    @NotBlank(message = "Game name is required.")
    private String name;
    @Min(value = 1, message = "Max price must be greater than 0.")
    private Integer maxPrice; //No @NotNull here, to allow null when the checkbox is not checked

    @Column(name="unique_identifier", nullable = false, unique = true)
    private String uniqueIdentifier;

    @NotNull(message = "Price limit checked status is required.")
    private Boolean priceLimitChecked;

}
