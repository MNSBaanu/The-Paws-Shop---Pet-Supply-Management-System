import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(176, 196, 222); // Light Steel Blue
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("The Paws Shop - Login");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("The Paws Shop", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        // Input Fields
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();

        // Buttons
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR, Color.WHITE, e -> authenticateUser());
        JButton exitButton = createStyledButton("Exit", SECONDARY_COLOR, Color.BLACK, e -> System.exit(0));

        // Add components to the panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createInputPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createButtonPanel(loginButton, exitButton));

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        inputPanel.add(createLabel("Username"));
        inputPanel.add(usernameField);
        inputPanel.add(createLabel("Password"));
        inputPanel.add(passwordField);

        return inputPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private JPanel createButtonPanel(JButton loginButton, JButton exitButton) {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        return buttonPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(LABEL_FONT);
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(action);
        return button;
    }

    private void authenticateUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = validateCredentials(username, password);
        if (role != null) {
            showMessage("Login Successful as " + role, "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardWindow(role).setVisible(true);
            dispose();
        } else {
            showMessage("Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 3 && credentials[0].equals(username) && credentials[1].equals(password)) {
                    return credentials[2]; // Return the role (Manager or Cashier)
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if no valid credentials are found
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
