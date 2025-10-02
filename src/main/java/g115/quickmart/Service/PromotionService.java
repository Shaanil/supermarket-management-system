// src/main/java/com/quickmart/promo/PromotionService.java
package g115.quickmart.Service;

import g115.quickmart.Model.Promotion;
import g115.quickmart.Repo.PromotionRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PromotionService {
    private final PromotionRepo repo;
    public PromotionService(PromotionRepo repo){
        this.repo = repo;
    }

    public List<Promotion> findActivePromotions() {
        return repo.findActive();
    }
}