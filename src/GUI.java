    import javax.swing.*;
    import java.awt.*;
    import java.io.IOException;
    import java.sql.SQLException;
    import java.util.List;
    import java.util.Map;
    import java.util.function.Consumer;
    import java.util.function.Function;
    import java.util.stream.Collectors;

    public class GUI {
        private Customer currentCustomer;
        private List<Products> products;
        private final Orders orders;
        private final Reports reports;
        private final JFrame frame;
        private final CardLayout cardLayout;
        private final JPanel cardPanel;

        public GUI() {
            this.frame = new JFrame("Skoshopp2024");
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);

            this.orders = new Orders();
            this.reports = new Reports();

            loginPage();

            frame.add(cardPanel);
            frame.setVisible(true);

        }

        private void loginPage() {
            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(null);
            JLabel userName = new JLabel("Email: ");
            userName.setBounds(50, 120, 80, 25);
            JTextField userNameField = new JTextField(20);
            userNameField.setBounds(120, 122, 170, 25);
            JLabel passWord = new JLabel("Lösenord: ");
            passWord.setBounds(50, 170, 80, 25);
            JTextField passwordField = new JTextField(20);
            passwordField.setBounds(120, 172, 170, 25);
            JButton loginButton = new JButton("Logga in");
            loginButton.setBounds(100, 250, 200, 30);

            loginButton.addActionListener(e -> {
                String email = userNameField.getText();
                String password = passwordField.getText();
                try {
                    Customer customer = Customer.queryLogin(email, password);
                    if (customer != null) {
                        currentCustomer = customer;
                        selectProgramPage();
                        cardLayout.show(cardPanel, "SelectPage");
                    } else {
                        JOptionPane.showMessageDialog(null, "Fel email eller lösenord.");
                    }
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            });
            loginPanel.add(userName);
            loginPanel.add(userNameField);
            loginPanel.add(passWord);
            loginPanel.add(passwordField);
            loginPanel.add(loginButton);

            cardPanel.add(loginPanel, "LoginPage");
        }

        private void selectProgramPage() throws SQLException, IOException {
            JPanel selectPanel = new JPanel();
            selectPanel.setLayout(null);
            JButton storeButton = new JButton("Butik");
            storeButton.setBounds(40, 150, 130, 60);
            JButton salesButton = new JButton("Rapporter");
            salesButton.setBounds(220, 150, 130, 60);

            selectPanel.add(storeButton, BorderLayout.CENTER);
            selectPanel.add(salesButton, BorderLayout.CENTER);

            storeButton.addActionListener(e -> {
                try {
                    products = Products.queryProducts();
                } catch (IOException |SQLException ex) {
                    throw new RuntimeException(ex);
                }
                storePage();
                frame.setSize(1100,650);
                frame.setLocationRelativeTo(null);
                cardLayout.show(cardPanel, "StorePanel");
            });

            salesButton.addActionListener(e -> {
                salesReportPage();
                frame.setSize(1100,650);
                frame.setLocationRelativeTo(null);
                cardLayout.show(cardPanel, "SalesReportPanel");
            });

            cardPanel.add(selectPanel, "SelectPage");

        }

        private void storePage() {
            JPanel storePanel = new JPanel(new BorderLayout());
            ImageIcon backgroundImage = new ImageIcon(getClass().getResource("backgroundShop.png"));
            JLabel backgroundLabel = new JLabel(backgroundImage);

            JPanel subPanel = new JPanel(null);
            subPanel.setOpaque(false);
            subPanel.setPreferredSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));

            JLabel customerLabel = new JLabel("Inloggad som: " + currentCustomer.getName() + " " + currentCustomer.getLastName());
            customerLabel.setBounds(630, 2, 200, 40);

            JLabel numberLabel = new JLabel("0");
            numberLabel.setBounds(885, 36, 50, 50);
            numberLabel.setFont(new Font(numberLabel.getFont().getName(), Font.BOLD, 40));

            JComboBox<String> productComboBox = new JComboBox<>();
            productComboBox.setBounds(12, 40, 350, 30);
            productComboBox.setEditable(false);

            DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
            products.stream()
                    .map(product -> String.format("%s | %s |%s |Storlek: %s |%.0fkr |Stock: %d",
                            product.getBrand(),
                            product.getCategory(),
                            product.getColour(),
                            product.getSize(),
                            product.getPrice(),
                            product.getStockAmount()))
                    .forEach(comboBoxModel::addElement);
            productComboBox.setModel(comboBoxModel);


            JButton cartButton = new JButton("Köp");
            cartButton.setBounds(365, 25, 80, 60);
            cartButton.setBackground(Color.white);
            cartButton.addActionListener(e -> {
                        int selectedIndex = productComboBox.getSelectedIndex();
                        if (selectedIndex >= 0) {
                            Products selectedProduct = products.get(selectedIndex);
                            try {
                                orders.addToCart(currentCustomer.getCustomerId(), selectedProduct.getProduktID());
                                selectedProduct.setStockAmount(selectedProduct.getStockAmount() - 1);
                                productComboBox.removeAllItems();

                                List<String> productStrings = products.stream()
                                        .map(product -> String.format("%s | %s |%s |Storlek: %s |%.0fkr |Stock: %d",
                                                product.getBrand(),
                                                product.getCategory(),
                                                product.getColour(),
                                                product.getSize(),
                                                product.getPrice(),
                                                product.getStockAmount()))
                                        .collect(Collectors.toList());
                                productStrings.forEach(productComboBox::addItem);

                                int currentCount = Integer.parseInt(numberLabel.getText());
                                numberLabel.setText(String.valueOf(++currentCount));
                                JOptionPane.showMessageDialog(frame, "Produkten har lagts till: " + selectedProduct.getBrand());
                            } catch (IOException | SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "Produkten är slut i lager: " + ex.getMessage());
                            }
                        }
                    });

            subPanel.add(productComboBox);
            subPanel.add(cartButton);

            subPanel.add(customerLabel);
            subPanel.add(numberLabel);

            backgroundLabel.setLayout(new BorderLayout());
            backgroundLabel.add(subPanel);

            storePanel.add(backgroundLabel, BorderLayout.CENTER);

            cardPanel.add(storePanel, "StorePanel");
        }

        private void salesReportPage() {
            JPanel reportPanel = new JPanel();
            reportPanel.setLayout(null);

            JButton report1 = new JButton("1");
            report1.setBounds(60,80,50,50);

            JButton report2 = new JButton("2");
            report2.setBounds(60,220,50,50);

            JTextArea txtArea = new JTextArea();
            txtArea.setBounds(200,20,700,500);

            JButton report3 = new JButton("3");
            report3.setBounds(60,360,50,50);


            report1.addActionListener(e -> {
                try {

                    reports.fetchSalesData();

                txtArea.setText(""); // för att återställa innehållet till tomt och göra plats för ny rapport.
                String keyword = JOptionPane.showInputDialog("Ange märke, storlek eller färg:");

                Reports.ReportSearcherInterface searcher = (report, searchWord) -> report.toLowerCase().contains(searchWord.toLowerCase());
                //hur sökningen ska göras. om rapportsträngen innehåller vårt sökord

                Consumer<String> match = s -> txtArea.append(s + "\n"); //att vi ska appenda till txtarean med det matchande innehållet
                // högre ordningens funktion här.
                reports.searchForReport(keyword, searcher, match); // i det här fallet är searcher och match de funktioner som jag passat in.

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            report2.addActionListener(e -> {
                try {
                    txtArea.setText("");
                    reports.fetchSalesData();
                    // skapar en map där nyckeln är kundens namn och värdet är antalet ordrar de har lagt. vi processar listan helt enkelt
                    Map<String, Long> orderCountByCustomer = reports.getReports().stream()
                            .map(entry -> entry.split(",")[0].split(": ")[1].trim())// delar upp strängen vid , och sedan vid : för att få ut kundens namn

                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    // groupingBy med function grupperar resultatet baserat på kundens namn som bli nyckel i mappen
                    // counting() räknar ihop hur många gånger varje nyckel förekkommer.



                    orderCountByCustomer.forEach((customer, count) -> {
                        txtArea.append(String.format("Kund: %s, Antalet beställningar: %s\n", customer, count));
                    });
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });

            report3.addActionListener(e -> {
                try {
                    reports.fetchSalesData();
                    //splittar vid,
                    Map<String, Double> totalOrderValuePerCustomer = reports.getReports().stream()
                            .map(entry -> entry.split(", "))

                             //kund adress ort som nyckel
                            .collect(Collectors.groupingBy(parts -> parts[0] + ", " + parts[1] + ", " + parts[2],
                                    Collectors.summingDouble(parts -> {

                                        //summerar priset för varje rad baserat på nyckeln rad gör till en doublesträngg
                                        String price = parts[parts.length - 1];
                                        String priceString = price.split(": ")[1].replace(",", ".");
                                        return Double.parseDouble(priceString);
                                    })));

                    txtArea.setText("");

                    totalOrderValuePerCustomer.forEach((customer, totalValue) -> {
                        txtArea.append(String.format("%s, Totalt Pris: %.2f\n", customer, totalValue));
                    });
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            });


            reportPanel.add(report1);
            reportPanel.add(report2);
            reportPanel.add(txtArea);
            reportPanel.add(report3);

            cardPanel.add(reportPanel, "SalesReportPanel");
        }
    }
