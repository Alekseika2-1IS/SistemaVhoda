package org.example;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KaptchaPazl extends GridPane {

    private static final double RAZMER_PLITKI = 110;
    private static final int STORON = 2; // сетка 2x2 = 4 фрагмента

    private final List<Image> kartinki = new ArrayList<>();   // 4 исходных фрагмента
    private final List<ImageView> plitki = new ArrayList<>(); // что показываем в клетках
    private final List<Integer> poryadok = new ArrayList<>(); // какой фрагмент стоит на позиции i
    private int vybrannaya = -1;

    public KaptchaPazl() {
        for (int i = 1; i <= STORON * STORON; i++) {
            kartinki.add(new Image(getClass().getResourceAsStream("/kaptcha" + i + ".png")));
            poryadok.add(i - 1);
        }
        peremeshat();
        for (int mesto = 0; mesto < STORON * STORON; mesto++) {
            ImageView plitka = new ImageView();
            plitka.setFitWidth(RAZMER_PLITKI);
            plitka.setFitHeight(RAZMER_PLITKI);
            plitka.setPreserveRatio(true);
            final int m = mesto;
            plitka.setOnMouseClicked(e -> klik(m));

            // StackPane держит фиксированный размер клетки (ImageView сам не умеет)
            StackPane yacheika = new StackPane(plitka);
            yacheika.setPrefSize(RAZMER_PLITKI, RAZMER_PLITKI);
            yacheika.setMinSize(RAZMER_PLITKI, RAZMER_PLITKI);
            yacheika.setMaxSize(RAZMER_PLITKI, RAZMER_PLITKI);
            yacheika.setStyle("-fx-border-color: #aaaaaa; -fx-border-width: 1;");
            add(yacheika, mesto % STORON, mesto / STORON);
            plitki.add(plitka);
        }
        pererisovat();
        setHgap(4);
        setVgap(4);
    }

    private void klik(int mesto) {
        if (vybrannaya == -1) {
            vybrannaya = mesto;
            plitki.get(mesto).setOpacity(0.5); // подсветка выбранного фрагмента
        } else {
            Collections.swap(poryadok, vybrannaya, mesto);
            plitki.get(vybrannaya).setOpacity(1);
            vybrannaya = -1;
            pererisovat();
        }
    }

    private void pererisovat() {
        for (int i = 0; i < plitki.size(); i++) {
            plitki.get(i).setImage(kartinki.get(poryadok.get(i)));
        }
    }

    public void peremeshat() {
        do {
            Collections.shuffle(poryadok);
        } while (sobranLi());
        pererisovat();
    }

    public boolean sobranLi() {
        for (int i = 0; i < poryadok.size(); i++) {
            if (poryadok.get(i) != i) {
                return false;
            }
        }
        return true;
    }
}