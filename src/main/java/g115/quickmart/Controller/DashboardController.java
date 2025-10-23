package g115.quickmart.Controller;

import g115.quickmart.Model.User;
import g115.quickmart.Repo.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final UserRepo userRepository;

    public DashboardController(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        User user = userRepository.findByUsername(username);
        session.setAttribute("loggedInUser", user);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        String role = user.getRole();
        switch (role) {
            case "Administrator":
                return "redirect:/admin/dashboard";
            case "Customer":
                return "redirect:/customer/dashboard";
            case "Payment_Manager":
                return "redirect:/payment/dashboard";
            case "Marketing_Manager":
                return "redirect:/marketing/dashboard";
            case "Inventory_Manager":
                return "redirect:/inventory/dashboard";
            case "Delivery_Coordinator":
                return "redirect:/delivery/dashboard";
            default:
                model.addAttribute("error", "Unknown role: " + role);
                return "login";
        }
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String save(@ModelAttribute User user) {
        user.setRole("Customer");
        userRepository.save(user);
        return "redirect:/login";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
