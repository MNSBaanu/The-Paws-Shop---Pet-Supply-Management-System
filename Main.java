public class Main {

    public static void main(String[] args) {
        // Launch the login window (GUI interface)
        new Thread(() -> {
            new LoginWindow().setVisible(true);
        }).start();

        // Run console-based functionality
        runConsoleApp();
    }

    private static void runConsoleApp() {
        // Create Manager and Cashier objects with updated credentials
        User.Manager manager = new User.Manager("manager", "manager123");
        User.Cashier cashier = new User.Cashier("cashier", "cashier123");

        // Logging in as Manager with updated credentials
        if (User.Manager.login("manager", "manager123")) {
            System.out.println(" ");
        }

        // Cashier logs in and manages pet supplies with updated credentials
        if (User.Cashier.login("cashier", "cashier123")) {
            System.out.println(" ");

        }
    }

}
