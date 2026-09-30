package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StolPolzovatelya {

    public static void otkryt(Stage okno, Polzovatel polzovatel) {
        Label privetstvie = new Label("Здравствуйте, " + polzovatel.login + "! Ваша роль: «Пользователь»");
        Button knopkaVyhod = new Button("Выйти");
        knopkaVyhod.setOnAction(e -> FormaVhoda.otkryt(okno));

        VBox konteyner = new VBox(15, privetstvie, knopkaVyhod);
        konteyner.setPadding(new Insets(20));

        okno.setTitle("Рабочий стол пользователя");
        okno.setScene(new Scene(konteyner, 360, 200));
    }
}