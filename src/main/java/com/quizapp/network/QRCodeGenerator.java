package com.quizapp.network;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    public static Image generate(String text , int size) {

        try {

            Map<EncodeHintType , Object> hints = new HashMap<>();

            hints.put(
                    EncodeHintType.MARGIN,
                    1
            );

            BitMatrix matrix =
                    new MultiFormatWriter().encode(
                            text,
                            BarcodeFormat.QR_CODE,
                            size,
                            size,
                            hints
                    );

            BufferedImage image =
                    new BufferedImage(
                            size,
                            size,
                            BufferedImage.TYPE_INT_RGB
                    );

            for (int x = 0; x < size; x++) {

                for (int y = 0; y < size; y++) {

                    image.setRGB(
                            x,
                            y,
                            matrix.get(x , y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF
                    );
                }
            }

            return SwingFXUtils.toFXImage(
                    image,
                    null
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}