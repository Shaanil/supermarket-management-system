package g115.quickmart.Controller;

import g115.quickmart.Model.User;
import g115.quickmart.Repo.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboard {

    private final UserRepo userRepository;

    public AdminDashboard (UserRepo userRepository) {
        this.userRepository = userRepository;
    }
    //Read Users
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUser", new User());
        System.out.println("Admin - Read");
        return "admin-dashboard";
    }

    //Create or Update
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            // keep existing password
            User existing = userRepository.findById(user.getId()).orElseThrow();
            user.setPassword(existing.getPassword());
            userRepository.save(user);
        }
        System.out.println("Admin - Create/Update");
        System.out.println(user.getId());
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }
    // Delete user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        System.out.println("Admin - delete");
        return "redirect:/admin/dashboard";
    }

    //Export CSV Report
    @GetMapping("/report")
    public ResponseEntity<byte[]> generateReport() {
        List<User> users = userRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        writer.println("ID,Username,Email,Role");
        for (User u : users) {
            writer.printf("%d,%s,%s,%s%n", u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        }

        writer.flush();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=quickmart_users.csv")
                .body(baos.toByteArray());
    }
}