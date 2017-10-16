package com.amazonaws.sample;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
public class PIN_validation {
    static boolean answer;

    public static boolean display(String title, String message) {
        CognitoHelper helper = new CognitoHelper();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid,400, 500);

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
//        Label label = new Label();
//        label.setText(message);
        Label scenetitle = new Label();
        scenetitle.setText(message);
        scenetitle.setFont(Font.font ("Tahoma", FontWeight.NORMAL, 30));
        grid.add(scenetitle, 0, 0, 2, 1);
        // PIN field
        Label PIN_label = new Label("PIN Code:");
        grid.add(PIN_label, 0, 1);
        TextField PIN_field = new TextField();
        grid.add(PIN_field, 1, 1);

        // password field
        Label password_label = new Label("Password:");
        grid.add(password_label, 0, 2);
        TextField Password = new PasswordField();
        grid.add(Password, 1, 2);

        // Repeat password field
        Label passwordr_label = new Label("Repeat:");
        grid.add(passwordr_label, 0, 3);
        TextField Passwordr = new PasswordField();
        grid.add(Passwordr, 1, 3);

        //Create signup button
        Button signinButton = new Button("Sign-In");
        HBox suBtn = new HBox(10);
        //       suBtn.setAlignment(Pos.BOTTOM_LEFT);
        suBtn.getChildren().add(signinButton);
        suBtn.setMaxWidth(190);
        grid.add(suBtn,0,6);
        // Create cancel button
        Button cancelButton = new Button("Cancel");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.BOTTOM_RIGHT);
        clBtn.getChildren().add(cancelButton);
        clBtn.setMaxWidth(190);
        grid.add(clBtn,1,6);
        Label usercreation_message = new Label();
        usercreation_message.setFont(Font.font ("Tahoma", FontWeight.NORMAL, 30));
        grid.add(usercreation_message, 0, 7, 2, 1);
        //Clicking will set answer and close window
        signinButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);

        //Add buttons
//        layout.getChildren().addAll(label);
//        layout.getChildren().addAll(signUpButton, cancelButton);
//        Scene scene = new Scene(layout, 400, 500);
        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        return answer;
    }

}
