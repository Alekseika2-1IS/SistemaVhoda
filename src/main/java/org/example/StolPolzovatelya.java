package org.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StolPolzovatelya {

    // адрес эмулятора данных в сети техникума
    private static final String ADRES_EMULYATORA = "http://192.168.1.200:4444/TransferSimulator/";

    private final ComboBox<String> vyborTipa = new ComboBox<>(FXCollections.observableArrayList(
            "fullName", "snils", "inn", "email", "identityCard"));
    private final Label nadpisDannye = new Label("Данные: —");
    private final Button knopkaProverit = new Button("Проверить данные");
    private final Label nadpisVerdikt = new Label("");

    private String tekushchiyTip = "";
    private String poluchennoeZnachenie = "";

    public static void otkryt(Stage okno, Polzovatel polzovatel) {
        new StolPolzovatelya(okno, polzovatel);
    }

    private StolPolzovatelya(Stage okno, Polzovatel polzovatel) {
        vyborTipa.getSelectionModel().selectFirst();
        knopkaProverit.setVisible(false); // спрятана, пока данных нет
        knopkaProverit.setOnAction(e -> nadpisVerdikt.setText(proverit(tekushchiyTip, poluchennoeZnachenie)));

        Button knopkaPoluchit = new Button("Получить данные");
        knopkaPoluchit.setOnAction(e -> poluchitDannye());

        Button knopkaVyhod = new Button("Выйти");
        knopkaVyhod.setOnAction(e -> FormaVhoda.otkryt(okno));

        VBox konteyner = new VBox(10,
                new Label("Здравствуйте, " + polzovatel.login + "! Ваша роль: «Пользователь»"),
                new Label("Тип данных:"), vyborTipa,
                knopkaPoluchit, nadpisDannye,
                knopkaProverit, nadpisVerdikt,
                knopkaVyhod);
        konteyner.setPadding(new Insets(20));

        okno.setTitle("Рабочий стол пользователя");
        okno.setScene(new Scene(konteyner, 420, 500));
    }

    private void poluchitDannye() {
        tekushchiyTip = vyborTipa.getValue();
        nadpisVerdikt.setText("");
        knopkaProverit.setVisible(false);
        nadpisDannye.setText("Запрос к серверу...");
        // отдельный поток, чтобы окно не подвисало
        new Thread(() -> {
            try {
                HttpClient klient = HttpClient.newHttpClient();
                HttpRequest zapros = HttpRequest.newBuilder(URI.create(ADRES_EMULYATORA + tekushchiyTip)).GET().build();
                HttpResponse<String> otvetServera = klient.send(zapros, HttpResponse.BodyHandlers.ofString());
                if (otvetServera.statusCode() != 200) {
                    Platform.runLater(() -> nadpisDannye.setText("Ошибка сервера: код " + otvetServera.statusCode()
                            + (otvetServera.statusCode() == 500 ? " — обратитесь к главному эксперту" : "")));
                    return;
                }
                Matcher poisk = Pattern.compile("\"value\"\\s*:\\s*\"(.*)\"").matcher(otvetServera.body());
                poluchennoeZnachenie = poisk.find() ? poisk.group(1) : "";
                Platform.runLater(() -> {
                    nadpisDannye.setText("Данные: " + poluchennoeZnachenie);
                    knopkaProverit.setVisible(true); // появляется ТОЛЬКО после получения данных
                });
            } catch (Exception oshibka) {
                Platform.runLater(() -> nadpisDannye.setText("Ошибка: нет связи с сервером. Проверьте сеть и повторите запрос."));
            }
        }).start();
    }

    private String proverit(String tip, String znachenie) {
        String oshibka = null;
        if (tip.equals("fullName")) {
            if (!znachenie.matches("[A-Za-zА-Яа-яЁё\\- ]+")) oshibka = "запрещённые символы в ФИО";
        } else if (tip.equals("snils")) {
            if (!znachenie.matches("\\d{3}-\\d{3}-\\d{3} \\d{2}")) oshibka = "неверный формат СНИЛС";
        } else if (tip.equals("inn")) {
            if (!znachenie.matches("\\d{10}(\\d{2})?")) oshibka = "ИНН должен быть 10 или 12 цифр";
        } else if (tip.equals("email")) {
            if (znachenie.contains(" ") || znachenie.indexOf('@') != znachenie.lastIndexOf('@') || !znachenie.contains("."))
                oshibka = "некорректный e-mail";
        } else if (tip.equals("identityCard")) {
            if (!znachenie.matches("\\d{2} \\d{2} \\d{6}")) oshibka = "неверный формат карты";
        } else {
            oshibka = "неизвестный тип";
        }
        return oshibka == null ? "Данные КОРРЕКТНЫ" : "Данные НЕ корректны: " + oshibka;
    }
}