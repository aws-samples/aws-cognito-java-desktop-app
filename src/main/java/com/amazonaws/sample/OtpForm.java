package com.amazonaws.sample;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class OtpForm {
    //Create variable
    static boolean answer;

    public static boolean display(String title, String message, String username) {
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
        otp_message.setFont(Font.font ("Tahoma", FontWeight.NORMAL, 30));
        grid.add(otp_message, 0, 7, 2, 1);
        //Clicking will set answer and close window
        signUpButton.setOnAction(e -> {
            helper.VerifyAccessCode(username, otpcode.getText());
            System.out.println("User Verification Successful");
            otp_message.setText("User Verification Successful");
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
