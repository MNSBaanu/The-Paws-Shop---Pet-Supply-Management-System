import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TransactionWindow extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(176, 196, 222); // Light Steel Blue
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue

    private JComboBox<String> categoryComboBox;
    private JComboBox<String> productComboBox;
    private JTextField priceField;
    private JLabel totalLabel;
    private DefaultListModel<String> cartModel;
    private double totalCost = 0.0;
    private JButton backButton;
    private Map<String, List<Product>> productCatalogData; // Explicitly use java.util.List
    private final String role; // Role of the user

    public TransactionWindow(String role) {
        this.role = role; // Initialize role
        setTitle("Process Transaction");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load products from the file
        loadProductData();

        // Main panel for the entire window
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(20, 20)); // Increased gap for margins
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Adds margin around the content
        panel.setBackground(BACKGROUND_COLOR); // Set background color

        // Cart display section
        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        JScrollPane cartScrollPane = new JScrollPane(cartList);
        cartScrollPane.setBorder(BorderFactory.createTitledBorder("Shopping Cart")); // Cart section with title
        cartScrollPane.setBackground(SECONDARY_COLOR); // Set background color for cart section
        panel.add(cartScrollPane, BorderLayout.CENTER);

        // Panel for input fields and buttons (Category, Product, Price)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10)); // Layout with margins between components
        inputPanel.setBackground(BACKGROUND_COLOR); // Set background color for input panel

        // Category selection ComboBox
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(PRIMARY_COLOR); // Set label color to primary color
        categoryComboBox = new JComboBox<>(productCatalogData.keySet().toArray(new String[0]));
        categoryComboBox.addActionListener(e -> updateProductComboBox());

        // Product selection ComboBox
        JLabel productLabel = new JLabel("Product:");
        productLabel.setForeground(PRIMARY_COLOR); // Set label color to primary color
        productComboBox = new JComboBox<>();
        productComboBox.setEnabled(false); // Initially disabled until category is selected
        productComboBox.addActionListener(e -> updatePriceField()); // Update price when product is selected

        // Price input field (read-only)
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(PRIMARY_COLOR); // Set label color to primary color
        priceField = new JTextField();
        priceField.setEditable(false); // Price is read-only based on product selection

        // Adding components to the input panel
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);
        inputPanel.add(productLabel);
        inputPanel.add(productComboBox);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);

        // Total cost label
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setForeground(PRIMARY_COLOR); // Set total label color to primary color
        inputPanel.add(totalLabel);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Buttons for adding/removing items and completing transaction
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10)); // Adjusted layout with some margin
        buttonPanel.setBackground(BACKGROUND_COLOR); // Set background color for button panel

        JButton addItemButton = new JButton("Add Item");
        addItemButton.setBackground(PRIMARY_COLOR); // Set button color to primary color
        addItemButton.setForeground(Color.WHITE); // Set button text color to white

        JButton removeItemButton = new JButton("Remove Item");
        removeItemButton.setBackground(PRIMARY_COLOR); // Set button color to primary color
        removeItemButton.setForeground(Color.WHITE); // Set button text color to white

        JButton completeTransactionButton = new JButton("Complete");
        completeTransactionButton.setBackground(PRIMARY_COLOR); // Set button color to primary color
        completeTransactionButton.setForeground(Color.WHITE); // Set button text color to white

        // Initialize the back button
        backButton = new JButton("Back");
        backButton.setBackground(PRIMARY_COLOR); // Set button color to primary color
        backButton.setForeground(Color.WHITE); // Set button text color to white
        backButton.setPreferredSize(new Dimension(100, 40)); // Set size of the button

        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(completeTransactionButton);
        buttonPanel.add(backButton); // Add the back button to the panel

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        addItemButton.addActionListener(e -> {
            String selectedProduct = (String) productComboBox.getSelectedItem();
            Product product = getProductByName(selectedProduct);

            if (product != null) {
                cartModel.addElement(product.getName() + " - $" + product.getPrice());
                totalCost += product.getPrice();
                totalLabel.setText("Total: $" + String.format("%.2f", totalCost));
            }
        });

        removeItemButton.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = cartModel.getElementAt(selectedIndex);
                String productName = selectedItem.split(" - ")[0]; // Extract product name
                Product product = getProductByName(productName);

                if (product != null) {
                    cartModel.removeElementAt(selectedIndex); // Remove item from cart
                    totalCost -= product.getPrice(); // Deduct price from total
                    totalLabel.setText("Total: $" + String.format("%.2f", totalCost)); // Update total label
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an item to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        completeTransactionButton.addActionListener(e -> {
            // Check if the cart is empty before proceeding
            if (cartModel.isEmpty()) {
                JOptionPane.showMessageDialog(TransactionWindow.this, "Cart is empty! Please add items to complete the transaction.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm the transaction completion
            int confirm = JOptionPane.showConfirmDialog(TransactionWindow.this,
                    "Are you sure you want to complete the transaction? Total: $" + String.format("%.2f", totalCost),
                    "Confirm Transaction", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Reduce the stock based on the cart items
                for (int i = 0; i < cartModel.size(); i++) {
                    String cartItem = cartModel.getElementAt(i);
                    Product product = getProductByNameFromCart(cartItem);

                    if (product != null) {
                        product.reduceStock(1); // Reduce stock by 1 for each purchased item
                    }
                }

                // Save the updated product data to the file
                saveProductData();

                // Save the transaction history to the transaction_report.txt file
                saveTransactionReport();

                // Clear the cart and reset total cost
                JOptionPane.showMessageDialog(TransactionWindow.this, "Transaction Completed! Total: $" + totalCost);
                cartModel.clear();
                totalCost = 0.0;
                totalLabel.setText("Total: $0.00");
            }
        });

        backButton.addActionListener(e -> {
            dispose(); // Close the current TransactionWindow
            new DashboardWindow(role).setVisible(true); // Open the dashboard with the correct role
        });

        add(panel);
        setVisible(true);
    }

    private void loadProductData() {
        productCatalogData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("product_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String category = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int stock = Integer.parseInt(parts[3]);

                    Product product = new Product(name, category, price, stock);

                    productCatalogData
                            .computeIfAbsent(category, k -> new ArrayList<>())
                            .add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProductComboBox() {
        productComboBox.removeAllItems();
        String category = (String) categoryComboBox.getSelectedItem();
        List<Product> products = productCatalogData.get(category);

        if (products != null) {
            for (Product product : products) {
                productComboBox.addItem(product.getName());
            }
            productComboBox.setEnabled(true);
        }
    }

    private void updatePriceField() {
        String selectedProduct = (String) productComboBox.getSelectedItem();
        Product product = getProductByName(selectedProduct);

        if (product != null) {
            priceField.setText("$" + String.format("%.2f", product.getPrice()));
        }
    }

    private Product getProductByName(String name) {
        for (List<Product> productList : productCatalogData.values()) {
            for (Product product : productList) {
                if (product.getName().equals(name)) {
                    return product;
                }
            }
        }
        return null;
    }

    private Product getProductByNameFromCart(String cartItem) {
        String productName = cartItem.split(" - ")[0];
        return getProductByName(productName);
    }

    private void saveProductData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("product_data.txt"))) {
            for (List<Product> products : productCatalogData.values()) {
                for (Product product : products) {
                    writer.write(product.getName() + "," + product.getCategory() + "," + product.getPrice() + "," + product.getStock() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTransactionReport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transaction_report.txt", true))) { // 'true' to append
            writer.write("Transaction Date: " + new Date() + "\n");
            writer.write("Items Purchased:\n");

            for (int i = 0; i < cartModel.size(); i++) {
                String cartItem = cartModel.getElementAt(i);
                writer.write(cartItem + "\n");
            }

            writer.write("Total Cost: $" + String.format("%.2f", totalCost) + "\n");
            writer.write("-------------------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save transaction report!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Product class
    static class Product {
        private final String name;
        private final String category;
        private final double price;
        private int stock;

        public Product(String name, String category, double price, int stock) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void reduceStock(int quantity) {
            stock -= quantity;
        }
    }
}
