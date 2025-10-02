package g115.quickmart.Strategy;

import g115.quickmart.Model.Payment;

public class PaypalPaymentStrategy implements PaymentStrategy {
    @Override
    public void process(Payment pm) {
        pm.setCardHolderName(null);
        pm.setCardNumber(null);
        pm.setExpiry(null);
        pm.setCustomMessage("PayPal payment created");
    }
}