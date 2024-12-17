package org.example.goodreads;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class GoodreadsApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String dbUser = dotenv.get("MYSQL_USER");
        String dbPassword = dotenv.get("MYSQL_PASSWORD");
        String dbUrl = dotenv.get("MYSQL_URL");

        System.setProperty("MYSQL_USER", dbUser);
        System.setProperty("MYSQL_PASSWORD", dbPassword);
        System.setProperty("MYSQL_URL", dbUrl);

        if (!isDatabaseConnected(dbUrl, dbUser, dbPassword)) {
            System.err.println("Nie wykryto połączenia z bazą danych!");
        } else {
            System.out.println("Poprawnie nawiązano połączenie z bazą danych.");
        }
        SpringApplication.run(GoodreadsApplication.class, args);
    }


    private static boolean isDatabaseConnected(String url, String username, String password) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection != null) return true;
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą danych: " + e.getMessage());
        }
        return false;
    }
}
