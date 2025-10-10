package g115.quickmart.Repo;

import g115.quickmart.Model.Driver;
import g115.quickmart.Model.Order;
import g115.quickmart.Model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends CrudRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
    List<Order> findByDriver(Driver driver);
    List<Order> findTop10ByUserOrderByOrderDateDesc(User user);

}
