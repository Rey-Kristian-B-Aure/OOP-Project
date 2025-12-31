import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
        setSize(1000, 700);
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
        panel.setBackground(new Color(255, 248, 240));
        
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
        
        JLabel footerLabel = new JLabel("Simple Restaurant Order Management System with Customization Options", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setForeground(new Color(139, 69, 19));
        panel.add(footerLabel, BorderLayout.SOUTH);
        
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
        
        totalPanel.add(new JLabel("Subtotal:", JLabel.RIGHT));
        lblSubtotal = new JLabel("P0.00");
        lblSubtotal.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(lblSubtotal);
        
        totalPanel.add(new JLabel("Tax (12%):", JLabel.RIGHT));
        lblTax = new JLabel("P0.00");
        lblTax.setFont(new Font("Arial", Font.PLAIN, 14));
        totalPanel.add(lblTax);
        
        totalPanel.add(new JLabel("TOTAL:", JLabel.RIGHT));
        lblTotal = new JLabel("P0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 100, 0));
        totalPanel.add(lblTotal);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(255, 250, 245));
        
        JButton btnCancel = new JButton("CANCEL ORDER");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancel.setBackground(new Color(220, 80, 80));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFocusPainted(false);
        
        JButton btnCheckout = new JButton("PROCEED TO CHECKOUT");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(144, 238, 144));
        btnCheckout.setForeground(Color.BLACK);
        btnCheckout.setFocusPainted(false);
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnCheckout);
        
        orderSummaryPanel.add(totalPanel, BorderLayout.NORTH);
        orderSummaryPanel.add(tableScroll, BorderLayout.CENTER);
        orderSummaryPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(orderSummaryPanel, BorderLayout.EAST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
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
        
        txtSummary = new JTextArea();
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtSummary.setForeground(Color.BLACK);
        txtSummary.setBackground(new Color(255, 253, 250));
        txtSummary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane summaryScroll = new JScrollPane(txtSummary);
        summaryScroll.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 1));
        panel.add(summaryScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(new Color(255, 250, 240));
        
        JButton btnBackToMenu = new JButton("BACK TO MENU");
        btnBackToMenu.setFont(new Font("Arial", Font.BOLD, 16));
        btnBackToMenu.setBackground(new Color(210, 180, 140));
        btnBackToMenu.setForeground(Color.BLACK);
        btnBackToMenu.setFocusPainted(false);
        btnBackToMenu.setPreferredSize(new Dimension(180, 45));
        
        JButton btnCheckout = new JButton("CHECKOUT");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 16));
        btnCheckout.setBackground(new Color(144, 238, 144));
        btnCheckout.setForeground(Color.BLACK);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setPreferredSize(new Dimension(180, 45));
        
        buttonPanel.add(btnBackToMenu);
        buttonPanel.add(btnCheckout);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        btnBack.addActionListener(e -> cardLayout.show(currentPanel, "MENU"));
        btnBackToMenu.addActionListener(e -> cardLayout.show(currentPanel, "MENU"));
        btnCheckout.addActionListener(e -> finalizeOrder());
        
        return panel;
    }
    
    private void refreshMenuDisplay() {
        menuItemsPanel.removeAll();
        
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
        categoryLabel.setForeground(new Color(139, 69, 19));
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
        
        String priceText = "";
        if (item instanceof Beverage) {
            Beverage bev = (Beverage) item;
            priceText = String.format("Base: P%.2f", bev.getBasePrice());
        } else if (item instanceof MainDish) {
            MainDish dish = (MainDish) item;
            priceText = String.format("Base: P%.2f", dish.getBasePrice());
        } else if (item instanceof Dessert) {
            Dessert dessert = (Dessert) item;
            priceText = String.format("Base: P%.2f", dessert.getBasePrice());
        } else {
            priceText = String.format("P%.2f", item.getPrice());
        }
        
        JLabel lblPrice = new JLabel(priceText);
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPrice.setForeground(new Color(0, 100, 0));
        infoPanel.add(lblPrice, gbc);
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        ButtonGroup optionsGroup = new ButtonGroup();
        
        if (item instanceof MainDish) {
            MainDish dish = (MainDish) item;
            JRadioButton rbMild = new JRadioButton("Mild - P" + String.format("%.2f", dish.getBasePrice()));
            JRadioButton rbMedium = new JRadioButton("Medium - P" + String.format("%.2f", dish.getBasePrice()));
            JRadioButton rbSpicy = new JRadioButton("Spicy - P" + String.format("%.2f", dish.getBasePrice() + MainDish.SPICE_SURCHARGE));
            
            rbMedium.setSelected(true);
            
            optionsGroup.add(rbMild);
            optionsGroup.add(rbMedium);
            optionsGroup.add(rbSpicy);
            
            optionsPanel.add(rbMild);
            optionsPanel.add(rbMedium);
            optionsPanel.add(rbSpicy);
            
            cardPanel.putClientProperty("mainDish", dish);
            cardPanel.putClientProperty("rbMild", rbMild);
            cardPanel.putClientProperty("rbMedium", rbMedium);
            cardPanel.putClientProperty("rbSpicy", rbSpicy);
            
        } else if (item instanceof Dessert) {
            Dessert dessert = (Dessert) item;
            JRadioButton rbLow = new JRadioButton("Low Sugar - P" + String.format("%.2f", dessert.getBasePrice()));
            JRadioButton rbNormal = new JRadioButton("Normal Sugar - P" + String.format("%.2f", dessert.getBasePrice()));
            JRadioButton rbExtra = new JRadioButton("Extra Sugar - P" + String.format("%.2f", dessert.getBasePrice() + Dessert.SUGAR_SURCHARGE));
            
            rbNormal.setSelected(true);
            
            optionsGroup.add(rbLow);
            optionsGroup.add(rbNormal);
            optionsGroup.add(rbExtra);
            
            optionsPanel.add(rbLow);
            optionsPanel.add(rbNormal);
            optionsPanel.add(rbExtra);
            
            cardPanel.putClientProperty("dessert", dessert);
            cardPanel.putClientProperty("rbLow", rbLow);
            cardPanel.putClientProperty("rbNormal", rbNormal);
            cardPanel.putClientProperty("rbExtra", rbExtra);
            
        } else if (item instanceof Beverage) {
            Beverage beverage = (Beverage) item;
            JRadioButton rbSmall = new JRadioButton("Small - P" + String.format("%.2f", beverage.getBasePrice()));
            JRadioButton rbMedium = new JRadioButton("Medium - P" + String.format("%.2f", beverage.getBasePrice() + 10.00));
            JRadioButton rbLarge = new JRadioButton("Large - P" + String.format("%.2f", beverage.getBasePrice() + 20.00));
            
            rbMedium.setSelected(true);
            
            optionsGroup.add(rbSmall);
            optionsGroup.add(rbMedium);
            optionsGroup.add(rbLarge);
            
            optionsPanel.add(rbSmall);
            optionsPanel.add(rbMedium);
            optionsPanel.add(rbLarge);
            
            cardPanel.putClientProperty("beverage", beverage);
            cardPanel.putClientProperty("rbSmall", rbSmall);
            cardPanel.putClientProperty("rbMedium", rbMedium);
            cardPanel.putClientProperty("rbLarge", rbLarge);
        }
        
        if (optionsPanel.getComponentCount() > 0) {
            infoPanel.add(optionsPanel, gbc);
        }
        
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
                MenuItem itemToAdd = null;
                
                if (item instanceof MainDish) {
                    MainDish original = (MainDish) cardPanel.getClientProperty("mainDish");
                    JRadioButton rbMild = (JRadioButton) cardPanel.getClientProperty("rbMild");
                    JRadioButton rbMedium = (JRadioButton) cardPanel.getClientProperty("rbMedium");
                    JRadioButton rbSpicy = (JRadioButton) cardPanel.getClientProperty("rbSpicy");
                    
                    MainDish modified = new MainDish(
                        original.getId(), 
                        original.getName(), 
                        original.getBasePrice()
                    );
                    
                    if (rbMild.isSelected()) modified.setSpiceLevel("Mild");
                    else if (rbMedium.isSelected()) modified.setSpiceLevel("Medium");
                    else if (rbSpicy.isSelected()) modified.setSpiceLevel("Spicy");
                    
                    itemToAdd = modified;
                    
                } else if (item instanceof Dessert) {
                    Dessert original = (Dessert) cardPanel.getClientProperty("dessert");
                    JRadioButton rbLow = (JRadioButton) cardPanel.getClientProperty("rbLow");
                    JRadioButton rbNormal = (JRadioButton) cardPanel.getClientProperty("rbNormal");
                    JRadioButton rbExtra = (JRadioButton) cardPanel.getClientProperty("rbExtra");
                    
                    Dessert modified = new Dessert(
                        original.getId(), 
                        original.getName(), 
                        original.getBasePrice()
                    );
                    
                    if (rbLow.isSelected()) modified.setSugarLevel("Low");
                    else if (rbNormal.isSelected()) modified.setSugarLevel("Normal");
                    else if (rbExtra.isSelected()) modified.setSugarLevel("Extra");
                    
                    itemToAdd = modified;
                    
                } else if (item instanceof Beverage) {
                    Beverage original = (Beverage) cardPanel.getClientProperty("beverage");
                    JRadioButton rbSmall = (JRadioButton) cardPanel.getClientProperty("rbSmall");
                    JRadioButton rbMedium = (JRadioButton) cardPanel.getClientProperty("rbMedium");
                    JRadioButton rbLarge = (JRadioButton) cardPanel.getClientProperty("rbLarge");
                    
                    String size = "Medium";
                    double price = original.getBasePrice() + 10.00;
                    
                    if (rbSmall.isSelected()) {
                        size = "Small";
                        price = original.getBasePrice();
                    } else if (rbMedium.isSelected()) {
                        size = "Medium";
                        price = original.getBasePrice() + 10.00;
                    } else if (rbLarge.isSelected()) {
                        size = "Large";
                        price = original.getBasePrice() + 20.00;
                    }
                    
                    Beverage modified = new Beverage(
                        original.getId(), 
                        original.getName(), 
                        price, 
                        size
                    );
                    
                    itemToAdd = modified;
                    
                } else {
                    itemToAdd = item;
                }
                
                restaurant.addToCurrentOrder(itemToAdd, quantity);
                refreshOrderDisplay();
                lblQuantity.setText("0");
                JOptionPane.showMessageDialog(this, 
                    quantity + " x " + itemToAdd.getName() + " added to order!\n" +
                    "Price: P" + String.format("%.2f", itemToAdd.getPrice()),
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
        
        for (OrderItem orderItem : currentOrder.getItems()) {
            MenuItem item = orderItem.getItem();
            String itemName = item.getName();
            
            if (item instanceof Beverage) {
                itemName += " (" + ((Beverage) item).getSize() + ")";
            } else if (item instanceof MainDish) {
                MainDish dish = (MainDish) item;
                itemName += " (" + dish.getSpiceLevel() + ")";
            } else if (item instanceof Dessert) {
                Dessert dessert = (Dessert) item;
                itemName += " (" + dessert.getSugarLevel() + " Sugar)";
            }
            
            Object[] row = {
                itemName,
                orderItem.getQuantity(),
                String.format("P%.2f", orderItem.getSubtotal())
            };
            orderTableModel.addRow(row);
        }
        
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
            restaurant.createNewOrder();
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
            } else if (item instanceof MainDish) {
                MainDish dish = (MainDish) item;
                itemName += " (" + dish.getSpiceLevel() + ")";
            } else if (item instanceof Dessert) {
                Dessert dessert = (Dessert) item;
                itemName += " (" + dessert.getSugarLevel() + " Sugar)";
            }
            
            summary.append(String.format("%-30s x%d  P%8.2f\n",
                itemName,
                orderItem.getQuantity(),
                orderItem.getSubtotal()));
        }
        
        summary.append("----------------------------------------\n");
        summary.append(String.format("Subtotal:               P%9.2f\n", currentOrder.getSubtotal()));
        summary.append(String.format("Tax (12%%):              P%9.2f\n", currentOrder.getTax()));
        summary.append(String.format("TOTAL:                  P%9.2f\n", currentOrder.getTotal()));
        summary.append("========================================\n\n");
        summary.append("Note: Spicy dishes add P" + MainDish.SPICE_SURCHARGE + "\n");
        summary.append("      Extra sugar adds P" + Dessert.SUGAR_SURCHARGE + "\n");
        summary.append("      Medium drinks: +P10.00, Large drinks: +P20.00\n");
        summary.append("Thank you for your order!\n");
        
        txtSummary.setText(summary.toString());
    }
    
    private void saveReceiptToFile(Order order) {
        try {
            // Create receipts directory if it doesn't exist
            File receiptsDir = new File("receipts");
            if (!receiptsDir.exists()) {
                receiptsDir.mkdir();
            }
            
            // Create filename with order ID and timestamp
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "receipts/" + order.getOrderId() + "_" + timestamp + ".txt";
            
            File receiptFile = new File(fileName);
            
            try (PrintWriter writer = new PrintWriter(receiptFile)) {
                // Write receipt header
                writer.println("========================================");
                writer.println("         RESTAURANT RECEIPT");
                writer.println("========================================");
                writer.println();
                writer.println("Order ID: " + order.getOrderId());
                writer.println("Date: " + java.time.LocalDateTime.now());
                writer.println();
                writer.println("Items Ordered:");
                writer.println("----------------------------------------");
                
                // Write items
                for (OrderItem orderItem : order.getItems()) {
                    MenuItem item = orderItem.getItem();
                    String itemName = item.getName();
                    
                    if (item instanceof Beverage) {
                        itemName += " (" + ((Beverage) item).getSize() + ")";
                    } else if (item instanceof MainDish) {
                        MainDish dish = (MainDish) item;
                        itemName += " (" + dish.getSpiceLevel() + ")";
                    } else if (item instanceof Dessert) {
                        Dessert dessert = (Dessert) item;
                        itemName += " (" + dessert.getSugarLevel() + " Sugar)";
                    }
                    
                    writer.println(String.format("%-30s x%d  P%8.2f",
                        itemName,
                        orderItem.getQuantity(),
                        orderItem.getSubtotal()));
                }
                
                // Write totals
                writer.println("----------------------------------------");
                writer.println(String.format("Subtotal:               P%9.2f", order.getSubtotal()));
                writer.println(String.format("Tax (12%%):              P%9.2f", order.getTax()));
                writer.println(String.format("TOTAL:                  P%9.2f", order.getTotal()));
                writer.println("========================================");
                writer.println();
                writer.println("Thank you for your order!");
                writer.println("Receipt saved to: " + receiptFile.getAbsolutePath());
            }
            
            System.out.println("Receipt saved to: " + receiptFile.getAbsolutePath());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving receipt: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void viewReceipt(Order order) {
        StringBuilder receipt = new StringBuilder();
        
        receipt.append("========================================\n");
        receipt.append("         RESTAURANT RECEIPT\n");
        receipt.append("========================================\n\n");
        receipt.append("Order ID: ").append(order.getOrderId()).append("\n");
        receipt.append("Date: ").append(java.time.LocalDateTime.now()).append("\n\n");
        receipt.append("Items Ordered:\n");
        receipt.append("----------------------------------------\n");
        
        for (OrderItem orderItem : order.getItems()) {
            MenuItem item = orderItem.getItem();
            String itemName = item.getName();
            
            if (item instanceof Beverage) {
                itemName += " (" + ((Beverage) item).getSize() + ")";
            } else if (item instanceof MainDish) {
                MainDish dish = (MainDish) item;
                itemName += " (" + dish.getSpiceLevel() + ")";
            } else if (item instanceof Dessert) {
                Dessert dessert = (Dessert) item;
                itemName += " (" + dessert.getSugarLevel() + " Sugar)";
            }
            
            receipt.append(String.format("%-30s x%d  P%8.2f\n",
                itemName,
                orderItem.getQuantity(),
                orderItem.getSubtotal()));
        }
        
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("Subtotal:               P%9.2f\n", order.getSubtotal()));
        receipt.append(String.format("Tax (12%%):              P%9.2f\n", order.getTax()));
        receipt.append(String.format("TOTAL:                  P%9.2f\n", order.getTotal()));
        receipt.append("========================================\n\n");
        receipt.append("Thank you for your order!\n");
        
        // Create a dialog to show the receipt
        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Order Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void finalizeOrder() {
        Order currentOrder = restaurant.getCurrentOrder();
        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No order to finalize!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm checkout? This will save the order and generate a receipt.",
            "Confirm Checkout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Save receipt to file
            saveReceiptToFile(currentOrder);
            
            // Finalize and save the order
            restaurant.finalizeCurrentOrder();
            
            // Ask if user wants to view the receipt
            int viewReceipt = JOptionPane.showConfirmDialog(this,
                "Order placed successfully!\n\n" +
                "Order ID: " + currentOrder.getOrderId() + "\n" +
                "Total: " + String.format("P%.2f", currentOrder.getTotal()) + "\n\n" +
                "Receipt has been saved to the 'receipts' folder.\n" +
                "Would you like to view the receipt now?",
                "Order Successful",
                JOptionPane.YES_NO_OPTION);
            
            if (viewReceipt == JOptionPane.YES_OPTION) {
                viewReceipt(currentOrder);
            }
            
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