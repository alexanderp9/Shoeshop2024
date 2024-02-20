import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Orders {


    public void addToCart(int kundId, int produktId) throws IOException, SQLException {
        int beställningId = getOrderID(kundId);
        Properties p = new Properties();
        String sql = "{CALL addToCart(?, ?, ?)}";
        p.load(Orders.class.getClassLoader().getResourceAsStream("Settings.properties"));

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("username"),
                p.getProperty("password"));
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setInt(1, kundId);
            stmt.setInt(2, produktId);
            stmt.setInt(3, beställningId);

            stmt.execute();
        } finally {

        }
    }

    public int getOrderID(int kundId) throws IOException, SQLException {
        Properties p = new Properties();
        p.load(Orders.class.getClassLoader().getResourceAsStream("Settings.properties"));
        int beställningId = -1;

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("username"),
                p.getProperty("password"))) {

            String sql = "SELECT BeställningID FROM Beställning WHERE KundID_FK = ? AND DATE(Datum) = CURDATE()";
            try (PreparedStatement findStmt = con.prepareStatement(sql)) {
                findStmt.setInt(1, kundId);
                ResultSet rs = findStmt.executeQuery();
                if (rs.next()) {
                    beställningId = rs.getInt("BeställningID");
                }
            }
        }
        return beställningId;
    }
}