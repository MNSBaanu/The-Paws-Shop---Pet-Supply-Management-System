import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateCashierAccount extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(176, 196, 222); // Light Steel Blue
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);  // Smaller font size for input fields
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18); // Font for buttons

    private static final String CREDENTIALS_FILE_PATH = "credentials.txt"; // Path to save the username, password, and role
    private static final String CASHIER_ACCOUNTS_FILE_PATH = "cashier_accounts.txt"; // Path to save all cashier details

    private final DashboardWindow dashboardWindow;  // Reference to DashboardWindow

    public CreateCashierAccount(DashboardWindow dashboardWindow) {
        this.dashboardWindow = dashboardWindow; // Initialize the reference
        setTitle("Create New Cashier Account");
        setSize(450, 550); // Increased size for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Create Cashier Account");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Form Panel with additional margins and spacing
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 20)); // Increased space between fields
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(30, 40, 20, 40));  // Added top margin for better layout

        // Username Field
        formPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        usernameField.setFont(FIELD_FONT);  // Apply smaller font size here
        formPanel.add(usernameField);

        // Password Field
        formPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(FIELD_FONT);  // Apply smaller font size here
        formPanel.add(passwordField);

        // Confirm Password Field
        formPanel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(FIELD_FONT);  // Apply smaller font size here
        formPanel.add(confirmPasswordField);

        // Email Field
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        emailField.setFont(FIELD_FONT);  // Apply smaller font size here
        formPanel.add(emailField);

        // Role Dropdown (Only Cashier option here)
        formPanel.add(new JLabel("Role:"));
        JComboBox<String> roleDropdown = new JComboBox<>(new String[]{"Cashier"});
        formPanel.add(roleDropdown);

        // Add the form panel to the main panel
        mainPanel.add(titlePanel);
        mainPanel.add(formPanel);

        // Button Panel (for Submit, Reset, and Back)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 25));  // Increased space between buttons

        // Submit Button
        JButton submitButton = createStyledButton("Submit", e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText();
            String role = (String) roleDropdown.getSelectedItem();

            // Validate email format
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(null, "Please enter a valid email address!");
                return;
            }

            // Check if password and confirm password match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!");
                return;
            }

            // Save the new cashier details to both files
            saveCashierDetails(username, password, email, role);
            saveCredentials(username, password, role);

            JOptionPane.showMessageDialog(this, "Cashier account created successfully!");
            dispose();  // Close the form after submission
            new DashboardWindow("Manager").setVisible(true);  // Open Dashboard after submission
        });

        // Reset Button
        JButton resetButton = createStyledButton("Reset", e -> {
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            emailField.setText("");
        });

        // Back Button
        JButton backButton = createStyledButton("Back", e -> {
            this.dispose();  // Close current window
            dashboardWindow.setVisible(true);  // Return to the Dashboard window
        });

        // Add buttons to the button panel
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);

        // Add button panel to the main panel
        mainPanel.add(buttonPanel);

        // Add the main panel to the frame
        add(mainPanel);
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Save all cashier details to the file
    private void saveCashierDetails(String username, String password, String email, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CASHIER_ACCOUNTS_FILE_PATH, true))) {
            writer.write(username + "," + password + "," + email + "," + role);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving cashier details.");
        }
    }

    // Save username, password, and role to the credentials file
    private void saveCredentials(String username, String password, String role) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE_PATH, true))) {
            writer.write(username + "," + password + "," + role);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving credentials.");
        }
    }

    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        button.addActionListener(listener);
        return button;
    }
}
