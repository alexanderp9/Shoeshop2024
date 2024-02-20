import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Products {
    private final int produktID;
    private final String category;

    private final String brand;

    private final String colour;

    private final double size;

    private double price;

    public void setStockAmount(int stockAmount) {
        this.stockAmount = stockAmount;
    }

    private int stockAmount;

    private Products(int produktID,String category, String brand, String colour, double size, double price, int stockAmount) {
        this.produktID = produktID;
        this.category = category;
        this.brand = brand;
        this.colour = colour;
        this.size = size;
        this.price = price;
        this.stockAmount = stockAmount;
    }

    public static List<Products> queryProducts() throws IOException, SQLException {
        List<Products> products = new ArrayList<>();
        Properties p = new Properties();
        String sql = "SELECT p.ProduktID, p.Märke, p.Färg, p.Storlek, p.Pris, p.Antal, k.Namn AS Kategori " +
                "FROM produkter p " +
                "JOIN skor s ON p.ProduktID = s.ProduktID_FK " +
                "JOIN kategori k ON s.KategoriID_FK = k.KategoriID";
        p.load(Customer.class.getClassLoader().getResourceAsStream("Settings.properties"));

        try {

            Connection con = DriverManager.getConnection(
                    p.getProperty("connectionString"),
                    p.getProperty("username"),
                    p.getProperty("password"));

            PreparedStatement ps = con.prepareStatement(sql);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Products product = new Products(rs.getInt("ProduktID"),
                            rs.getString("Kategori"),
                            rs.getString("Märke"),
                            rs.getString("Färg"),
                            rs.getDouble("Storlek"),
                            rs.getDouble("Pris"),
                            rs.getInt("Antal")
                    );
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    public int getProduktID() {
        return produktID;
    }
    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getColour() {
        return colour;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public int getStockAmount() {
        return stockAmount;
    }
}
