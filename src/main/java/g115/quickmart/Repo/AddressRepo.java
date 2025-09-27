package g115.quickmart.Repo;

import g115.quickmart.Model.Address;
import g115.quickmart.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}