public class MainDish extends MenuItem {
    private String spiceLevel; // "Mild", "Medium", "Spicy"
    public static final double SPICE_SURCHARGE = 15.00; // Extra charge for spicy

    public MainDish(String id, String name, double basePrice) {
        super(id, name, basePrice, "MainDish");
        this.spiceLevel = "Medium"; // Default
    }

    public MainDish(String id, String name, double basePrice, String spiceLevel) {
        super(id, name, basePrice, "MainDish");
        this.spiceLevel = spiceLevel;
    }

    public String getSpiceLevel() { return spiceLevel; }
    public void setSpiceLevel(String spiceLevel) { 
        this.spiceLevel = spiceLevel; 
    }

    @Override
    public double getPrice() {
        double price = getBasePrice();
        // Add surcharge for spicy level
        if ("Spicy".equals(spiceLevel)) {
            price += SPICE_SURCHARGE;
        }
        return price;
    }

    @Override
    public String toString() {
        return super.getName() + " (" + spiceLevel + ") - P" + getPrice();
    }
}