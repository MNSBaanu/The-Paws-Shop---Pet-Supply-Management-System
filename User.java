// Abstract class User (Abstraction)
abstract class User {
    protected String username;
    protected String password;
    protected String role;

    // Constructor to initialize the user credentials and role
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static class Manager extends User {
        public Manager(String username, String password) {
            super(username, password, "User.Manager");
        }

        public static boolean login(String username, String password) {
            return "manager".equals(username) && "manager123".equals(password);
        }
    }

    public static class Cashier extends User {
        public Cashier(String username, String password ) {
            super(username, password, "User.Cashier");
        }

        public static boolean login(String username, String password) {
            return "cashier".equals(username) && "cashier123".equals(password);}
    }
}