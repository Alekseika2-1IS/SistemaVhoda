package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BazaPolzovateley {

    private static final String ADRES = "jdbc:mysql://localhost:3306/vhod";
    private static final String LOGIN_BD = "apiuser";
    private static final String PAROL_BD = "api12345";

    private static Connection soedinenie() throws SQLException {
        return DriverManager.getConnection(ADRES, LOGIN_BD, PAROL_BD);
    }

    public static Polzovatel naitiPoLoginu(String login) throws SQLException {
        String sql = "SELECT id, login, rol, zablokirovan, oshibok_podryad FROM polzovateli WHERE login = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, login);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                return new Polzovatel(r.getInt("id"), r.getString("login"), r.getString("rol"),
                        r.getBoolean("zablokirovan"), r.getInt("oshibok_podryad"));
            }
            return null;
        }
    }

    public static String naitiHashParolya(String login) throws SQLException {
        String sql = "SELECT parol_hash FROM polzovateli WHERE login = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, login);
            ResultSet r = p.executeQuery();
            return r.next() ? r.getString("parol_hash") : null;
        }
    }

    public static void dobavit(String login, String hashParolya, String rol) throws SQLException {
        String sql = "INSERT INTO polzovateli (login, parol_hash, rol) VALUES (?, ?, ?)";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, login);
            p.setString(2, hashParolya);
            p.setString(3, rol);
            p.executeUpdate();
        }
    }

    public static void zapisatOshibku(int id, int chisloOshibok, boolean blokirovat) throws SQLException {
        String sql = "UPDATE polzovateli SET oshibok_podryad = ?, zablokirovan = ? WHERE id = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, chisloOshibok);
            p.setBoolean(2, blokirovat);
            p.setInt(3, id);
            p.executeUpdate();
        }
    }

    public static void sbrositOshibki(int id) throws SQLException {
        String sql = "UPDATE polzovateli SET oshibok_podryad = 0 WHERE id = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public static void razblokirovat(int id) throws SQLException {
        String sql = "UPDATE polzovateli SET zablokirovan = 0, oshibok_podryad = 0 WHERE id = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    public static void izmenit(int id, String rol, String novyiHashIliNull) throws SQLException {
        String sql = novyiHashIliNull == null
                ? "UPDATE polzovateli SET rol = ? WHERE id = ?"
                : "UPDATE polzovateli SET rol = ?, parol_hash = ? WHERE id = ?";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, rol);
            if (novyiHashIliNull == null) {
                p.setInt(2, id);
            } else {
                p.setString(2, novyiHashIliNull);
                p.setInt(3, id);
            }
            p.executeUpdate();
        }
    }

    public static List<Polzovatel> spisok() throws SQLException {
        List<Polzovatel> spisok = new ArrayList<>();
        String sql = "SELECT id, login, rol, zablokirovan, oshibok_podryad FROM polzovateli ORDER BY id";
        try (Connection c = soedinenie(); PreparedStatement p = c.prepareStatement(sql)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                spisok.add(new Polzovatel(r.getInt("id"), r.getString("login"), r.getString("rol"),
                        r.getBoolean("zablokirovan"), r.getInt("oshibok_podryad")));
            }
        }
        return spisok;
    }
}