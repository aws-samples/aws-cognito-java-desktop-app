package com.amazonaws.sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowMessage {

    public static void display(String title, String message) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 400, 500);

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        TextArea scenetitle = new TextArea();

        scenetitle.setText(message);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(scenetitle, 0, 0, 2, 1);
        Button cancelButton = new Button("OK");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.BOTTOM_RIGHT);
        clBtn.getChildren().add(cancelButton);
        clBtn.setMaxWidth(190);
        grid.add(clBtn, 1, 6);
        cancelButton.setOnAction(e -> {
            window.close();
        });

        VBox layout = new VBox(10);

        window.setScene(scene);
        window.showAndWait();
    }

}
