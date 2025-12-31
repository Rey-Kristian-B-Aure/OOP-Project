import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {
    private String orderId;
    private LocalDateTime orderDate;
    private ArrayList<OrderItem> items;
    private double taxRate = 0.12;

    public Order(String orderId) {
        this.orderId = orderId;
        this.orderDate = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public void addItem(OrderItem orderItem) {
        items.add(orderItem);
    }

    public void removeItem(OrderItem orderItem) {
        items.remove(orderItem);
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (OrderItem oi : items) {
            subtotal += oi.getSubtotal();
        }
        return subtotal;
    }

    public double getTax() {
        return getSubtotal() * taxRate;
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    public String getOrderId() { return orderId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public ArrayList<OrderItem> getItems() { return new ArrayList<>(items); }
}