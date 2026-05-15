package com.quizapp.gui;
import com.quizapp.gui.screens.SplashScreen;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;

public class QuizApplication extends Application {

    private Node hoveredInteractiveNode;

    @Override
    public void start(Stage stage) {
        NavigationManager.setStage(stage);

        Scene scene = new Scene(
                new SplashScreen() ,
                1700,
                1000
        );

        scene.getStylesheets().addAll(
                getClass().getResource("/styles/theme.css").toExternalForm(),
                getClass().getResource("/styles/components.css").toExternalForm(),
                getClass().getResource("/styles/screens.css").toExternalForm(),
                getClass().getResource("/styles/animations.css").toExternalForm()
        );

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (event.getTarget() instanceof Node node) {
                hoveredInteractiveNode = findInteractiveNode(node);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && hoveredInteractiveNode != null) {
                activateNode(hoveredInteractiveNode);
                event.consume();
            }
        });

        stage.setTitle("Quiz Project");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        SoundManager.startMusic();
        stage.show();
    }

    private Node findInteractiveNode(Node node) {
        Node current = node;

        while (current != null) {
            if (current instanceof Button) {
                return current;
            }

            if (current.getOnMouseClicked() != null) {
                return current;
            }

            current = current.getParent();
        }

        return null;
    }

    private void activateNode(Node node) {
        if (node instanceof Button button) {
            if (!button.isDisabled()) {
                button.fire();
            }

            return;
        }

        MouseEvent clickEvent = new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                0,
                0,
                0,
                0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                true,
                false,
                false,
                true,
                false,
                false,
                new PickResult(node, 0, 0)
        );

        node.fireEvent(clickEvent);
    }
}