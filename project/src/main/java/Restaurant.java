import java.util.ArrayList;

public class Restaurant {
    private ArrayList<MenuItem> menu;
    private Order currentOrder;

    public Restaurant() {
        menu = new ArrayList<>();
        loadSampleMenu();
        createNewOrder(); 
    }

    private void loadSampleMenu() {
        // Main Dishes with different spice levels
        menu.add(new MainDish("M001", "Chicken Curry", 150.00, "Spicy"));
        menu.add(new MainDish("M002", "Spaghetti Bolognese", 120.00, "Medium"));
        menu.add(new MainDish("M003", "Beef Stir Fry", 140.00, "Spicy"));
        menu.add(new MainDish("M004", "Chicken Alfredo", 130.00, "Mild"));
        menu.add(new MainDish("M005", "Vegetable Curry", 110.00, "Medium"));

        // Beverages - 5 unique items with base prices
        menu.add(new Beverage("B001", "Coke", 40.00, "Medium"));
        menu.add(new Beverage("B002", "Sprite", 40.00, "Medium"));
        menu.add(new Beverage("B003", "Iced Tea", 35.00, "Medium"));
        menu.add(new Beverage("B004", "Blue Lemonade", 45.00, "Medium"));
        menu.add(new Beverage("B005", "Pineapple Juice", 42.00, "Medium"));

        // Desserts with different sugar levels
        menu.add(new Dessert("D001", "Chocolate Cake", 100.00, "Normal"));
        menu.add(new Dessert("D002", "Cheesecake", 120.00, "Low"));
        menu.add(new Dessert("D003", "Ice Cream Sundae", 90.00, "Extra"));
        menu.add(new Dessert("D004", "Fruit Tart", 85.00, "Normal"));
        menu.add(new Dessert("D005", "Brownies", 95.00, "Extra"));
    }

    public ArrayList<MenuItem> getMenuByCategory(String category) {
        ArrayList<MenuItem> result = new ArrayList<>();
        for (MenuItem item : menu) {
            if (item.getCategory().equals(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<MenuItem> getAllMenu() {
        return new ArrayList<>(menu);
    }

    public void createNewOrder() {
        String orderId = "ORD-" + System.currentTimeMillis();
        currentOrder = new Order(orderId);
    }

    public void addToCurrentOrder(MenuItem item, int quantity) {
        if (currentOrder == null) createNewOrder();
        OrderItem orderItem = new OrderItem(item, quantity);
        currentOrder.addItem(orderItem);
    }

    public void finalizeCurrentOrder() {
        if (currentOrder != null && !currentOrder.getItems().isEmpty()) {
            createNewOrder();
        }
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}