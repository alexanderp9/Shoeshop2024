import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class Reports {
    private final List<String> reports = new ArrayList<>();

    public void fetchSalesData() throws IOException {
        Properties p = new Properties();
        p.load(getClass().getClassLoader().getResourceAsStream("Settings.properties"));

        String sql = """
                SELECT Kund.Namn, Kund.Adress, Kund.Ort, Produkter.Storlek, Produkter.Färg, Produkter.Märke, Produkter.Pris * Innehåller.Antal AS TotalPris
                FROM Beställning
                JOIN Kund ON Beställning.KundID_FK = Kund.KundID
                JOIN Innehåller ON Beställning.BeställningID = Innehåller.BeställningID_FK
                JOIN Produkter ON Innehåller.ProduktID_FK = Produkter.ProduktID
                """;

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("username"),
                p.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            reports.clear(); // för att rensa datat och inte få dubbletter skrivna när vi sedan kallar på metoden fram och tillbaka
            while (rs.next()) {
                String report = String.format("Kund: %s, Adress: %s, Ort: %s, Produkt: %s, Färg: %s, Storlek: %s, Totalt Pris: %.2f",
                        rs.getString("Namn"),
                        rs.getString("Adress"),
                        rs.getString("Ort"),
                        rs.getString("Märke"),
                        rs.getString("Färg"),
                        rs.getString("Storlek"),
                        rs.getDouble("TotalPris"));
                reports.add(report);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Databas fel: " + e.getMessage());
        }
    }

    public List<String> getReports() {
        return reports;
    }

    @FunctionalInterface
    public interface ReportSearcherInterface {
        boolean search(String report, String searchWord);
    }

    // metoden som använder interfacet för att filtrera rapporter baserat på sökord.
    public void searchForReport(String keyword, ReportSearcherInterface searcher, Consumer<String> consumer) {
        reports.stream()
                .filter(report -> searcher.search(report, keyword))
                .forEach(consumer);
    }
}
