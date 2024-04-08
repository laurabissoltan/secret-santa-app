package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByGameIdAndUserId(UUID gameId, UUID userId);
}
