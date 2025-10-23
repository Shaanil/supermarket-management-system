package g115.quickmart.Controller;

import g115.quickmart.Model.*;
import g115.quickmart.Repo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final ProductRepo productRepo;
    private final CartRepo cartRepo;
    private final WishlistRepo wishlistRepo;
    private final PromotionRepo promotionRepo;

    @Autowired private OrderRepo orderRepo;
    @Autowired private AddressRepo addressRepo;
    @Autowired private PaymentRepo paymentRepo;

    public CustomerController(ProductRepo productRepo, CartRepo cartRepo, WishlistRepo wishlistRepo, PromotionRepo promotionRepo) {
        this.productRepo = productRepo;
        this.cartRepo = cartRepo;
        this.wishlistRepo = wishlistRepo;
        this.promotionRepo = promotionRepo;
    }

    // Homepage with Products
    @GetMapping("/dashboard")
    public String customerDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
model.addAttribute("username", "Guest");
            model.addAttribute("products", productRepo.findAll());
            model.addAttribute("cartItems", List.of());
            model.addAttribute("wishlistItems", List.of());
            model.addAttribute("cartCount", 0);
            model.addAttribute("wishlistCount", 0);
            model.addAttribute("categories", productRepo.findDistinctCategories());
            model.addAttribute("promotions", promotionRepo.findActive());
            model.addAttribute("cartTotal", 0.0);
            return "customer-dashboard";
        }
        if (user == null) return "redirect:/login";
        model.addAttribute("username", user.getUsername());
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("cartItems", cartRepo.findByUser(user));
        model.addAttribute("wishlistItems", wishlistRepo.findByUser(user));
        model.addAttribute("cartCount", cartRepo.countByUser(user));
        model.addAttribute("wishlistCount", wishlistRepo.countByUser(user));
        model.addAttribute("categories", productRepo.findDistinctCategories());
        model.addAttribute("promotions", promotionRepo.findActive());

        // Compute total price of cart items
        List<Cart> cartItems = cartRepo.findByUser(user);
        double cartTotal = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();
        model.addAttribute("cartTotal", cartTotal);

        return "customer-dashboard";
    }

    // Add to cart
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Product product = productRepo.findById(productId).orElse(null);
        if (product == null || product.getQuantity() <= 0) {
            return "redirect:/customer/dashboard";
        }

        // ✅ Check if item already in cart
        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);
        if (existingCartItem.isPresent()) {
            Cart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartRepo.save(cartItem);
        } else {
            Cart newCartItem = new Cart();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(1);
            cartRepo.save(newCartItem);
        }

        // Stock Reduce by 1
        product.setQuantity(product.getQuantity() - 1);
        productRepo.save(product);

        return "redirect:/customer/dashboard";
    }

    //Add to wishlist
    @PostMapping("/wishlist/add")
    @Transactional
    public String addToWishlist(@RequestParam Long productId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return "redirect:/customer/dashboard";

        if (wishlistRepo.findByUserAndProduct(user, product).isPresent()) {
            return "redirect:/customer/dashboard";
        }

        Wishlist wish = new Wishlist();
        wish.setUser(user);
        wish.setProduct(product);
        wishlistRepo.save(wish);
        return "redirect:/customer/dashboard";
    }

    // View Cart
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Cart> cartItems = cartRepo.findByUser(user);
        double total = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
        return "customer-cart";
    }

    //View Wishlist
    @GetMapping("/wishlist")
    public String viewWishlist(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("wishlistItems", wishlistRepo.findByUser(user));
        return "redirect:/customer/dashboard";
    }

    //Remove from cart
    @DeleteMapping("/cart/remove/{id}")
    @Transactional
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id) {
        Optional<Cart> item = cartRepo.findById(id);
        item.ifPresent(ci -> {
            Product p = ci.getProduct();
            if (p != null) {
                p.setQuantity(p.getQuantity() + ci.getQuantity());
                productRepo.save(p);
            }
            cartRepo.delete(ci);
        });
        return ResponseEntity.ok().build();
    }

    //Remove from wishlist
    @DeleteMapping("/wishlist/remove/{id}")
    @Transactional
    public ResponseEntity<Void> removeWishlistItem(@PathVariable Long id) {
        wishlistRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    //Clear cart
    @DeleteMapping("/cart/clear")
    @Transactional
    public ResponseEntity<Void> clearCart(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return ResponseEntity.status(401).build();
        // Restock all items
        List<Cart> items = cartRepo.findByUser(user);
        for (Cart ci : items) {
            Product p = ci.getProduct();
            if (p != null) { p.setQuantity(p.getQuantity() + ci.getQuantity()); productRepo.save(p); }
        }
        cartRepo.deleteAll(items);
        return ResponseEntity.ok().build();
    }

    // Clear wishlist
    @DeleteMapping("/wishlist/clear")
    @Transactional
    public ResponseEntity<Void> clearWishlist(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return ResponseEntity.status(401).build();
        wishlistRepo.deleteAll(wishlistRepo.findByUser(user));
        return ResponseEntity.ok().build();
    }

    // Update cart item quantity
    @PostMapping("/cart/update")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateCartQty(@RequestParam Long cartItemId,
                                                             @RequestParam int quantity) {
        Map<String, Object> res = new HashMap<>();
        if (quantity <= 0) { res.put("error", "Quantity must be positive"); return ResponseEntity.badRequest().body(res); }
        Optional<Cart> opt = cartRepo.findById(cartItemId);
        if (opt.isEmpty()) { res.put("error", "Item not found"); return ResponseEntity.badRequest().body(res); }

        Cart ci = opt.get();
        Product p = ci.getProduct();
        int delta = quantity - ci.getQuantity(); // positive = need more stock, negative = give back

        if (delta > 0) {
            if (p.getQuantity() < delta) { res.put("error", "Insufficient stock"); return ResponseEntity.badRequest().body(res); }
            p.setQuantity(p.getQuantity() - delta);
            productRepo.save(p);
        } else if (delta < 0) {
            p.setQuantity(p.getQuantity() + (-delta));
            productRepo.save(p);
        }

        ci.setQuantity(quantity);
        cartRepo.save(ci);

        res.put("ok", true);
        return ResponseEntity.ok(res);
    }

    //Cart JSON for drawer
    @GetMapping("/cart/json")
    public ResponseEntity<Map<String, Object>> cartJson(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return ResponseEntity.status(401).build();
        List<Cart> items = cartRepo.findByUser(user);
        double total = items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
        Map<String, Object> out = new HashMap<>();
        out.put("items", items.stream().map(i -> Map.of(
                "id", i.getId(),
                "name", i.getProduct().getName(),
                "price", i.getProduct().getPrice(),
                "quantity", i.getQuantity(),
                "cartItemId", i.getId()
        )).toList());
        out.put("counts", Map.of(
                "cartCount", cartRepo.countByUser(user),
                "wishCount", wishlistRepo.countByUser(user)
        ));
        out.put("total", total);
        return ResponseEntity.ok(out);
    }

    //Wishlist JSON for drawer
    @GetMapping("/wishlist/json")
    public ResponseEntity<Map<String, Object>> wishlistJson(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return ResponseEntity.status(401).build();
        List<Wishlist> items = wishlistRepo.findByUser(user);
        Map<String, Object> out = new HashMap<>();
        out.put("items", items.stream().map(i -> Map.of(
                "id", i.getId(),
                "name", i.getProduct().getName(),
                "price", i.getProduct().getPrice(),
                "wishlistItemId", i.getId()
        )).toList());
        out.put("counts", Map.of(
                "cartCount", cartRepo.countByUser(user),
                "wishCount", wishlistRepo.countByUser(user)
        ));
        return ResponseEntity.ok(out);
    }

    // View Orders
    @GetMapping("/orders")
    public String viewOrders(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("orders", orderRepo.findByUser(user));
        return "customer-orders";
    }

    //Checkout
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<Cart> cartItems = cartRepo.findByUser(user);
        if (cartItems.isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            return "redirect:/customer/dashboard";
        }

        double total = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();

        session.setAttribute("checkoutTotal", total);
        return "redirect:/customer/payments";
    }

    //Payment Methods Page
    @GetMapping("/payments")
    public String showPayments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Double total = (Double) session.getAttribute("checkoutTotal");
        model.addAttribute("payments", paymentRepo.findByUser(user));
        model.addAttribute("total", total != null ? total : 0.0);
        return "payment-portal";
    }
    //Select Payment and Continue
    @PostMapping("/payments/select")
    public String selectPayment(@RequestParam Long paymentId, HttpSession session) {
        Payment payment = paymentRepo.findById(paymentId).orElse(null);
        if (payment == null) return "redirect:/customer/payments";

        session.setAttribute("selectedPayment", payment);
        return "redirect:/customer/addresses";
    }

    // Address Management Page
    @GetMapping("/addresses")
    public String viewAddresses(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Double total = (Double) session.getAttribute("checkoutTotal");
        if (user == null) return "redirect:/login";

        model.addAttribute("addresses", addressRepo.findByUser(user));
        model.addAttribute("total", total != null ? total : 0.0);
        return "delivery-portal";
    }

    // Add New Address
    @PostMapping("/addresses/add")
    public String addAddress(@RequestParam String label,
                             @RequestParam String line1,
                             @RequestParam String city,
                             @RequestParam String postalCode,
                             @RequestParam String contactNumber,
                             HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        Address address = new Address();
        address.setUser(user);
        address.setLabel(label);
        address.setLine1(line1);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setContactNumber(contactNumber);
        addressRepo.save(address);

        return "redirect:/customer/addresses";
    }

    //Confirm Order with Selected Address and Payment
    @PostMapping("/confirm-order")
    public String confirmOrder(@RequestParam Long addressId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Double total = (Double) session.getAttribute("checkoutTotal");
        Payment payment = (Payment) session.getAttribute("selectedPayment");

        if (user == null) return "redirect:/login";
        if (total == null) return "redirect:/customer/cart";
        if (payment == null) return "redirect:/customer/payments";

        Address address = addressRepo.findById(addressId).orElse(null);
        if (address == null) return "redirect:/customer/addresses";

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setPaymentStatus("PENDING");
        order.setDeliveryStatus("PENDING");
        order.setAddressLine1(address.getLine1());
        order.setCity(address.getCity());
        order.setPostalCode(address.getPostalCode());
        order.setContactNumber(address.getContactNumber());
        order.setPaymentType(payment.getType());
        orderRepo.save(order);

        cartRepo.deleteAll(cartRepo.findByUser(user));

        session.removeAttribute("checkoutTotal");
        session.removeAttribute("selectedPayment");

        // Removed stock adjustment loop to prevent double mutation

        model.addAttribute("message", "✅ Order placed successfully with selected payment and address.");
        return "redirect:/customer/track";
    }


}