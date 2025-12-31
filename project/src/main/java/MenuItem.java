public class MenuItem {
    private String id;
    private String name;
    private double basePrice;
    private String category;
    private boolean available;

    // Constructor
    public MenuItem(String id, String name, double basePrice, String category) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.category = category;
        this.available = true;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public double getPrice() { return basePrice; } // Default price is base price
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return name + " - P" + getPrice();
    }
}