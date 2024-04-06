package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kz.hackathon.secretsantaapp.dto.game.GameResponse;
import kz.hackathon.secretsantaapp.dto.wshlist.WishlistResponse;
import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import kz.hackathon.secretsantaapp.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<?> createWishlist(@PathVariable UUID gameId, @PathVariable UUID userId, @PathVariable List<String> descriptions) {
        wishlistService.createWishlist(gameId, userId, descriptions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forme")
    public ResponseEntity<List<WishlistResponse>> getWishlist(@PathVariable UUID gameId, @PathVariable UUID userId) {
        List<Wishlist> wishlists = wishlistService.getWishlistByGameIdAndUserId(gameId, userId);
        List<WishlistResponse> wishlistResponses = new ArrayList<>();
        wishlists.forEach(wishlist -> {
            wishlistResponses.add(new WishlistResponse(
                    wishlist.getId(), wishlist.getGame().getId(),
                    wishlist.getUser().getId(), wishlist.getDescription()));
        });
        return new ResponseEntity<>(wishlistResponses,HttpStatus.OK);
    }
}
