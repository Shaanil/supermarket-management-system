package g115.quickmart.Strategy;

public class PaymentStrategyFactory {
    public static PaymentStrategy getStrategy(String type) {
        if (type == null) throw new IllegalArgumentException("Payment type required");
        switch (type.toUpperCase()) {
            case "CARD":
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                return new CardPaymentStrategy();
            case "COD":
            case "CASH_ON_DELIVERY":
                return new CashOnDeliveryStrategy();
            case "PAYPAL":
                return new PaypalPaymentStrategy();
            default:
                throw new IllegalArgumentException("Unknown payment type: " + type);
        }
    }
}