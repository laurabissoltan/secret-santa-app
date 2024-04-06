package kz.hackathon.secretsantaapp.service;

import jakarta.persistence.EntityNotFoundException;
import kz.hackathon.secretsantaapp.model.game.Game;
import kz.hackathon.secretsantaapp.model.user.User;
import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import kz.hackathon.secretsantaapp.repository.GameRepository;
import kz.hackathon.secretsantaapp.repository.UserRepository;
import kz.hackathon.secretsantaapp.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    public void createWishlist(UUID gameId, UUID userId, List<String> descriptions) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        List<Wishlist> wishlists = new ArrayList<>();
        descriptions.forEach(description->{
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setGame(game);
            wishlist.setDescription(description);
            wishlists.add(wishlist);
        });

        wishlistRepository.saveAll(wishlists);
    }

    public List<Wishlist> getWishlistByGameIdAndUserId(UUID gameId, UUID userId){
        return wishlistRepository.findByGameIdAndUserId(gameId, userId);
    }
}
