package com.quizapp.gui.effects;

import java.util.Random;

import com.quizapp.gui.AppSettings;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimatedBackground extends Pane {

    private final Random random = new Random();

    public AnimatedBackground() {
        setMouseTransparent(true);

        Rectangle base = new Rectangle();
        base.widthProperty().bind(widthProperty());
        base.heightProperty().bind(heightProperty());

        base.setFill(new LinearGradient(
                0 , 0 , 1 , 1 , true , CycleMethod.NO_CYCLE,
                new Stop(0 , Color.web("#050014")),
                new Stop(0.25 , Color.web("#11102E")),
                new Stop(0.55 , Color.web("#071A2F")),
                new Stop(0.78 , Color.web("#1A0B2E")),
                new Stop(1 , Color.web("#020617"))
        ));

        Circle purple = createBlob(720 , Color.rgb(168 , 85 , 247 , 0.36));
        Circle blue = createBlob(760 , Color.rgb(59 , 130 , 246 , 0.30));
        Circle pink = createBlob(680 , Color.rgb(244 , 63 , 94 , 0.26));
        Circle cyan = createBlob(580 , Color.rgb(34 , 211 , 238 , 0.24));
        Circle orange = createBlob(520 , Color.rgb(251 , 146 , 60 , 0.18));

        purple.layoutXProperty().bind(widthProperty().multiply(0.12));
        purple.layoutYProperty().bind(heightProperty().multiply(0.22));

        blue.layoutXProperty().bind(widthProperty().multiply(0.86));
        blue.layoutYProperty().bind(heightProperty().multiply(0.18));

        pink.layoutXProperty().bind(widthProperty().multiply(0.82));
        pink.layoutYProperty().bind(heightProperty().multiply(0.82));

        cyan.layoutXProperty().bind(widthProperty().multiply(0.18));
        cyan.layoutYProperty().bind(heightProperty().multiply(0.82));

        orange.layoutXProperty().bind(widthProperty().multiply(0.50));
        orange.layoutYProperty().bind(heightProperty().multiply(0.52));

        getChildren().addAll(base , purple , blue , pink , cyan , orange);

        createStars(180);

        if (AppSettings.isAnimationsEnabled()) {
            animateBlob(purple , 300 , -220 , 10);
            animateBlob(blue , -320 , 240 , 12);
            animateBlob(pink , 260 , -300 , 11);
            animateBlob(cyan , -280 , 220 , 13);
            animateBlob(orange , 220 , -220 , 9);

            breathe(purple , 1.00 , 1.22 , 8);
            breathe(blue , 1.00 , 1.18 , 9);
            breathe(pink , 1.00 , 1.18 , 8);
            breathe(cyan , 1.00 , 1.15 , 10);
            breathe(orange , 1.00 , 1.20 , 7);
        }
    }

    private Circle createBlob(double radius , Color color) {
        Circle blob = new Circle(radius);
        blob.setMouseTransparent(true);

        blob.setFill(new RadialGradient(
                0 , 0 , 0.5 , 0.5 , 0.5 , true , CycleMethod.NO_CYCLE,
                new Stop(0 , color),
                new Stop(0.45 , Color.rgb(255 , 255 , 255 , 0.035)),
                new Stop(1 , Color.TRANSPARENT)
        ));

        blob.setEffect(new BoxBlur(130 , 130 , 3));
        blob.setOpacity(1);

        return blob;
    }

    private void createStars(int count) {
        for (int i = 0; i < count; i++) {
            Circle star = new Circle(random.nextDouble() * 2.4 + 0.35);

            double opacity = random.nextDouble() * 0.55 + 0.45;

            if (random.nextBoolean()) {
                star.setFill(Color.rgb(255 , 255 , 255 , opacity));
            } else {
                star.setFill(Color.rgb(125 , 211 , 252 , opacity));
            }

            star.layoutXProperty().bind(widthProperty().multiply(random.nextDouble()));
            star.layoutYProperty().bind(heightProperty().multiply(random.nextDouble()));
            star.setMouseTransparent(true);

            getChildren().add(star);

            if (AppSettings.isAnimationsEnabled()) {
                twinkle(
                        star ,
                        random.nextDouble() * 0.25 + 0.25 ,
                        random.nextDouble() * 0.35 + 0.65 ,
                        random.nextInt(3) + 1
                );

                drift(
                        star ,
                        random.nextDouble() * 120 - 60 ,
                        random.nextDouble() * 120 - 60 ,
                        random.nextInt(10) + 6
                );
            }
        }
    }

    private void animateBlob(Circle blob , double x , double y , int seconds) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(seconds) , blob);

        move.setByX(x);
        move.setByY(y);

        move.setAutoReverse(true);
        move.setCycleCount(Animation.INDEFINITE);
        move.play();
    }

    private void breathe(Circle blob , double from , double to , int seconds) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(seconds) , blob);

        scale.setFromX(from);
        scale.setFromY(from);

        scale.setToX(to);
        scale.setToY(to);

        scale.setAutoReverse(true);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.play();
    }

    private void twinkle(Circle star , double from , double to , int seconds) {
        FadeTransition fade = new FadeTransition(Duration.seconds(seconds) , star);

        fade.setFromValue(from);
        fade.setToValue(to);

        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.play();
    }

    private void drift(Circle star , double x , double y , int seconds) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(seconds) , star);

        move.setByX(x);
        move.setByY(y);

        move.setAutoReverse(true);
        move.setCycleCount(Animation.INDEFINITE);
        move.play();
    }
}