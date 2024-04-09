package kz.hackathon.secretsantaapp.repository;

import kz.hackathon.secretsantaapp.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByGameIdAndUserId(UUID gameId, UUID userId);

    List<Wishlist> findByUserId(UUID userId);
    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
