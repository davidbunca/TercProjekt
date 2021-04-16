package com.bunca.controllers;

import com.bunca.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    public Label lukostrelbaLabel;
    public AnchorPane rootPane;
    public TableView<Record> table;
    public TableColumn<Record,String> nameC,scoreC,ammoC,coC;

    ArrayList<Record> records = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lukostrelbaLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane pane = null;
                try {
                    pane = FXMLLoader.load(getClass().getResource("/fxmls/strelaNaTerc.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rootPane.getChildren().setAll(pane);
            }
        });
        loadRecords();

        nameC.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreC.setCellValueFactory(new PropertyValueFactory<>("score"));
        ammoC.setCellValueFactory(new PropertyValueFactory<>("ammo"));
        coC.setCellValueFactory(new PropertyValueFactory<>("coeficient"));

        table.setItems(FXCollections.observableArrayList(records));

        coC.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(coC);

    }

    public void loadRecords() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("records.txt"));
            String line = reader.readLine();
            do {
                line = reader.readLine();
                if (line!= null) {
                    String[] splLine = line.split(";-;");
                    records.add(new Record(splLine[0], Integer.parseInt(splLine[1]), Integer.parseInt(splLine[2]), Integer.parseInt(splLine[3]), Double.parseDouble(splLine[4])));
                }
            } while (line!=null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(records);

    }
}
