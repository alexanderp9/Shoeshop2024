import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Customer {
    private final int customerId;
    private final String name;
    private final String lastName;
    private String email;

    private Customer(int customerId, String name, String lastName, String email ) {
        this.customerId = customerId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
    }

    public static Customer queryLogin(String email, String password) throws IOException, SQLException {
        Properties p = new Properties();
        String sql = "SELECT * FROM kund WHERE epost = ? and password = ?";
        p.load(Customer.class.getClassLoader().getResourceAsStream("Settings.properties"));
        try {
            Connection con = DriverManager.getConnection(
                    p.getProperty("connectionString"),
                    p.getProperty("username"),
                    p.getProperty("password"));
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("KundID"),
                            rs.getString("namn"),
                            rs.getString("efternamn"),
                            rs.getString("epost")
                    );
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            
        } return null;
    } 


    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}