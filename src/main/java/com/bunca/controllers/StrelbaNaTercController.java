package com.bunca.controllers;

import com.bunca.Record;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class StrelbaNaTercController implements Initializable {


    public AnchorPane rootPane, playAreaPane, widgets;
    public Button backButton, simButton, playTournament, save;
    public ImageView terc, cursor;
    public Label infoLabel, scoreLabel, windLabel;
    public TextField gravityField, ammoField, usernameField;
    public Rectangle lowWind, medWindR, medWindL, highWindR, highWindL;
    public Slider shootingSlider;
    public Group saveRecordGroup;


    private int score = 0;
    private double windSimValue = 0;
    private boolean sim = false;
    private boolean canShoot = true;
    private boolean tournamet = false;
    private int exhRange1 = 4;
    private int exhRange2 = 2;

    private int ammo = 10;

    PauseTransition crChange = new PauseTransition(Duration.seconds(1));
    MediaPlayer mediaPlayer;

    Record record = new Record();

    Thread windSim = new Thread() {
        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                if (windSimValue <= 10 && windSimValue >= -10) {
                    windSimValue += random.nextInt(20) - 10;
                }
                if (windSimValue < 20 && windSimValue > 10) {
                    windSimValue += random.nextInt(20) - 10;
                }
                if (windSimValue >= 20) {
                    windSimValue += random.nextInt(20) - 15;
                }

                if (windSimValue > -20 && windSimValue < -10) {
                    windSimValue += random.nextInt(20) - 10;
                }
                if (windSimValue <= -20) {
                    windSimValue += random.nextInt(20) - 5;
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showWind(windSimValue);
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
    Thread exhaustSimX = new Thread() {
        @Override
        public void run() {
            Random random = new Random();

            while (true) {
                if (sim) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            exhaEfectX(random.nextInt(exhRange1) - exhRange2);
                        }
                    });
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    Thread exhaustSimY = new Thread() {
        @Override
        public void run() {
            Random random = new Random();


            while (true) {
                if (sim) {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            exhaEfectY(random.nextInt(exhRange1) - exhRange2);
                        }
                    });

                }
                try {
                    Thread.sleep(100);
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
        double tercX = terc.getLayoutX() + terc.getFitWidth() / 3;
        double tercY = terc.getLayoutY() + terc.getFitHeight() / 2;


        exhaustSimX.start();
        exhaustSimY.start();
        windSim.start();

        playAreaPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("X: "+event.getX()+" Y: "+event.getY());
                if (canShoot) {
                    canShoot = false;
                    mediaPlayer.seek(Duration.seconds(0));
                    mediaPlayer.play();
                    strela(tercX, tercY, cursor.getLayoutX() + cursor.getFitWidth() / 2, cursor.getLayoutY() + cursor.getFitHeight() / 2);
                    cursor.setImage(crosshairred);
                    crChange.play();
                }
            }
        });

        simButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (sim && tournamet == false) {
                    windSimValue = 0;
                    sim = false;
                } else {
                    sim = true;
                    windSimValue = 0;
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


        playAreaPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                cursor.setLayoutX(mouseEvent.getX() - cursor.getFitWidth() / 2);
                cursor.setLayoutY(mouseEvent.getY() - cursor.getFitHeight() / 2);
            }
        });
        playAreaPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                cursor.setLayoutX(mouseEvent.getX() - cursor.getFitWidth() / 2);
                cursor.setLayoutY(mouseEvent.getY() - cursor.getFitHeight() / 2);
            }
        });

        widgets.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rootPane.setCursor(Cursor.DEFAULT);
                cursor.setVisible(false);
            }
        });

        playAreaPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cursor.setVisible(true);
            }
        });

        shootingSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == 0) {
                    exhRange1 = 4;
                    exhRange2 = 2;
                }
                if (newValue.intValue() == 1) {
                    exhRange1 = 10;
                    exhRange2 = 5;
                }
            }
        });


        playTournament.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sim = true;
                try {
                    ammo = Integer.parseInt(ammoField.getText());
                    if (ammo <= 0) {
                        ammo = 10;
                    }
                } catch (Exception e) {
                    ammo = 10;
                }
                int stance = (int) shootingSlider.getValue();
                tournamet = true;
                score = 0;

                record.setAmmo(ammo);
                record.setStance(stance);

                scoreLabel.setText("Score: " + score);
                ammoField.setText(ammo + "");

                gravityField.setDisable(true);
                shootingSlider.setDisable(true);
                ammoField.setEditable(false);
                playTournament.setVisible(false);
                simButton.setVisible(false);
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (usernameField.getText().equals("")) {
                    record.setUsername("User");
                } else {
                    record.setUsername(usernameField.getText());
                }
                gravityField.setDisable(false);
                saveRecordGroup.setVisible(false);
                shootingSlider.setDisable(false);
                playTournament.setVisible(true);
                simButton.setVisible(true);
                saveRecord(record);
                record = new Record();

            }
        });


        crChange.setOnFinished(event -> {
            cursor.setImage(crosshairgreen);
            canShoot = true;
        });

        lowWind.setVisible(false);
        medWindR.setVisible(false);
        medWindL.setVisible(false);
        highWindR.setVisible(false);
        highWindL.setVisible(false);

    }

    public void exhaEfectX(double x) {
        cursor.setLayoutX(cursor.getLayoutX() + x);

    }

    public void exhaEfectY(double y) {
        cursor.setLayoutY(cursor.getLayoutY() + y);
    }


    public void showWind(double windValue) {
        if (windValue <= 10 && windValue >= -10) {
            lowWind.setVisible(true);

            medWindR.setVisible(false);
            highWindR.setVisible(false);

            medWindL.setVisible(false);
            highWindL.setVisible(false);
        }
        if (windValue < 20 && windValue > 10) {
            lowWind.setVisible(true);

            medWindR.setVisible(true);
            highWindR.setVisible(false);

            medWindL.setVisible(false);
            highWindL.setVisible(false);
        }
        if (windValue >= 20) {
            lowWind.setVisible(true);

            medWindR.setVisible(true);
            highWindR.setVisible(true);

            medWindL.setVisible(false);
            highWindL.setVisible(false);
        }

        if (windValue > -20 && windValue < -10) {
            lowWind.setVisible(true);

            medWindR.setVisible(false);
            highWindR.setVisible(false);

            medWindL.setVisible(true);
            highWindL.setVisible(false);
        }
        if (windValue <= -20) {
            lowWind.setVisible(true);

            medWindR.setVisible(false);
            highWindR.setVisible(false);

            medWindL.setVisible(true);
            highWindL.setVisible(true);
        }

        windLabel.setText(windValue + "");
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
            strelaY += grav;
            System.out.println(windSimValue);
        }

        double distance = Math.sqrt(Math.pow(tercX - strelaX, 2) + Math.pow(tercY - strelaY, 2));
        Circle hole = new Circle();
        hole.setRadius(4);
        hole.setCenterX(strelaX);
        hole.setCenterY(strelaY);
        playAreaPane.getChildren().add(playAreaPane.getChildren().indexOf(cursor) - 1, hole);

        hole.setFill(Paint.valueOf("gray"));

        System.out.println(distance);

        if (distance <= 40) {
            score += 100;
            infoLabel.setText("Trafil si zltu.");
            System.out.println("Trafil si zltu.");
        } else if (distance > 40 && distance <= 100) {
            score += 50;
            infoLabel.setText("Trafil si cervenu.");
            System.out.println("Trafil si cervenu.");
        } else if (distance > 100 && distance <= 155) {
            score += 25;
            infoLabel.setText("Trafil si modru.");
            System.out.println("Trafil si modru.");
        } else if (distance > 155 && distance <= 200) {
            score += 10;
            infoLabel.setText("Trafil si ciernu.");
            System.out.println("Trafil si ciernu.");
        } else {
            infoLabel.setText("Netrafil si terc.");
            System.out.println("Netrafil si terc.");
        }
        scoreLabel.setText("Score: " + score);

        if (tournamet) {
            if (ammo > 0) ammo--;
            if (ammo == 0) {
                tournamet = false;
                saveRecordGroup.setVisible(true);
            }
            ammoField.setText(ammo + "");
            record.setScore(score);
        }
    }

    public void saveRecord(Record record){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("records.txt",true));
            writer.append("\n"+record.getUsername()+";-;"+record.getScore()+";-;"+record.getAmmo()+";-;"+record.getStance()+";-;"+record.getCoeficient());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
