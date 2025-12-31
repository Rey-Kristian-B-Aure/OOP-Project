public class Dessert extends MenuItem {
    private String sugarLevel; // "Low", "Normal", "Extra"
    public static final double SUGAR_SURCHARGE = 10.00; // Extra charge for extra sugar

    public Dessert(String id, String name, double basePrice) {
        super(id, name, basePrice, "Dessert");
        this.sugarLevel = "Normal"; // Default
    }

    public Dessert(String id, String name, double basePrice, String sugarLevel) {
        super(id, name, basePrice, "Dessert");
        this.sugarLevel = sugarLevel;
    }

    public String getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(String sugarLevel) { 
        this.sugarLevel = sugarLevel; 
    }

    @Override
    public double getPrice() {
        double price = getBasePrice();
        // Add surcharge for extra sugar
        if ("Extra".equals(sugarLevel)) {
            price += SUGAR_SURCHARGE;
        }
        return price;
    }

    @Override
    public String toString() {
        return super.getName() + " (" + sugarLevel + " Sugar) - P" + getPrice();
    }
}