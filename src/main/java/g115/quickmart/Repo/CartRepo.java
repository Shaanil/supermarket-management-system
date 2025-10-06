package g115.quickmart.Repo;

import g115.quickmart.Model.Cart;
import g115.quickmart.Model.Product;
import g115.quickmart.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    long countByUser(User user);
    Optional<Cart> findByUserAndProduct(User user, Product product);
}