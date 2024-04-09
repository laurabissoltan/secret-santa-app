package kz.hackathon.secretsantaapp.dto.wshlist;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class GifteeWishlistResponse {
    private String message;
    private String gifteeEmail;
    private List<String> wishlistDescriptions;

    public GifteeWishlistResponse(String message) {
        this.message = message;
    }

    public GifteeWishlistResponse(String gifteeEmail, List<String> wishlistDescriptions) {
        this.gifteeEmail = gifteeEmail;
        this.wishlistDescriptions = wishlistDescriptions;
    }
}
