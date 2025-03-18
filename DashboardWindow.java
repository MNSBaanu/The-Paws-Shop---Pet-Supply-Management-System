import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class DashboardWindow extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(176, 196, 222); // Light Steel Blue
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);

    private final String role;

    public DashboardWindow(String role) {
        this.role = role;
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle(role + " Dashboard - The Paws Shop");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Welcome, " + role);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.setLayout(new GridLayout(role.equals("Manager") ? 5 : 4, 1, 0, 15));
        buttonsPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Create and add buttons
        addStyledButton(buttonsPanel, "View Pet Supplies", e -> openWindow(new ViewSearchWindow()));
        addStyledButton(buttonsPanel, "Add New Pet Supplies", e -> openWindow(new AddSuppliesWindow()));
        addStyledButton(buttonsPanel, "Process Transaction", e -> openWindow(new TransactionWindow(role)));

        if (role.equals("Manager")) {
            addStyledButton(buttonsPanel, "Create Cashier Account", e -> openWindow(new CreateCashierAccount(this)));
        }

        // Logout button
        JButton logoutButton = createStyledButton("Logout", e -> logout());
        logoutButton.setBackground(SECONDARY_COLOR);
        buttonsPanel.add(logoutButton);

        // Add all panels to main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonsPanel);

        // Center align all panels
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }

        // Add main panel to frame
        add(mainPanel);
    }

    private void addStyledButton(JPanel panel, String text, ActionListener listener) {
        JButton button = createStyledButton(text, listener);
        panel.add(button);
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
        button.setPreferredSize(new Dimension(300, 60));

        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                if (text.equals("Logout")) {
                    button.setBackground(SECONDARY_COLOR);
                }
            }
        });

        if (listener != null) {
            button.addActionListener(listener);
        }

        return button;
    }

    private void openWindow(JFrame window) {
        window.setVisible(true);
        dispose();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            new LoginWindow().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardWindow dashboard = new DashboardWindow("Manager");
            dashboard.setVisible(true);
        });
    }
}
