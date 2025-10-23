package g115.quickmart.Controller;

import g115.quickmart.Model.Product;
import g115.quickmart.Model.User;
import g115.quickmart.Repo.ProductRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
    private final ProductRepo productRepo;

    public InventoryController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
    //Read Inventory
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("newProduct", new Product());

        if (loggedInUser != null) {
            model.addAttribute("username", loggedInUser.getUsername());
        }
        System.out.println("Inventory - Read");
        return "inventory-dashboard";
    }
    //Create/Update Product
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            product.setAddedBy(loggedInUser.getUsername());
        }
        productRepo.save(product);
        System.out.println("Inventory - Create/Update");
        return "redirect:/inventory/dashboard";
    }
    //Delete Product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepo.deleteById(id);
        System.out.println("Inventory - Delete");
        return "redirect:/inventory/dashboard";
    }


    // Generate CSV Report
    @GetMapping("/report")
    public ResponseEntity<byte[]> generateReport() {
        List<Product> products = productRepo.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        writer.println("ID,Name,Category,Price,Quantity,Image URL,Added By,Created At");
        for (Product p : products) {
            writer.printf("%d,%s,%s,%.2f,%d,%s,%s,%s%n", p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getImageUrl(), p.getAddedBy(), p.getCreatedAt());
        }
        writer.flush();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory_report.csv")
                .body(baos.toByteArray());
    }
}