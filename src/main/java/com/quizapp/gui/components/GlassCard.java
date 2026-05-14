package com.quizapp.gui.components;

import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GlassCard extends StackPane {

    public GlassCard() {
        setPadding(new Insets(34));
        setMaxWidth(900);

        setBackground(new Background(new BackgroundFill(
                Color.rgb(255 , 255 , 255 , 0.055),
                new CornerRadii(34),
                Insets.EMPTY
        )));

        setBorder(new Border(new BorderStroke(
                Color.rgb(255 , 255 , 255 , 0.16),
                BorderStrokeStyle.SOLID,
                new CornerRadii(34),
                new BorderWidths(1.1)
        )));

        DropShadow outerGlow = new DropShadow();
        outerGlow.setRadius(42);
        outerGlow.setSpread(0.08);
        outerGlow.setOffsetY(16);
        outerGlow.setColor(Color.rgb(0 , 0 , 0 , 0.42));

        InnerShadow innerHighlight = new InnerShadow();
        innerHighlight.setRadius(28);
        innerHighlight.setColor(Color.rgb(255 , 255 , 255 , 0.055));

        outerGlow.setInput(innerHighlight);
        setEffect(outerGlow);
    }
}