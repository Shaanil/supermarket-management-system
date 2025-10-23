package g115.quickmart.Controller;

import g115.quickmart.Model.Order;
import g115.quickmart.Model.User;
import g115.quickmart.Repo.OrderRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class OrderStatusController {

    private final OrderRepo orderRepo;

    public OrderStatusController(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    // GET /customer/track (empty form or show result if ?orderId=...)
    @GetMapping("/track")
    public String track(@RequestParam(required = false) Long orderId,
                        HttpSession session,
                        Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        if (orderId == null) {
            // Show the empty form + the user's recent orders for quick tracking
            List<Order> myOrders = orderRepo.findTop10ByUserOrderByOrderDateDesc(user);
            model.addAttribute("myOrders", myOrders);
            model.addAttribute("viewState", "FORM");
            return "order-track";
        }

        // Load ONLY this user's order
        Order order = orderRepo.findByIdAndUser(orderId, user).orElse(null);
        if (order == null) {
            model.addAttribute("viewState", "NOT_FOUND");
            model.addAttribute("searchedId", orderId);
            return "order-track";
        }

        model.addAttribute("viewState", "RESULT");
        model.addAttribute("order", order);
        model.addAttribute("progress", progressFor(order.getDeliveryStatus()));
        return "order-track";
    }

    @GetMapping("/track/{id}")
    public String trackByPath(@PathVariable("id") Long id) {
        return "redirect:/customer/track?orderId=" + id;
    }

    // Printable receipt
    @GetMapping("/receipt/{id}")
    public String receipt(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Order order = orderRepo.findByIdAndUser(id, user).orElse(null);
        if (order == null) {
            model.addAttribute("message", "Order not found or you don't have access.");
            return "checkout-success";
        }
        model.addAttribute("order", order);
        return "order-receipt";
    }

    // Map delivery status to a % for a progress bar
    private int progressFor(String status) {
        if (status == null) return 10;
        return switch (status) {
            case "PENDING" -> 10;
            case "ASSIGNED" -> 30;
            case "RECEIVED" -> 50;
            case "SHIPPED" -> 80;
            case "DELIVERED" -> 100;
            default -> 10;
        };
    }
}