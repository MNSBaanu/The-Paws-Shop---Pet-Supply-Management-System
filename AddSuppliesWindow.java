import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddSuppliesWindow extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18); // Larger font for buttons

    private static final String FILE_PATH = "product_data.txt"; // Change to the correct file path

    public AddSuppliesWindow() {
        setTitle("Add New Pet Supplies");
        setSize(400, 350);  // Increase height for the back button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel with margins
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.setLayout(new GridLayout(7, 2, 10, 10)); // Increased grid size for back button
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 20px margin around the content

        // Labels and fields
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(PRIMARY_COLOR);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        panel.add(nameField);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(LABEL_FONT);
        categoryLabel.setForeground(PRIMARY_COLOR);
        panel.add(categoryLabel);

        JComboBox<String> categoryDropdown = new JComboBox<>(new String[]{"Food", "Pet Toys", "Harnesses", "Cages", "Grooming Products"});
        panel.add(categoryDropdown);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(LABEL_FONT);
        priceLabel.setForeground(PRIMARY_COLOR);
        panel.add(priceLabel);

        JTextField priceField = new JTextField();
        panel.add(priceField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(LABEL_FONT);
        quantityLabel.setForeground(PRIMARY_COLOR);
        panel.add(quantityLabel);

        JTextField quantityField = new JTextField();
        panel.add(quantityField);

// Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(PRIMARY_COLOR); // Set to Submit button color
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setOpaque(true);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(submitButton);

// Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(BUTTON_FONT);
        resetButton.setBackground(PRIMARY_COLOR); // Set to Submit button color
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setOpaque(true);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(resetButton);

// Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(BUTTON_FONT);
        backButton.setBackground(PRIMARY_COLOR); // Set to Submit button color
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(backButton);


        // Action listeners for buttons
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String category = (String) categoryDropdown.getSelectedItem();
                String price = priceField.getText();
                String quantity = quantityField.getText();

                // Save the supply details to the product_data.txt file
                saveSupplyToFile(name, category, price, quantity);

                // Show confirmation message
                JOptionPane.showMessageDialog(null, "Pet Supply Added/Updated Successfully!");
                dispose();
            }
        });

        resetButton.addActionListener(e -> {
            nameField.setText("");
            priceField.setText("");
            quantityField.setText("");
        });

        // Back Button Action Listener
        backButton.addActionListener(e -> {
            // Open the Dashboard window and dispose the current window
            new DashboardWindow("Manager").setVisible(true);
            dispose();
        });

        // Add the panel to the center of the frame
        add(panel, BorderLayout.CENTER);
    }

    // Method to save or update the supply details in the file
    private void saveSupplyToFile(String name, String category, String price, String quantity) {
        try {
            // Read the existing file into a list to check for duplicates
            File file = new File(FILE_PATH);
            List<String> lines = new ArrayList<>();
            boolean productExists = false;
            String updatedRecord = name + "," + category + "," + price + "," + quantity;

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Split the line into parts
                    String[] parts = line.split(",");

                    // Ensure that the line has exactly 4 elements
                    if (parts.length == 4) {
                        String productName = parts[0];
                        String productCategory = parts[1];
                        String productPrice = parts[2];
                        String productQuantity = parts[3];

                        // Check if the product already exists
                        if (productName.equals(name) && productCategory.equals(category)) {
                            // Update the quantity if the product already exists
                            int existingQuantity = Integer.parseInt(productQuantity);
                            int newQuantity = existingQuantity + Integer.parseInt(quantity);
                            updatedRecord = name + "," + category + "," + price + "," + newQuantity;
                            productExists = true;
                        } else {
                            lines.add(line); // Add the old line if it's a valid product
                        }
                    } else {
                        // Log or handle any invalid line here if necessary (optional)
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
                reader.close();
            }

            // Add the new or updated record
            lines.add(updatedRecord);

            // Write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();

            // Show confirmation message
            String message = productExists ? "Product Quantity Updated Successfully!" : "Pet Supply Added Successfully!";
            JOptionPane.showMessageDialog(this, message);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving to file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AddSuppliesWindow().setVisible(true);
        });
    }
}
