package g115.quickmart.Repo;

import g115.quickmart.Model.Payment;
import g115.quickmart.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
}