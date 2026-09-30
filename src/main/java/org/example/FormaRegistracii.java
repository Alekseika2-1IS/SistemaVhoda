package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FormaRegistracii {

    private final TextField poleLogin = new TextField();
    private final PasswordField poleParol = new PasswordField();
    private final PasswordField polePovtor = new PasswordField();
    private final Label nadpisOshibka = new Label("");

    public static void otkryt() {
        new FormaRegistracii();
    }

    private FormaRegistracii() {
        Stage okno = new Stage();
        Button knopkaSozdat = new Button("Зарегистрироваться");
        knopkaSozdat.setOnAction(e -> sozdat(okno));

        VBox konteyner = new VBox(10,
                new Label("Логин:"), poleLogin,
                new Label("Пароль:"), poleParol,
                new Label("Повторите пароль:"), polePovtor,
                knopkaSozdat, nadpisOshibka);
        konteyner.setPadding(new Insets(20));

        okno.setTitle("Регистрация");
        okno.setScene(new Scene(konteyner, 320, 300));
        okno.show();
    }

    private void sozdat(Stage okno) {
        String login = poleLogin.getText().trim();
        String parol = poleParol.getText();
        if (login.isEmpty() || parol.isEmpty()) {
            nadpisOshibka.setText("Заполните все поля формы");
            return;
        }
        if (!parol.equals(polePovtor.getText())) {
            nadpisOshibka.setText("Пароли не совпадают. Введите одинаковый пароль в оба поля");
            return;
        }
        try {
            if (BazaPolzovateley.naitiPoLoginu(login) != null) {
                nadpisOshibka.setText("Пользователь с логином «" + login + "» уже существует. Выберите другой логин");
                return;
            }
            BazaPolzovateley.dobavit(login, HashParolya.poluchitHash(parol), "Пользователь");
            okno.close();
            Alert uspeh = new Alert(Alert.AlertType.INFORMATION);
            uspeh.setHeaderText("Регистрация завершена");
            uspeh.setContentText("Пользователь «" + login + "» создан с ролью «Пользователь». Теперь войдите в систему.");
            uspeh.showAndWait();
        } catch (Exception oshibkaBazy) {
            nadpisOshibka.setText("Ошибка базы данных: " + oshibkaBazy.getMessage());
        }
    }
}