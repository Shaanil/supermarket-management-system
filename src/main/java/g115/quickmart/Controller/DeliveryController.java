package g115.quickmart.Controller;

import g115.quickmart.Model.Order;
import g115.quickmart.Model.Driver;
import g115.quickmart.Repo.OrderRepo;
import g115.quickmart.Repo.DriverRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final OrderRepo ordersRepo;
    private final DriverRepo driverRepo;

    public DeliveryController(OrderRepo ordersRepo, DriverRepo driverRepo) {
        this.ordersRepo = ordersRepo;
        this.driverRepo = driverRepo;
    }
    //Read Drivers and Orders
    @GetMapping("/dashboard")
    public String deliveryDashboard(Model model) {
        model.addAttribute("orders", ordersRepo.findAll());
        model.addAttribute("drivers", driverRepo.findAll());
        System.out.println("Delivery - Read");
        return "delivery-dashboard";
    }
    //Update - Assign Driver to Order
    @PostMapping("/assign")
    public String assignDriver(@RequestParam Long orderId, @RequestParam Long driverId) {
        Order o = ordersRepo.findById(orderId).orElse(null);
        Driver d = driverRepo.findById(driverId).orElse(null);
        if (o != null && d != null) {
            o.setDriver(d);
            d.setStatus("ON_DELIVERY");
            driverRepo.save(d);
            o.setDeliveryStatus("ASSIGNED");
            ordersRepo.save(o);
        }
        System.out.println("Delivery - Update (Order&Driver)");
        return "redirect:/delivery/dashboard";
    }
    //Update Delivery Status
    @PostMapping("/status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam String status) {
        Order o = ordersRepo.findById(orderId).orElse(null);
        if (o != null) {
            if ("RECEIVED".equals(status) && "APPROVED".equals(o.getPaymentStatus())) {
                o.setDeliveryStatus("RECEIVED");
                if (o.getDriver() != null) {
                    o.getDriver().setStatus("ON_DELIVERY");
                    driverRepo.save(o.getDriver());
                }}
            else if ("SHIPPED".equals(status) && "APPROVED".equals(o.getPaymentStatus()) && ("PENDING".equals(o.getDeliveryStatus()) || "RECEIVED".equals(o.getDeliveryStatus()))) {
                o.setDeliveryStatus("SHIPPED");
                if (o.getDriver() != null) {
                    o.getDriver().setStatus("ON_DELIVERY");
                    driverRepo.save(o.getDriver());
                }}
            else if ("DELIVERED".equals(status) && "SHIPPED".equals(o.getDeliveryStatus())) {
                o.setDeliveryStatus("DELIVERED");
                if (o.getDriver() != null) {
                    o.getDriver().setStatus("AVAILABLE");
                    driverRepo.save(o.getDriver());
                }
            }
            ordersRepo.save(o);
        }
        System.out.println("Delivery - Update Order");
        return "redirect:/delivery/dashboard";
    }
    //Update Driver Status in Driver Overview
    @PostMapping("/drivers/status")
    public String updateDriverStatus(@RequestParam Long driverId,
                                     @RequestParam String status) {
        Driver d = driverRepo.findById(driverId).orElse(null);
        if (d != null) {
            d.setStatus(status);
            driverRepo.save(d);
        }
        System.out.println("Delivery - Update(Driver Status)");
        return "redirect:/delivery/dashboard";
    }
    //Remove Drivers
    @PostMapping("/drivers/remove/{driverId}")
    public String removeDriver(@PathVariable Long driverId) {
        Driver driver = driverRepo.findById(driverId).orElse(null);
        if (driver != null) {
            // Unassign the drinver before deletion
            List<Order> assignedOrders = ordersRepo.findByDriver(driver);
            for (Order order : assignedOrders) {
                order.setDriver(null);
                order.setDeliveryStatus("UNASSIGNED");
                ordersRepo.save(order);
            }
            driverRepo.delete(driver);
        }
        System.out.println("Delivery - Delete (Driver)");
        return "redirect:/delivery/dashboard";
    }
    //Create Drivers
    @PostMapping("/drivers/add")
    public String addDriver(@RequestParam String name,
                            @RequestParam String contactNumber,
                            @RequestParam String vehicleNumber) {
        Driver d = new Driver();
        d.setName(name);
        d.setContactNumber(contactNumber);
        d.setVehicleNumber(vehicleNumber);
        d.setStatus("AVAILABLE");
        driverRepo.save(d);
        System.out.println("Delivery - Create Driver");
        return "redirect:/delivery/dashboard";
    }


    //Report
    @GetMapping("/report")
    public ResponseEntity<byte[]> deliveryReport() {
        List<Order> orders = (List<Order>) ordersRepo.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter w = new PrintWriter(baos);
        w.println("OrderID,Customer,Amount,PaymentType,PaymentStatus,DeliveryStatus,Address,City,Postal,Contact,Driver");
        for (Order o : orders) {
            w.printf("%d,%s,%.2f,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    o.getId(),
                    o.getUser()!=null?o.getUser().getUsername():"",
                    o.getTotalAmount(),
                    o.getPaymentType(),
                    o.getPaymentStatus(),
                    o.getDeliveryStatus(),
                    o.getAddressLine1(),
                    o.getCity(),
                    o.getPostalCode(),
                    o.getContactNumber(),
                    (o.getDriver() != null ? o.getDriver().getName() : ""));
        }
        w.flush();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=delivery_report.csv")
                .body(baos.toByteArray());
    }

}