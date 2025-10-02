package g115.quickmart.Strategy;

import g115.quickmart.Model.Payment;

public interface PaymentStrategy {
    void process(Payment pm);
}