package kz.hackathon.secretsantaapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ValidationException;
import kz.hackathon.secretsantaapp.dto.wshlist.GifteeWishlistResponse;
import kz.hackathon.secretsantaapp.dto.wshlist.WishlistResponse;
import kz.hackathon.secretsantaapp.model.gameUser.GameUser;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.GameUserRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import kz.hackathon.secretsantaapp.service.CustomUserDetailService;
import kz.hackathon.secretsantaapp.service.GameUserService;
import kz.hackathon.secretsantaapp.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameUserRepository gameUserRepository;

    @Operation(summary = "создание списка подарков, по требованию ограничение максимум 10 подарков")
    @PostMapping("/{gameId}/create-wishlist")
    public ResponseEntity<?> createWishlist(@PathVariable UUID gameId, @RequestBody List<String> descriptions) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();

        wishlistService.createWishlist(gameId, userId, descriptions);
        gameUserService.updateGameUserStatusAccepted(gameId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Список подарков данного пользователя (то что он сам написал)")
    @GetMapping("/{gameId}/my-wishlist")
    public ResponseEntity<List<WishlistResponse>> getWishlist(@PathVariable UUID gameId) {
        User user = customUserDetailService.getCurrentUser();
        UUID userId = user.getId();

        List<Wishlist> wishlists = wishlistService.getWishlistByGameIdAndUserId(gameId, userId);
        List<WishlistResponse> wishlistResponses = new ArrayList<>();
        wishlists.forEach(wishlist -> {
            wishlistResponses.add(new WishlistResponse(
                    wishlist.getId(), wishlist.getGame().getId(),
                    wishlist.getUser().getId(), wishlist.getDescription(), wishlist.getUser().getEmail()));
        });
        return new ResponseEntity<>(wishlistResponses,HttpStatus.OK);
    }

    @Operation(summary = "Имейл и список подарков подопечнего данного авторизованного пользователя")
    @GetMapping("/{gameId}/my-giftee-wishlist")
    public ResponseEntity<GifteeWishlistResponse> getMyGifteeWishlist(@PathVariable UUID gameId) {
        User currentUser = customUserDetailService.getCurrentUser();
        UUID currentUserId = currentUser.getId();

        GameUser currentGameUser = gameUserRepository.findByGameIdAndUserId(gameId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameUser not found for current user and game"));

        User giftee = currentGameUser.getGiftee();

        if (giftee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GifteeWishlistResponse("No giftee assigned yet"));
        }

        List<Wishlist> gifteeWishlist = wishlistService.getWishlistByGameIdAndUserId(gameId, giftee.getId());

        List<String> descriptions = new ArrayList<>();
        for (Wishlist item : gifteeWishlist) {
            descriptions.add(item.getDescription());
        }

        GifteeWishlistResponse response = new GifteeWishlistResponse(giftee.getEmail(), descriptions);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

