package g115.quickmart.Repo;
import g115.quickmart.Model.Product;
import g115.quickmart.Model.User;
import g115.quickmart.Model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepo extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser(User user);
    long countByUser(User user);
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
}