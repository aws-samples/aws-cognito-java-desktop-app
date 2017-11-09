package com.amazonaws.sample.cognitoui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class ConfirmUser {
    //Create variable


     static boolean display(String title, String message, String username) {
        boolean answer=false;
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

        Label scenetitle = new Label();
        scenetitle.setText(message);

        grid.add(scenetitle, 0, 0, 2, 1);
        // Username field
        Label otpcode_label = new Label("Enter OTP Code:");
        grid.add(otpcode_label, 0, 1);
        TextField otpcode = new TextField();
        grid.add(otpcode, 1, 1);

        //Create signup button
        Button signUpButton = new Button("Sign-Up");
        HBox suBtn = new HBox(10);
        //       suBtn.setAlignment(Pos.BOTTOM_LEFT);
        suBtn.getChildren().add(signUpButton);
        suBtn.setMaxWidth(190);
        grid.add(suBtn,0,6);
        // Create cancel button
        Button cancelButton = new Button("Cancel");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.BOTTOM_RIGHT);
        clBtn.getChildren().add(cancelButton);
        clBtn.setMaxWidth(190);
        grid.add(clBtn,1,6);
        Label otp_message = new Label();

        grid.add(otp_message, 0, 7, 2, 1);
        //Clicking will set answer and close window
        signUpButton.setOnAction(e -> {
            boolean success=helper.VerifyAccessCode(username, otpcode.getText());
            if (success){
                System.out.println("OTP validation is Successful");
                otp_message.setText("OTP validation is Successful");

            }
            else {
                System.out.println("OTP validation has failed");
                otp_message.setText("OTP validation has failed/Re-enter OTP code");
            }

        });
        cancelButton.setOnAction(e -> {

            window.close();
        });

        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
