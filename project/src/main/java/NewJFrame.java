/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
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

/**
 *
 * @author Admin
 */
public class NewJFrame extends javax.swing.JFrame {
    private Restaurant restaurant;
    private CardLayout cardLayout;
    
    // Menu panel components
    private JPanel menuItemsPanel;
    private JScrollPane menuScrollPane;
    
    // Order table components
    private DefaultTableModel orderTableModel;
    private JLabel lblTotal;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    
    // Summary components
    private JTextArea txtSummary;
    
    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        restaurant = new Restaurant();
        
        // Initialize dynamic components
        initializeDynamicComponents();
        setLocationRelativeTo(null);
    }
    
    private void initializeDynamicComponents() {
        // Set up card layout
        cardLayout = (CardLayout) cardLayoutPanel.getLayout();
        
        // Set up order table
        setupOrderTable();
        
        // Set up menu scroll pane
        setupMenuScrollPane();
        
        // Add action listeners to buttons
        setupActionListeners();
    }
    
    private void setupOrderTable() {
        String[] columns = {"Item", "Qty", "Price"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable.setModel(orderTableModel);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        orderTable.setRowHeight(25);
        
        // Initialize total labels
        lblSubtotal = new JLabel("P0.00");
        lblTax = new JLabel("P0.00");
        lblTotal = new JLabel("P0.00");
        
        // Replace the placeholder labels with dynamic ones
        subtotalValueLabel.setText("P0.00");
        taxValueLabel.setText("P0.00");
        totalValueLabel.setText("P0.00");
    }
    
    private void setupMenuScrollPane() {
        menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setBackground(new Color(255, 250, 240));
        
        menuScrollPane = new JScrollPane(menuItemsPanel);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Replace the placeholder panel with our dynamic menu
        menuContainerPanel.removeAll();
        menuContainerPanel.add(menuScrollPane, BorderLayout.CENTER);
        menuContainerPanel.revalidate();
        menuContainerPanel.repaint();
    }
    
    private void setupActionListeners() {
        // Welcome panel buttons
        viewMenuButton.addActionListener(e -> {
            refreshMenuDisplay();
            cardLayout.show(cardLayoutPanel, "menuPanel");
        });
        
        exitButton.addActionListener(e -> System.exit(0));
        
        // Menu panel buttons
        backToWelcomeButton.addActionListener(e -> cardLayout.show(cardLayoutPanel, "welcomePanel"));
        cancelOrderButton.addActionListener(e -> cancelOrder());
        proceedToCheckoutButton.addActionListener(e -> {
            if (restaurant.getCurrentOrder() == null || restaurant.getCurrentOrder().getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add items to your order first!");
            } else {
                showSummary();
                cardLayout.show(cardLayoutPanel, "summaryPanel");
            }
        });
        
        // Summary panel buttons
        backToMenuButton.addActionListener(e -> cardLayout.show(cardLayoutPanel, "menuPanel"));
        backToMenuButton2.addActionListener(e -> cardLayout.show(cardLayoutPanel, "menuPanel"));
        checkoutButton.addActionListener(e -> finalizeOrder());
    }
    
    // ALL YOUR BUSINESS LOGIC METHODS GO HERE (same as before)
    // refreshMenuDisplay(), addCategorySection(), createMenuItemCard(), etc.
    // ... (copy all those methods from your original code)
    
    // Add this method to handle menu item addition
    private void addMenuItemToOrder(MenuItem item, int quantity) {
        restaurant.addToCurrentOrder(item, quantity);
        refreshOrderDisplay();
        JOptionPane.showMessageDialog(this, 
            quantity + " x " + item.getName() + " added to order!\n" +
            "Price: P" + String.format("%.2f", item.getPrice()),
            "Item Added",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshOrderDisplay() {
        orderTableModel.setRowCount(0);
        
        if (restaurant.getCurrentOrder() == null) {
            subtotalValueLabel.setText("P0.00");
            taxValueLabel.setText("P0.00");
            totalValueLabel.setText("P0.00");
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
        
        subtotalValueLabel.setText(String.format("P%.2f", currentOrder.getSubtotal()));
        taxValueLabel.setText(String.format("P%.2f", currentOrder.getTax()));
        totalValueLabel.setText(String.format("P%.2f", currentOrder.getTotal()));
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
        
        orderSummaryTextArea.setText(summary.toString());
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
        
        // Create category header
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(new Color(255, 250, 240));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel categoryLabel = new JLabel(categoryName);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
        categoryLabel.setForeground(new Color(139, 69, 19));
        categoryPanel.add(categoryLabel, BorderLayout.WEST);
        
        menuItemsPanel.add(categoryPanel);
        
        // Create items grid
        JPanel itemsGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        itemsGrid.setBackground(new Color(255, 250, 240));
        itemsGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        for (MenuItem item : items) {
            // Create a simplified card for each item
            JPanel card = createSimpleMenuItemCard(item);
            itemsGrid.add(card);
        }
        
        menuItemsPanel.add(itemsGrid);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(210, 180, 140));
        menuItemsPanel.add(separator);
    }
    
    private JPanel createSimpleMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 100));
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel priceLabel = new JLabel("P" + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(new Color(0, 100, 0));
        
        JButton addButton = new JButton("Add");
        addButton.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(priceLabel, BorderLayout.CENTER);
        infoPanel.add(addButton, BorderLayout.SOUTH);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Add action listener to the button
        addButton.addActionListener(e -> {
            // For simplicity, add 1 quantity
            addMenuItemToOrder(item, 1);
        });
        
        return card;
    }
    
    private void saveReceiptToFile(Order order) {
        // Your existing saveReceiptToFile method
        try {
            File receiptsDir = new File("receipts");
            if (!receiptsDir.exists()) {
                receiptsDir.mkdir();
            }
            
            String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "receipts/" + order.getOrderId() + "_" + timestamp + ".txt";
            
            File receiptFile = new File(fileName);
            
            try (PrintWriter writer = new PrintWriter(receiptFile)) {
                writer.println("========================================");
                writer.println("         RESTAURANT RECEIPT");
                writer.println("========================================");
                writer.println();
                writer.println("Order ID: " + order.getOrderId());
                writer.println("Date: " + java.time.LocalDateTime.now());
                writer.println();
                writer.println("Items Ordered:");
                writer.println("----------------------------------------");
                
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
        // Your existing viewReceipt method
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
            saveReceiptToFile(currentOrder);
            restaurant.finalizeCurrentOrder();
            
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
            
            restaurant.createNewOrder();
            refreshOrderDisplay();
            cardLayout.show(cardLayoutPanel, "welcomePanel");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        cardLayoutPanel = new javax.swing.JPanel();
        welcomePanel = new javax.swing.JPanel();
        welcomeTitleLabel = new javax.swing.JLabel();
        viewMenuButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        welcomeFooterLabel = new javax.swing.JLabel();
        menuPanel = new javax.swing.JPanel();
        menuHeaderPanel = new javax.swing.JPanel();
        menuTitleLabel = new javax.swing.JLabel();
        backToWelcomeButton = new javax.swing.JButton();
        menuContainerPanel = new javax.swing.JPanel();
        orderSummaryPanel = new javax.swing.JPanel();
        currentOrderLabel = new javax.swing.JLabel();
        orderTableScrollPane = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        totalsPanel = new javax.swing.JPanel();
        subtotalLabel = new javax.swing.JLabel();
        subtotalValueLabel = new javax.swing.JLabel();
        taxLabel = new javax.swing.JLabel();
        taxValueLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        totalValueLabel = new javax.swing.JLabel();
        orderButtonsPanel = new javax.swing.JPanel();
        cancelOrderButton = new javax.swing.JButton();
        proceedToCheckoutButton = new javax.swing.JButton();
        summaryPanel = new javax.swing.JPanel();
        summaryHeaderPanel = new javax.swing.JPanel();
        summaryTitleLabel = new javax.swing.JLabel();
        backToMenuButton = new javax.swing.JButton();
        orderSummaryScrollPane = new javax.swing.JScrollPane();
        orderSummaryTextArea = new javax.swing.JTextArea();
        summaryButtonsPanel = new javax.swing.JPanel();
        backToMenuButton2 = new javax.swing.JButton();
        checkoutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Restaurant Order System");

        cardLayoutPanel.setLayout(new java.awt.CardLayout());

        welcomePanel.setBackground(new java.awt.Color(255, 248, 240));
        welcomePanel.setLayout(new java.awt.BorderLayout());

        welcomeTitleLabel.setFont(new java.awt.Font("Arial", 1, 32)); // NOI18N
        welcomeTitleLabel.setForeground(new java.awt.Color(139, 69, 19));
        welcomeTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeTitleLabel.setText("Restaurant Order System");
        welcomePanel.add(welcomeTitleLabel, java.awt.BorderLayout.PAGE_START);

        viewMenuButton.setBackground(new java.awt.Color(210, 180, 140));
        viewMenuButton.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        viewMenuButton.setText("VIEW MENU");
        viewMenuButton.setFocusPainted(false);
        viewMenuButton.setPreferredSize(new java.awt.Dimension(250, 70));

        exitButton.setBackground(new java.awt.Color(205, 133, 63));
        exitButton.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        exitButton.setText("EXIT");
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new java.awt.Dimension(250, 70));

        welcomeFooterLabel.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        welcomeFooterLabel.setForeground(new java.awt.Color(139, 69, 19));
        welcomeFooterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeFooterLabel.setText("Simple Restaurant Order Management System with Customization Options");
        welcomePanel.add(welcomeFooterLabel, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(welcomePanel);
        welcomePanel.setLayout(welcomePanelLayout);
        welcomePanelLayout.setHorizontalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(viewMenuButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(150, 150, 150))
        );
        welcomePanelLayout.setVerticalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(viewMenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(200, Short.MAX_VALUE))
        );

        cardLayoutPanel.add(welcomePanel, "welcomePanel");

        menuPanel.setBackground(new java.awt.Color(255, 250, 240));
        menuPanel.setLayout(new java.awt.BorderLayout());

        menuHeaderPanel.setBackground(new java.awt.Color(139, 69, 19));
        menuHeaderPanel.setLayout(new java.awt.BorderLayout());

        menuTitleLabel.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        menuTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        menuTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        menuTitleLabel.setText("MENU");
        menuHeaderPanel.add(menuTitleLabel, java.awt.BorderLayout.CENTER);

        backToWelcomeButton.setBackground(new java.awt.Color(210, 180, 140));
        backToWelcomeButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        backToWelcomeButton.setText("← Back");
        menuHeaderPanel.add(backToWelcomeButton, java.awt.BorderLayout.LINE_START);

        menuPanel.add(menuHeaderPanel, java.awt.BorderLayout.PAGE_START);

        menuContainerPanel.setLayout(new java.awt.BorderLayout());

        orderSummaryPanel.setBackground(new java.awt.Color(255, 250, 245));
        orderSummaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(139, 69, 19), 2), "CURRENT ORDER", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12), new java.awt.Color(139, 69, 19))); // NOI18N
        orderSummaryPanel.setPreferredSize(new java.awt.Dimension(350, 100));
        orderSummaryPanel.setLayout(new java.awt.BorderLayout());

        currentOrderLabel.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        currentOrderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentOrderLabel.setText("Order Items");
        orderSummaryPanel.add(currentOrderLabel, java.awt.BorderLayout.PAGE_START);

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Qty", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderTableScrollPane.setViewportView(orderTable);

        orderSummaryPanel.add(orderTableScrollPane, java.awt.BorderLayout.CENTER);

        totalsPanel.setBackground(new java.awt.Color(255, 250, 245));
        totalsPanel.setLayout(new java.awt.GridLayout(3, 2, 5, 5));

        subtotalLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        subtotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        subtotalLabel.setText("Subtotal:");
        totalsPanel.add(subtotalLabel);

        subtotalValueLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        subtotalValueLabel.setText("P0.00");
        totalsPanel.add(subtotalValueLabel);

        taxLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        taxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        taxLabel.setText("Tax (12%):");
        totalsPanel.add(taxLabel);

        taxValueLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        taxValueLabel.setText("P0.00");
        totalsPanel.add(taxValueLabel);

        totalLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        totalLabel.setText("TOTAL:");
        totalsPanel.add(totalLabel);

        totalValueLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        totalValueLabel.setForeground(new java.awt.Color(0, 100, 0));
        totalValueLabel.setText("P0.00");
        totalsPanel.add(totalValueLabel);

        orderSummaryPanel.add(totalsPanel, java.awt.BorderLayout.PAGE_END);

        orderButtonsPanel.setBackground(new java.awt.Color(255, 250, 245));
        orderButtonsPanel.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        cancelOrderButton.setBackground(new java.awt.Color(220, 80, 80));
        cancelOrderButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        cancelOrderButton.setText("CANCEL ORDER");
        cancelOrderButton.setFocusPainted(false);
        orderButtonsPanel.add(cancelOrderButton);

        proceedToCheckoutButton.setBackground(new java.awt.Color(144, 238, 144));
        proceedToCheckoutButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        proceedToCheckoutButton.setText("PROCEED TO CHECKOUT");
        proceedToCheckoutButton.setFocusPainted(false);
        orderButtonsPanel.add(proceedToCheckoutButton);

        orderSummaryPanel.add(orderButtonsPanel, java.awt.BorderLayout.PAGE_END);

        menuContainerPanel.add(orderSummaryPanel, java.awt.BorderLayout.LINE_END);

        menuPanel.add(menuContainerPanel, java.awt.BorderLayout.CENTER);

        cardLayoutPanel.add(menuPanel, "menuPanel");

        summaryPanel.setBackground(new java.awt.Color(255, 250, 240));
        summaryPanel.setLayout(new java.awt.BorderLayout());

        summaryHeaderPanel.setBackground(new java.awt.Color(139, 69, 19));
        summaryHeaderPanel.setLayout(new java.awt.BorderLayout());

        summaryTitleLabel.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        summaryTitleLabel.setForeground(new java.awt.Color(255, 255, 255));
        summaryTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        summaryTitleLabel.setText("ORDER SUMMARY");
        summaryHeaderPanel.add(summaryTitleLabel, java.awt.BorderLayout.CENTER);

        backToMenuButton.setBackground(new java.awt.Color(210, 180, 140));
        backToMenuButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        backToMenuButton.setText("← Back to Menu");
        summaryHeaderPanel.add(backToMenuButton, java.awt.BorderLayout.LINE_START);

        summaryPanel.add(summaryHeaderPanel, java.awt.BorderLayout.PAGE_START);

        orderSummaryTextArea.setEditable(false);
        orderSummaryTextArea.setColumns(20);
        orderSummaryTextArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        orderSummaryTextArea.setRows(5);
        orderSummaryScrollPane.setViewportView(orderSummaryTextArea);

        summaryPanel.add(orderSummaryScrollPane, java.awt.BorderLayout.CENTER);

        summaryButtonsPanel.setBackground(new java.awt.Color(255, 250, 240));
        summaryButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 20));

        backToMenuButton2.setBackground(new java.awt.Color(210, 180, 140));
        backToMenuButton2.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        backToMenuButton2.setText("BACK TO MENU");
        backToMenuButton2.setFocusPainted(false);
        backToMenuButton2.setPreferredSize(new java.awt.Dimension(180, 45));
        summaryButtonsPanel.add(backToMenuButton2);

        checkoutButton.setBackground(new java.awt.Color(144, 238, 144));
        checkoutButton.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        checkoutButton.setText("CHECKOUT");
        checkoutButton.setFocusPainted(false);
        checkoutButton.setPreferredSize(new java.awt.Dimension(180, 45));
        summaryButtonsPanel.add(checkoutButton);

        summaryPanel.add(summaryButtonsPanel, java.awt.BorderLayout.PAGE_END);

        cardLayoutPanel.add(summaryPanel, "summaryPanel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cardLayoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cardLayoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton backToMenuButton;
    private javax.swing.JButton backToMenuButton2;
    private javax.swing.JButton backToWelcomeButton;
    private javax.swing.JPanel cardLayoutPanel;
    private javax.swing.JButton cancelOrderButton;
    private javax.swing.JButton checkoutButton;
    private javax.swing.JLabel currentOrderLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel menuContainerPanel;
    private javax.swing.JPanel menuHeaderPanel;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JLabel menuTitleLabel;
    private javax.swing.JPanel orderButtonsPanel;
    private javax.swing.JScrollPane orderSummaryScrollPane;
    private javax.swing.JTextArea orderSummaryTextArea;
    private javax.swing.JPanel orderSummaryPanel;
    private javax.swing.JTable orderTable;
    private javax.swing.JScrollPane orderTableScrollPane;
    private javax.swing.JButton proceedToCheckoutButton;
    private javax.swing.JPanel summaryButtonsPanel;
    private javax.swing.JPanel summaryHeaderPanel;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel summaryTitleLabel;
    private javax.swing.JLabel subtotalLabel;
    private javax.swing.JLabel subtotalValueLabel;
    private javax.swing.JLabel taxLabel;
    private javax.swing.JLabel taxValueLabel;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JLabel totalValueLabel;
    private javax.swing.JPanel totalsPanel;
    private javax.swing.JButton viewMenuButton;
    private javax.swing.JLabel welcomeFooterLabel;
    private javax.swing.JPanel welcomePanel;
    private javax.swing.JLabel welcomeTitleLabel;
    // End of variables declaration                   
}