package com.bunca.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StrelbaNaTercController implements Initializable {


    public AnchorPane rootPane, playAreaPane;
    public Button backButton, simButton;
    public ImageView terc;
    public Label infoLabel;
    public Slider windSlider;
    public TextField gravityField;

    PauseTransition crChange = new PauseTransition(Duration.seconds(1));
    MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image crosshairgreen = new Image(getClass().getResource("/images/crosshairgreen.png").toString());
        Image crosshairred = new Image(getClass().getResource("/images/crosshairred.png").toString());
        mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/sounds/strela.mp3").toString()));
        System.out.println("[Terc] X:" + (terc.getLayoutX() + terc.getFitWidth() / 2) + " Y: " + (terc.getLayoutY() + terc.getFitHeight() / 2));
        double tercX = terc.getLayoutX() + terc.getFitWidth() / 2;
        double tercY = terc.getLayoutY() + terc.getFitHeight() / 2;

        playAreaPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("X: "+event.getX()+" Y: "+event.getY());
                mediaPlayer.seek(Duration.seconds(0));
                mediaPlayer.play();
                strela(tercX, tercY, event.getX(), event.getY());
                playAreaPane.setCursor(new ImageCursor(crosshairred, crosshairred.getWidth() / 2, crosshairred.getHeight() / 2));
                crChange.play();
            }
        });

        simButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane pane = null;
                try {
                    pane = FXMLLoader.load(getClass().getResource("/fxmls/menu.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rootPane.getChildren().setAll(pane);
            }
        });

        playAreaPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playAreaPane.setCursor(new ImageCursor(crosshairgreen, crosshairgreen.getWidth() / 2, crosshairgreen.getHeight() / 2));
            }
        });
        rootPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rootPane.setCursor(Cursor.DEFAULT);
            }
        });

        crChange.setOnFinished(event -> {
            playAreaPane.setCursor(new ImageCursor(crosshairgreen, crosshairgreen.getWidth() / 2, crosshairgreen.getHeight() / 2));
        });
    }

    private void strela(double tercX, double tercY, double strelaX, double strelaY) {
        double grav = 9.81;
        if (!gravityField.equals("")){
            try {
                grav = Double.parseDouble(gravityField.getText());
            }catch (Exception e){
                grav = 9.81;
                gravityField.setText("");
            }
        }
        strelaX += windSlider.getValue();
        strelaY += grav;
        double distance = Math.sqrt(Math.pow(tercX - strelaX, 2) + Math.pow(tercY - strelaY, 2));
        Circle hole = new Circle();
        hole.setRadius(4);
        hole.setCenterX(strelaX);
        hole.setCenterY(strelaY);
        playAreaPane.getChildren().add(hole);
        hole.setFill(Paint.valueOf("gray"));
        if (distance <= 64) {
            infoLabel.setText("Trafil si zltu.");
            System.out.println("Trafil si zltu.");
        } else if (distance > 64 && distance <= 137.5) {
            infoLabel.setText("Trafil si cervenu.");
            System.out.println("Trafil si cervenu.");
        } else if (distance > 137.5 && distance <= 171) {
            infoLabel.setText("Trafil si modru.");
            System.out.println("Trafil si modru.");
        } else if (distance > 171 && distance <= 217) {
            infoLabel.setText("Trafil si ciernu.");
            System.out.println("Trafil si ciernu.");
        } else {
            infoLabel.setText("Netrafil si terc.");
            System.out.println("Netrafil si terc.");
        }
    }
}
