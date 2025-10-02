package g115.quickmart.Strategy;
import g115.quickmart.Model.Payment;

public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public void process(Payment pm) {
        String number = pm.getCardNumber();
        if (number != null && number.length() >= 4) {
            String last4 = number.substring(number.length() - 4);
            pm.setCardNumber("**** **** **** " + last4);
        }
        pm.setCustomMessage("Card payment created");
    }
}