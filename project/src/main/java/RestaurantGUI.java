import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class RestaurantGUI extends JFrame {
    private Restaurant restaurant;
    private JPanel currentPanel;
    private CardLayout cardLayout;
    
    private JPanel menuItemsPanel;
    private JScrollPane menuScrollPane;

    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    private JLabel lblTotal;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    

    private JTextArea txtSummary;
    
    public RestaurantGUI() {
        restaurant = new Restaurant();
        initializeGUI();
        
        setTitle("Restaurant Order System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeGUI() {
        cardLayout = new CardLayout();
        currentPanel = new JPanel(cardLayout);
        
        JPanel welcomePanel = createWelcomePanel();
        JPanel menuPanel = createMenuPanel();
        JPanel summaryPanel = createSummaryPanel();
        
        currentPanel.add(welcomePanel, "WELCOME");
        currentPanel.add(menuPanel, "MENU");
        currentPanel.add(summaryPanel, "SUMMARY");
        
        add(currentPanel);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 240)); // Light peach background
        
        // Title
        JLabel titleLabel = new JLabel("Restaurant Order System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(139, 69, 19)); 
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(255, 248, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 150, 30, 150);
        
        JButton btnViewMenu = new JButton("VIEW MENU");
        btnViewMenu.setFont(new Font("Arial", Font.BOLD, 20));
        btnViewMenu.setBackground(new Color(210, 180, 140)); 
        btnViewMenu.setForeground(Color.BLACK); 
        btnViewMenu.setFocusPainted(false);
        btnViewMenu.setBorder(BorderFactory.createRaisedBevelBorder());
        btnViewMenu.setPreferredSize(new Dimension(250, 70));
        
        JButton btnExit = new JButton("EXIT");
        btnExit.setFont(new Font("Arial", Font.BOLD, 20));
        btnExit.setBackground(new Color(205, 133, 63)); 
        btnExit.setForeground(Color.BLACK); 
        btnExit.setFocusPainted(false);
        btnExit.setBorder(BorderFactory.createRaisedBevelBorder());
        btnExit.setPreferredSize(new Dimension(250, 70));
        
        centerPanel.add(btnViewMenu, gbc);
        centerPanel.add(btnExit, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JLabel footerLabel = new JLabel("Simple Restaurant Order Management System For Project in oop", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setForeground(new Color(139, 69, 19));
        panel.add(footerLabel, BorderLayout.SOUTH);
        
        // Event listeners
        btnViewMenu.addActionListener(e -> {
            refreshMenuDisplay();
            cardLayout.show(currentPanel, "MENU");
        });
        
        btnExit.addActionListener(e -> System.exit(0));
        
        return panel;
    }
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240)); 
        

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(139, 69, 19));
        JLabel titleLabel = new JLabel("MENU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setBackground(new Color(210, 180, 140));
        btnBack.setForeground(Color.BLACK);
        headerPanel.add(btnBack, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        

        JPanel contentPanel = new JPanel(new BorderLayout());
        

        menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setBackground(new Color(255, 250, 240));
        
        menuScrollPane = new JScrollPane(menuItemsPanel);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        contentPanel.add(menuScrollPane, BorderLayout.CENTER);
        

        JPanel orderSummaryPanel = new JPanel(new BorderLayout());
        orderSummaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "CURRENT ORDER"
        ));
        orderSummaryPanel.setBackground(new Color(255, 250, 245));
        orderSummaryPanel.setPreferredSize(new Dimension(350, 0));
        

        String[] columns = {"Item", "Qty", "Price"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(orderTable);
        

        JPanel totalPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalPanel.setBackground(new Color(255, 250, 245));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Subtotal
        totalPanel.add(new JLabel("Subtotal:", JLabel.RIGHT));
        lblSubtotal = new JLabel("P0.00");
        lblSubtotal.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(lblSubtotal);
        // Tax
        totalPanel.add(new JLabel("Tax (12%):", JLabel.RIGHT));
        lblTax = new JLabel("P0.00");
        lblTax.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(lblTax);
        // Total
        totalPanel.add(new JLabel("TOTAL:", JLabel.RIGHT));
        lblTotal = new JLabel("P0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 100, 0));
        totalPanel.add(lblTotal);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(255, 250, 245));
        
        JButton btnCancel = new JButton("CANCEL ORDER");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(220, 80, 80));
        btnCancel.setForeground(Color.BLACK); // Black text
        btnCancel.setFocusPainted(false);
        
        JButton btnCheckout = new JButton("PROCEED TO CHECKOUT");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(144, 238, 144)); // Light green
        btnCheckout.setForeground(Color.BLACK); // Black text
        btnCheckout.setFocusPainted(false);
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnCheckout);
        
        orderSummaryPanel.add(totalPanel, BorderLayout.NORTH);
        orderSummaryPanel.add(tableScroll, BorderLayout.CENTER);
        orderSummaryPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(orderSummaryPanel, BorderLayout.EAST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Event listeners
        btnBack.addActionListener(e -> cardLayout.show(currentPanel, "WELCOME"));
        btnCancel.addActionListener(e -> cancelOrder());
        btnCheckout.addActionListener(e -> {
            if (restaurant.getCurrentOrder() == null || restaurant.getCurrentOrder().getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add items to your order first!");
            } else {
                showSummary();
                cardLayout.show(currentPanel, "SUMMARY");
            }
        });
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 250, 240));
        

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(139, 69, 19));
        JLabel titleLabel = new JLabel("ORDER SUMMARY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton btnBack = new JButton("← Back to Menu");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setBackground(new Color(210, 180, 140));
        btnBack.setForeground(Color.BLACK);
        headerPanel.add(btnBack, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Summary text area
        txtSummary = new JTextArea();
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtSummary.setForeground(Color.BLACK);
        txtSummary.setBackground(new Color(255, 253, 250));
        txtSummary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane summaryScroll = new JScrollPane(txtSummary);
        summaryScroll.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));
        panel.add(summaryScroll, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(new Color(255, 250, 240));
        
        JButton btnBackToMenu = new JButton("BACK TO MENU");
        btnBackToMenu.setFont(new Font("Arial", Font.BOLD, 16));
        btnBackToMenu.setBackground(new Color(210, 180, 140));
        btnBackToMenu.setForeground(Color.BLACK); // Black text
        btnBackToMenu.setFocusPainted(false);
        btnBackToMenu.setPreferredSize(new Dimension(180, 45));
        
        JButton btnCheckout = new JButton("CHECKOUT");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 16));
        btnCheckout.setBackground(new Color(144, 238, 144)); // Light green
        btnCheckout.setForeground(Color.BLACK); // Black text
        btnCheckout.setFocusPainted(false);
        btnCheckout.setPreferredSize(new Dimension(180, 45));
        
        buttonPanel.add(btnBackToMenu);
        buttonPanel.add(btnCheckout);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event listeners
        btnBack.addActionListener(e -> cardLayout.show(currentPanel, "MENU"));
        btnBackToMenu.addActionListener(e -> cardLayout.show(currentPanel, "MENU"));
        btnCheckout.addActionListener(e -> finalizeOrder());
        
        return panel;
    }
    
    private void refreshMenuDisplay() {
        menuItemsPanel.removeAll();
        
        // Create category sections
        addCategorySection("MAIN DISHES", restaurant.getMenuByCategory("MainDish"));
        addCategorySection("BEVERAGES", restaurant.getMenuByCategory("Beverage"));
        addCategorySection("DESSERTS", restaurant.getMenuByCategory("Dessert"));
        
        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
        refreshOrderDisplay();
    }
    
    private void addCategorySection(String categoryName, ArrayList<MenuItem> items) {
        if (items.isEmpty()) return;
        
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(new Color(255, 250, 240));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel categoryLabel = new JLabel(categoryName);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        categoryLabel.setForeground(new Color(139, 69, 19)); // Brown
        categoryPanel.add(categoryLabel, BorderLayout.WEST);
        
        menuItemsPanel.add(categoryPanel);
        
  
        JPanel itemsGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        itemsGrid.setBackground(new Color(255, 250, 240));
        itemsGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        for (MenuItem item : items) {
            itemsGrid.add(createMenuItemCard(item));
        }
        
        menuItemsPanel.add(itemsGrid);
        

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(210, 180, 140));
        separator.setBackground(new Color(210, 180, 140));
        menuItemsPanel.add(separator);
    }
    
    private JPanel createMenuItemCard(MenuItem item) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        cardPanel.setBackground(Color.WHITE);
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        JLabel lblName = new JLabel(item.getName());
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        lblName.setForeground(Color.BLACK);
        infoPanel.add(lblName, gbc);
        
        String priceText = String.format("P%.2f", item.getPrice());
        if (item instanceof Beverage) {
            Beverage bev = (Beverage) item;
            priceText += " (" + bev.getSize() + ")";
        }
        
        JLabel lblPrice = new JLabel(priceText);
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPrice.setForeground(new Color(0, 100, 0)); // Green for price
        infoPanel.add(lblPrice, gbc);
        
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        quantityPanel.setBackground(Color.WHITE);

        JButton btnMinus = new JButton("-");
        btnMinus.setFont(new Font("Arial", Font.BOLD, 16));
        btnMinus.setBackground(new Color(255, 200, 200));
        btnMinus.setForeground(Color.BLACK);
        btnMinus.setFocusPainted(false);
        btnMinus.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        btnMinus.setPreferredSize(new Dimension(40, 30));
        
        JLabel lblQuantity = new JLabel("0");
        lblQuantity.setFont(new Font("Arial", Font.BOLD, 16));
        lblQuantity.setForeground(Color.BLACK);
        lblQuantity.setPreferredSize(new Dimension(40, 30));
        lblQuantity.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuantity.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JButton btnPlus = new JButton("+");
        btnPlus.setFont(new Font("Arial", Font.BOLD, 16));
        btnPlus.setBackground(new Color(200, 255, 200));
        btnPlus.setForeground(Color.BLACK);
        btnPlus.setFocusPainted(false);
        btnPlus.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        btnPlus.setPreferredSize(new Dimension(40, 30));
        
        // Add button
        JButton btnAdd = new JButton("ADD");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setBackground(new Color(70, 130, 180));
        btnAdd.setForeground(Color.BLACK); 
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(80, 30));
        
        quantityPanel.add(btnMinus);
        quantityPanel.add(lblQuantity);
        quantityPanel.add(btnPlus);
        quantityPanel.add(Box.createHorizontalStrut(10));
        quantityPanel.add(btnAdd);
        
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(quantityPanel, BorderLayout.SOUTH);
        
        // Event listeners
        btnMinus.addActionListener(e -> {
            int current = Integer.parseInt(lblQuantity.getText());
            if (current > 0) {
                lblQuantity.setText(String.valueOf(current - 1));
            }
        });
        
        btnPlus.addActionListener(e -> {
            int current = Integer.parseInt(lblQuantity.getText());
            lblQuantity.setText(String.valueOf(current + 1));
        });
        
        btnAdd.addActionListener(e -> {
            int quantity = Integer.parseInt(lblQuantity.getText());
            if (quantity > 0) {
                restaurant.addToCurrentOrder(item, quantity);
                refreshOrderDisplay();
                lblQuantity.setText("0");
                JOptionPane.showMessageDialog(this, 
                    quantity + " x " + item.getName() + " added to order!",
                    "Item Added",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select quantity first!",
                    "No Quantity",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        return cardPanel;
    }
    
    private void refreshOrderDisplay() {
        orderTableModel.setRowCount(0);
        
        if (restaurant.getCurrentOrder() == null) {
            lblSubtotal.setText("P0.00");
            lblTax.setText("P0.00");
            lblTotal.setText("P0.00");
            return;
        }
        
        Order currentOrder = restaurant.getCurrentOrder();
        
        // Add items to table
        for (OrderItem orderItem : currentOrder.getItems()) {
            MenuItem item = orderItem.getItem();
            String itemName = item.getName();
            if (item instanceof Beverage) {
                itemName += " (" + ((Beverage) item).getSize() + ")";
            }
            
            Object[] row = {
                itemName,
                orderItem.getQuantity(),
                String.format("P%.2f", orderItem.getSubtotal())
            };
            orderTableModel.addRow(row);
        }
        
        // Update total label
        lblSubtotal.setText(String.format("P%.2f", currentOrder.getSubtotal()));
        lblTax.setText(String.format("P%.2f", currentOrder.getTax()));
        lblTotal.setText(String.format("P%.2f", currentOrder.getTotal()));
    }
    
    private void cancelOrder() {
        if (restaurant.getCurrentOrder() == null || restaurant.getCurrentOrder().getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order is already empty!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel the order?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            restaurant.createNewOrder(); // Create fresh empty order
            refreshOrderDisplay();
            JOptionPane.showMessageDialog(this, "Order cancelled!");
        }
    }
    
    private void showSummary() {
        if (restaurant.getCurrentOrder() == null) return;
        
        Order currentOrder = restaurant.getCurrentOrder();
        StringBuilder summary = new StringBuilder();
        
        summary.append("========================================\n");
        summary.append("           ORDER SUMMARY\n");
        summary.append("========================================\n\n");
        summary.append("Order ID: ").append(currentOrder.getOrderId()).append("\n");
        summary.append("Date: ").append(java.time.LocalDateTime.now()).append("\n\n");
        summary.append("Items Ordered:\n");
        summary.append("----------------------------------------\n");
        
        for (OrderItem orderItem : currentOrder.getItems()) {
            MenuItem item = orderItem.getItem();
            String itemName = item.getName();
            if (item instanceof Beverage) {
                itemName += " (" + ((Beverage) item).getSize() + ")";
            }
            
            summary.append(String.format("%-25s x%d  P%8.2f\n",
                itemName,
                orderItem.getQuantity(),
                orderItem.getSubtotal()));
        }
        
        summary.append("----------------------------------------\n");
        summary.append(String.format("Subtotal:               P%9.2f\n", currentOrder.getSubtotal()));
        summary.append(String.format("Tax (12%%):              P%9.2f\n", currentOrder.getTax()));
        summary.append(String.format("TOTAL:                  P%9.2f\n", currentOrder.getTotal()));
        summary.append("========================================\n\n");
        summary.append("Thank you for your order!\n");
        
        txtSummary.setText(summary.toString());
    }
    
    private void finalizeOrder() {
        Order currentOrder = restaurant.getCurrentOrder();
        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order to finalize!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm checkout? This will save the order.",
            "Confirm Checkout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Finalize and save the order
            restaurant.finalizeCurrentOrder();
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Order placed successfully!\n\n" +
                "Order ID: " + currentOrder.getOrderId() + "\n" +
                "Total: " + String.format("P%.2f", currentOrder.getTotal()) + "\n\n" +
                "Thank you for your order!",
                "Order Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reset and go back to welcome screen
            restaurant.createNewOrder();
            refreshOrderDisplay();
            cardLayout.show(currentPanel, "WELCOME");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RestaurantGUI();
        });
    }
}