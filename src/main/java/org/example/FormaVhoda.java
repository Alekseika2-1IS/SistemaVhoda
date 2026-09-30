package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FormaVhoda {

    private final TextField poleLogin = new TextField();
    private final PasswordField poleParol = new PasswordField();
    private final Label nadpisOshibka = new Label("");
    private KaptchaPazl kapcha = new KaptchaPazl();

    public static void otkryt(Stage okno) {
        new FormaVhoda(okno);
    }

    private FormaVhoda(Stage okno) {
        kapcha = new KaptchaPazl();
        nadpisOshibka.setWrapText(true);

        Button knopkaVoyti = new Button("Войти");
        knopkaVoyti.setOnAction(e -> voiti(okno));

        Button knopkaRegistraciya = new Button("Регистрация");
        knopkaRegistraciya.setOnAction(e -> FormaRegistracii.otkryt());

        VBox konteyner = new VBox(10,
                new Label("Логин:"), poleLogin,
                new Label("Пароль:"), poleParol,
                new Label("Соберите картинку из фрагментов (клик по двум кусочкам меняет их местами):"),
                kapcha,
                knopkaVoyti, knopkaRegistraciya,
                nadpisOshibka);
        konteyner.setPadding(new Insets(20));

        okno.setTitle("Авторизация");
        okno.setScene(new Scene(konteyner, 360, 620));
        okno.show();
    }

    private void voiti(Stage okno) {
        String login = poleLogin.getText().trim();
        String parol = poleParol.getText();
        if (login.isEmpty() || parol.isEmpty()) {
            nadpisOshibka.setText("Заполните обязательные поля «Логин» и «Пароль»");
            return;
        }
        try {
            Polzovatel polzovatel = BazaPolzovateley.naitiPoLoginu(login);
            if (polzovatel == null) {
                nadpisOshibka.setText("Вы ввели неверный логин или пароль. Пожалуйста проверьте ещё раз введенные данные");
                return;
            }
            if (polzovatel.zablokirovan) {
                nadpisOshibka.setText("Вы заблокированы. Обратитесь к администратору");
                return;
            }
            if (!kapcha.sobranLi()) {
                uchestOshibku(polzovatel, "Капча собрана неверно. Соберите картинку из фрагментов и попробуйте снова");
                kapcha.peremeshat();
                return;
            }
            String hash = BazaPolzovateley.naitiHashParolya(login);
            if (!hash.equals(HashParolya.poluchitHash(parol))) {
                uchestOshibku(polzovatel, "Вы ввели неверный логин или пароль. Пожалуйста проверьте ещё раз введенные данные");
                poleParol.clear();
                return;
            }
            BazaPolzovateley.sbrositOshibki(polzovatel.id);
            if (polzovatel.rol.equals("Администратор")) {
                StolAdministratora.otkryt(okno, polzovatel);
            } else {
                StolPolzovatelya.otkryt(okno, polzovatel);
            }
        } catch (Exception oshibkaBazy) {
            nadpisOshibka.setText("Ошибка базы данных: " + oshibkaBazy.getMessage());
        }
    }

    private void uchestOshibku(Polzovatel polzovatel, String tekst) throws Exception {
        int chislo = polzovatel.oshibokPodryad + 1;
        boolean blokirovat = chislo >= 3;
        BazaPolzovateley.zapisatOshibku(polzovatel.id, chislo, blokirovat);
        nadpisOshibka.setText(blokirovat
                ? "Вы заблокированы. Обратитесь к администратору"
                : tekst + " (попытка " + chislo + " из 3)");
    }
}