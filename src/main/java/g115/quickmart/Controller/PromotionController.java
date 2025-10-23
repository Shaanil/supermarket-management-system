package g115.quickmart.Controller;
import g115.quickmart.Model.Promotion;
import g115.quickmart.Repo.PromotionRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;
@Controller
@RequestMapping("/marketing")
public class PromotionController {

    private final PromotionRepo repo;
    public PromotionController(PromotionRepo repo) {
        this.repo = repo;
    }
    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("promotions", repo.findAll());
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("mode", "create");
        System.out.println("Marketing- Read");
        return "promotion-dashboard"; //
    }
    // Update
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("promotions", repo.findAll());
        Promotion formBacking = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid id: " + id));
        model.addAttribute("promotion", formBacking);
        model.addAttribute("mode", "edit");
        return "promotion-dashboard";
    }
    //Create
    @PostMapping("/save")
    public String save(@ModelAttribute Promotion promotion) {
        if (promotion.getPriority() == null) promotion.setPriority(0);
        repo.save(promotion);
        System.out.println("Marketing- Create/Update");
        return "redirect:/marketing/dashboard";
    }
    //Delete
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        System.out.println("Marketing- Delete");
        return "redirect:/marketing/dashboard";
    }

    //Export CSV Report
    @GetMapping("/report")
    public ResponseEntity<byte[]> report() {
        List<Promotion> list = repo.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        writer.println("ID,Title,Description,ImageURL,Active,Priority");
        for (Promotion p : list) {
            writer.printf("%d,%s,%s,%s,%s,%d%n",
                    p.getId(),
                    safe(p.getTitle()),
                    safe(p.getDescription()),
                    safe(p.getImageUrl()),
                    String.valueOf(Boolean.TRUE.equals(p.isActive())),
                    p.getPriority() == null ? 0 : p.getPriority()
            );
        }
        writer.flush();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=quickmart_promotions.csv")
                .body(baos.toByteArray());
    }

    private String safe(String s){
        if (s == null) return "";
        // Basic CSV escaping for commas/quotes/newlines
        boolean needsQuotes = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
        String esc = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + esc + "\"" : esc;
    }
}