import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViewSearchWindow extends JFrame {

    private JTable suppliesTable;
    private JTextField searchBar;
    private JComboBox<String> categoryFilter;
    private int currentPage = 1;
    private static int TOTAL_RECORDS = 0; // Total records in the dataset
    private static final int ITEMS_PER_PAGE = 10; // 10 records per page

    // Data holders for products
    private List<Product> productList = new ArrayList<>();

    public ViewSearchWindow() {
        setTitle("View All Pet Supplies Details");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 230, 250)); // Light background color similar to a dashboard

        // Columns and data
        String[] columns = {"Product Name", "Category", "Price", "Stock Quantity", "Edit", "Delete"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0); // Empty table model
        suppliesTable = new JTable(tableModel);
        suppliesTable.setFillsViewportHeight(true);
        suppliesTable.setBackground(new Color(255, 255, 255)); // Table background white

        // Add sorting functionality
        suppliesTable.setAutoCreateRowSorter(true);

        // Set cell spacing and padding for a more spacious look
        suppliesTable.setRowHeight(40); // Increase row height for better readability
        suppliesTable.setIntercellSpacing(new Dimension(10, 10)); // Increase cell spacing

        // Set up search bar
        searchBar = new JTextField(20);
        searchBar.setPreferredSize(new Dimension(200, 30));
        searchBar.setBackground(new Color(240, 240, 240)); // Light grey background for the search bar
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateTableData();
            }
            public void removeUpdate(DocumentEvent e) {
                updateTableData();
            }
            public void changedUpdate(DocumentEvent e) {
                updateTableData();
            }
        });

        // Updated categories list for filter
        String[] categoriesList = {"All", "Pet Toys", "Harnesses", "Cages", "Grooming Products", "Collars"};
        categoryFilter = new JComboBox<>(categoriesList);
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        categoryFilter.setBackground(new Color(240, 240, 240)); // Same light grey background
        categoryFilter.addActionListener(e -> updateTableData());

        // Set up buttons in each row (Edit and Delete)
        suppliesTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        suppliesTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit"));

        suppliesTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));
        suppliesTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        // Set up pagination panel with styling
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(240, 240, 240)); // Background color similar to the search bar
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        prevButton.setBackground(new Color(70, 130, 180)); // Steel Blue background for buttons
        prevButton.setForeground(Color.WHITE);
        nextButton.setBackground(new Color(70, 130, 180)); // Steel Blue background for buttons
        nextButton.setForeground(Color.WHITE);
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    updateTableData();
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage * ITEMS_PER_PAGE < TOTAL_RECORDS) {
                    currentPage++;
                    updateTableData();
                }
            }
        });
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);

        // Back Button functionality
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(70, 130, 180)); // Steel Blue background for button
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                new DashboardWindow("Manager").setVisible(true); // Navigate to Dashboard with "Manager" role
            }
        });
        paginationPanel.add(backButton);

        // Set up the layout with styling
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(230, 230, 250)); // Light background
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchBar);
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryFilter);

        // Centering the table with added margins
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(new Color(230, 230, 250)); // Light background
        JScrollPane scrollPane = new JScrollPane(suppliesTable);
        scrollPane.setPreferredSize(new Dimension(850, 400)); // Set the preferred size of the table
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add margin around the table
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        loadProductData();  // Load data from file
        updateTableData(); // Initial load
    }

    private void loadProductData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("product_data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0].trim();
                    String category = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    int stockQuantity = Integer.parseInt(parts[3].trim());

                    Product product = new Product(name, category, price, stockQuantity);
                    productList.add(product);
                }
            }
            TOTAL_RECORDS = productList.size();  // Set total records after loading
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading product data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableData() {
        String searchText = searchBar.getText().toLowerCase();
        String selectedCategory = (String) categoryFilter.getSelectedItem();

        // Filter the data
        List<Object[]> filteredData = new ArrayList<>();
        for (Product product : productList) {
            boolean matchesSearch = product.getName().toLowerCase().contains(searchText);
            boolean matchesCategory = selectedCategory.equals("All") || product.getCategory().equals(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredData.add(new Object[]{product.getName(), product.getCategory(), product.getPrice(), product.getStockQuantity(), "Edit", "Delete"});
            }
        }

        // Paginate the data
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(currentPage * ITEMS_PER_PAGE, filteredData.size());
        Object[][] pageData = new Object[endIndex - startIndex][6];

        for (int i = startIndex; i < endIndex; i++) {
            pageData[i - startIndex] = filteredData.get(i);
        }

        // Update the table model with the paginated data
        DefaultTableModel model = (DefaultTableModel) suppliesTable.getModel();
        model.setRowCount(0); // Clear the previous rows
        for (Object[] row : pageData) {
            model.addRow(row);  // Add the paginated data
        }
    }

    // Renderer for the Edit and Delete buttons
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String buttonText) {
            setText(buttonText);
            setBackground(new Color(70, 130, 180)); // Steel Blue background
            setForeground(Color.WHITE);
            setFocusable(false);
            setPreferredSize(new Dimension(100, 30));
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((String) value);
            return this;
        }
    }

    // Button editor for the action columns (Edit, Delete)
    class ButtonEditor extends DefaultCellEditor {
        private String buttonText;

        public ButtonEditor(JCheckBox checkBox, String buttonText) {
            super(checkBox);
            this.buttonText = buttonText;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            JButton button = new JButton(buttonText);
            button.setBackground(new Color(70, 130, 180)); // Steel Blue background
            button.setForeground(Color.WHITE);
            return button;
        }
    }
}
