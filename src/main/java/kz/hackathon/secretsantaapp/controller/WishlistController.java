package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ValidationException;
import kz.hackathon.secretsantaapp.dto.wshlist.WishlistResponse;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import kz.hackathon.secretsantaapp.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private GameUserService gameUserService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Operation(summary = "создание списка подарков, по требованию ограничение максимум 10 подарков")
    @PostMapping("/{gameId}/create-wishlist")
    public ResponseEntity<?> createWishlist(@PathVariable UUID gameId, @RequestBody List<String> descriptions) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();

        wishlistService.createWishlist(gameId, userId, descriptions);
        gameUserService.updateGameUserStatusAccepted(gameId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "список подарков подопечнего данного пользователя")
    @GetMapping("/{gameId}/my-giftee")
    public ResponseEntity<List<WishlistResponse>> getWishlist(@PathVariable UUID gameId) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();

        List<Wishlist> wishlists = wishlistService.getWishlistByGameIdAndUserId(gameId, userId);
        List<WishlistResponse> wishlistResponses = new ArrayList<>();
        wishlists.forEach(wishlist -> {
            wishlistResponses.add(new WishlistResponse(
                    wishlist.getId(), wishlist.getGame().getId(),
                    wishlist.getUser().getId(), wishlist.getUser().getEmail(), wishlist.getDescription()));
        });
        return new ResponseEntity<>(wishlistResponses,HttpStatus.OK);
    }
}
