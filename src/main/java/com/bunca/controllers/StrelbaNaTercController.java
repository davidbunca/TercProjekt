package com.bunca.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
import java.util.Random;
import java.util.ResourceBundle;

public class StrelbaNaTercController implements Initializable {


    public AnchorPane rootPane, playAreaPane;
    public Button backButton, simButton;
    public ImageView terc;
    public Label infoLabel, scoreLabel, windLabel;
    public ImageView windImage;
    public Slider windSlider;
    public TextField gravityField;

    private int score = 0;

    private double windSimValue = 0;
    private double windStrenght = 1;
    private boolean sim = false;

    PauseTransition crChange = new PauseTransition(Duration.seconds(1));
    MediaPlayer mediaPlayer;

    Thread windSim = new Thread() {
        @Override
        public void run() {
            Random random = new Random();

            while (true) {
                windStrenght = random.nextInt(2) + 1;
                if (windSimValue > 0) {
                    windSimValue += windStrenght * random.nextInt(10) - 6;
                }
                if (windSimValue < 0) {
                    windSimValue += windStrenght * random.nextInt(10) - 4;
                }
                if (windSimValue == 0) {
                    windSimValue += windStrenght * random.nextInt(10) - 5;
                }
                if (windSimValue > 100) {
                    windSimValue = 100;
                }
                if (windSimValue < -100) {
                    windSimValue = -100;
                }
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        showWind(windStrenght, windSimValue);
                    }
                });


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image crosshairgreen = new Image(getClass().getResource("/images/crosshairgreen.png").toString());
        Image crosshairred = new Image(getClass().getResource("/images/crosshairred.png").toString());
        mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/sounds/strela.mp3").toString()));
        System.out.println("[Terc] X:" + (terc.getLayoutX() + terc.getFitWidth() / 2) + " Y: " + (terc.getLayoutY() + terc.getFitHeight() / 2));
        double tercX = terc.getLayoutX() + (terc.getFitWidth() / 2);
        double tercY = terc.getLayoutY() + (terc.getFitHeight() / 2);

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
                if (sim) {
                    windSimValue = 0;
                    sim = false;
                } else {
                    windSim.start();
                    sim = true;
                }
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

    public void showWind(double windStrenght, double windValue) {
        if (windValue < 0) {
            if (windStrenght == 1){
                windImage.setImage(new Image(getClass().getResource("/images/leftw.png").toString()));
            }else windImage.setImage(new Image(getClass().getResource("/images/left.png").toString()));
            windLabel.setText(String.valueOf(windValue * -1));
        } else if (windValue == 0) {
            windImage.setImage(null);
        } else {
            if (windStrenght == 1){
                windImage.setImage(new Image(getClass().getResource("/images/rightw.png").toString()));
            }else windImage.setImage(new Image(getClass().getResource("/images/right.png").toString()));
            windLabel.setText(String.valueOf(windValue));
        }

    }

    private void strela(double tercX, double tercY, double strelaX, double strelaY) {
        double grav = 9.81;
        if (!gravityField.equals("")) {
            try {
                grav = Double.parseDouble(gravityField.getText());
            } catch (Exception e) {
                grav = 9.81;
                gravityField.setText("");
            }
        }

        if (sim) {
            strelaX += windSimValue;
            System.out.println(windSimValue);
        } else strelaX += windSlider.getValue();
        strelaY += grav;
        double distance = Math.sqrt(Math.pow(tercX - strelaX, 2) + Math.pow(tercY - strelaY, 2));
        Circle hole = new Circle();
        hole.setRadius(4);
        hole.setCenterX(strelaX);
        hole.setCenterY(strelaY);
        playAreaPane.getChildren().add(hole);
        hole.setFill(Paint.valueOf("white"));

        System.out.println(distance);

        if (distance <= 64) {
            score += 30;
            infoLabel.setText("Trafil si zltu.");
            System.out.println("Trafil si zltu.");
        } else if (distance > 64 && distance <= 137.5) {
            score += 15;
            infoLabel.setText("Trafil si cervenu.");
            System.out.println("Trafil si cervenu.");
        } else if (distance > 137.5 && distance <= 171) {
            score += 10;
            infoLabel.setText("Trafil si modru.");
            System.out.println("Trafil si modru.");
        } else if (distance > 171 && distance <= 217) {
            score += 8;
            infoLabel.setText("Trafil si ciernu.");
            System.out.println("Trafil si ciernu.");
        } else {
            infoLabel.setText("Netrafil si terc.");
            System.out.println("Netrafil si terc.");
        }
        scoreLabel.setText("Score: " + score);
    }
}
