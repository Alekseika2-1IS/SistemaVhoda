package org.example;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;  // ✅ TableView живёт в javafx.scene.control
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class StolAdministratora {

    private static TableView<Polzovatel> tablitsa = new TableView<>();

    public static void otkryt(Stage okno, Polzovatel admin) {
        tablitsa = new TableView<>();
        TableColumn<Polzovatel, String> kolLogin = new TableColumn<>("Логин");
        kolLogin.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().login));
        TableColumn<Polzovatel, String> kolRol = new TableColumn<>("Роль");
        kolRol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().rol));
        TableColumn<Polzovatel, String> kolBlok = new TableColumn<>("Заблокирован");
        kolBlok.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().zablokirovan ? "да" : "нет"));
        TableColumn<Polzovatel, String> kolOshibok = new TableColumn<>("Ошибок подряд");
        kolOshibok.setCellValueFactory(d -> new ReadOnlyStringWrapper(String.valueOf(d.getValue().oshibokPodryad)));
        tablitsa.getColumns().addAll(kolLogin, kolRol, kolBlok, kolOshibok);
        tablitsa.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField poleLogin = new TextField();
        poleLogin.setPromptText("логин нового пользователя");
        PasswordField poleParol = new PasswordField();
        poleParol.setPromptText("пароль");
        ComboBox<String> vyborRoli = new ComboBox<>(FXCollections.observableArrayList("Пользователь", "Администратор"));
        vyborRoli.getSelectionModel().selectFirst();
        Label nadpisOshibka = new Label("");
        nadpisOshibka.setWrapText(true);

        Button knopkaDobavit = new Button("Добавить пользователя");
        knopkaDobavit.setOnAction(e -> {
            try {
                String login = poleLogin.getText().trim();
                String parol = poleParol.getText();
                if (login.isEmpty() || parol.isEmpty()) {
                    nadpisOshibka.setText("Заполните логин и пароль нового пользователя");
                    return;
                }
                if (BazaPolzovateley.naitiPoLoginu(login) != null) {
                    nadpisOshibka.setText("Пользователь с логином «" + login + "» уже существует");
                    return;
                }
                BazaPolzovateley.dobavit(login, HashParolya.poluchitHash(parol), vyborRoli.getValue());
                nadpisOshibka.setText("Пользователь «" + login + "» добавлен");
                obnovit();
            } catch (SQLException oshibkaBazy) {
                nadpisOshibka.setText("Ошибка базы данных: " + oshibkaBazy.getMessage());
            }
        });

        Button knopkaIzmenit = new Button("Изменить выбранного");
        knopkaIzmenit.setOnAction(e -> {
            Polzovatel vybran = tablitsa.getSelectionModel().getSelectedItem();
            if (vybran == null) {
                nadpisOshibka.setText("Сначала выберите пользователя в таблице");
                return;
            }
            try {
                String novyiParol = poleParol.getText();
                BazaPolzovateley.izmenit(vybran.id, vyborRoli.getValue(),
                        novyiParol.isEmpty() ? null : HashParolya.poluchitHash(novyiParol));
                nadpisOshibka.setText("Данные пользователя «" + vybran.login + "» обновлены");
                obnovit();
            } catch (SQLException oshibkaBazy) {
                nadpisOshibka.setText("Ошибка базы данных: " + oshibkaBazy.getMessage());
            }
        });

        Button knopkaRazblokirovat = new Button("Разблокировать выбранного");
        knopkaRazblokirovat.setOnAction(e -> {
            Polzovatel vybran = tablitsa.getSelectionModel().getSelectedItem();
            if (vybran == null) {
                nadpisOshibka.setText("Сначала выберите пользователя в таблице");
                return;
            }
            try {
                BazaPolzovateley.razblokirovat(vybran.id);
                nadpisOshibka.setText("Пользователь «" + vybran.login + "» разблокирован");
                obnovit();
            } catch (SQLException oshibkaBazy) {
                nadpisOshibka.setText("Ошибка базы данных: " + oshibkaBazy.getMessage());
            }
        });

        Button knopkaVyhod = new Button("Выйти");
        knopkaVyhod.setOnAction(e -> FormaVhoda.otkryt(okno));

        VBox konteyner = new VBox(10,
                new Label("Администратор: " + admin.login),
                tablitsa,
                poleLogin, poleParol, vyborRoli,
                knopkaDobavit, knopkaIzmenit, knopkaRazblokirovat,
                nadpisOshibka, knopkaVyhod);
        konteyner.setPadding(new Insets(20));

        okno.setTitle("Рабочий стол администратора");
        okno.setScene(new Scene(konteyner, 560, 640));
        obnovit();
    }

    private static void obnovit() {
        try {
            tablitsa.setItems(FXCollections.observableArrayList(BazaPolzovateley.spisok()));
        } catch (SQLException oshibkaBazy) {
            tablitsa.setItems(FXCollections.observableArrayList());
        }
    }
}