package g115.quickmart.Controller;

import g115.quickmart.Model.Order;
import g115.quickmart.Model.Payment;
import g115.quickmart.Model.User;
import g115.quickmart.Repo.OrderRepo;
import g115.quickmart.Repo.PaymentRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import g115.quickmart.Strategy.PaymentStrategyFactory;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final OrderRepo ordersRepo;
    private final PaymentRepo paymentRepo;

    public PaymentController(OrderRepo ordersRepo, PaymentRepo paymentRepo) {
        this.ordersRepo = ordersRepo;
        this.paymentRepo = paymentRepo;
    }
    //ReadPayment
    @GetMapping("/dashboard")
    public String viewPayments(Model model) {
        model.addAttribute("orders", ordersRepo.findAll());
        model.addAttribute("payments", paymentRepo.findAll());
        System.out.println("Payment -Read");
        return "payment-dashboard";
    }
    //Upadte Payments - ApprovePayment Working with view payments
    @GetMapping("/approve/{id}")
    public String approvePayment(@PathVariable Long id) {
        Order order = ordersRepo.findById(id).orElse(null);
        if (order != null) {
            order.setPaymentStatus("APPROVED");
            ordersRepo.save(order);
        }
        System.out.println("Payment - Update (Approve)");
        return "redirect:/payment/dashboard";
    }
    //Upadte Payments - Reject Payment Woking with view Payments
    @GetMapping("/reject/{id}")
    public String rejectPayment(@PathVariable Long id) {
        Order order = ordersRepo.findById(id).orElse(null);
        if (order != null) {
            order.setPaymentStatus("REFUNDED");
            ordersRepo.save(order);
        }
        System.out.println("Payment - Update (Reject)");

        return "redirect:/payment/dashboard";
    }
    //DeletePayment- Working
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        System.out.println("deletePayment");
        paymentRepo.deleteById(id);
        System.out.println("Payment - Delete");
        return "redirect:/payment/dashboard";
    }
    //Create Payment -Working
    @PostMapping("/add")
    public String addPayment(@RequestParam String type,
                             @RequestParam(required = false) String cardHolderName,
                             @RequestParam(required = false) String cardNumber,
                             @RequestParam(required = false) String expiry,
                             @RequestParam(required = false) String customMessage,
                             HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Payment pm = new Payment();
        pm.setUser(user);
        pm.setType(type);
        pm.setCardHolderName(cardHolderName);
        pm.setCardNumber(cardNumber);
        pm.setExpiry(expiry);
        pm.setCustomMessage(customMessage);
        PaymentStrategyFactory.getStrategy(type).process(pm);
        System.out.println("Payment -Create");
        paymentRepo.save(pm);

        return "redirect:/customer/payments";
    }
}