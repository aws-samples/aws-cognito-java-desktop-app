package com.amazonaws.sample;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class ForgotPassword {
    //Create variable
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
        // email field
        Label email_label = new Label("Enter email address:");
        grid.add(email_label, 0, 1);
        TextField emailcode = new TextField();
        grid.add(emailcode, 1, 1);

        //Create password reset link button
        Button passwordResetLinkButton = new Button(" Send PIN Code");
        HBox suBtn = new HBox(10);
        suBtn.setAlignment(Pos.CENTER);
        suBtn.getChildren().add(passwordResetLinkButton);
        suBtn.setMaxWidth(300);
        grid.add(suBtn,0,6);
        // Create cancel button
        Button cancelButton = new Button("        Cancel        ");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.CENTER);
        clBtn.getChildren().add(cancelButton);
        clBtn.setMaxWidth(300);
        grid.add(clBtn,0,8);
        // Create cancel button
        Button codeButton = new Button("I have PIN Code");
        HBox codeBtn = new HBox(10);
        codeBtn.setAlignment(Pos.CENTER);
        codeBtn.getChildren().add(codeButton);
        codeBtn.setMaxWidth(300);
        grid.add(codeBtn,0,7);

        Label passwordreset_message = new Label();
        passwordreset_message.setFont(Font.font ("Tahoma", FontWeight.NORMAL, 30));
        grid.add(passwordreset_message, 0, 7, 2, 1);
        //Clicking will set answer and close window
        passwordResetLinkButton.setOnAction(e -> {

            System.out.println(emailcode.getText());
            window.close();

        });
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        codeButton.setOnAction(e -> {
            boolean answer = PIN_validation.display("re:Invent 2017 - Cognito Workshop", "Reset Password");
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
