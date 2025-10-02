// src/main/java/com/quickmart/promo/PromotionRepository.java
package g115.quickmart.Repo;

import g115.quickmart.Model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PromotionRepo extends JpaRepository<Promotion, Long> {

    // Active only, ordered by priority then most recent
    @Query("""
        select p from Promotion p
        where p.active = true
        order by p.priority asc, p.id desc
    """)
    List<Promotion> findActive();
}