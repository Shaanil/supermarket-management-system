package g115.quickmart.Strategy;

import g115.quickmart.Model.Payment;

public class CashOnDeliveryStrategy implements PaymentStrategy {
        @Override
        public void process(Payment pm) {
            pm.setCardHolderName(null);
            pm.setCardNumber(null);
            pm.setExpiry(null);
            pm.setCustomMessage("Cash on Delivery selected");
        }
    }
