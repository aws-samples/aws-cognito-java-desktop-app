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

        grid.add(scenetitle, 0, 0, 2, 1);
        // email field
        Label email_label = new Label("Username:");
        grid.add(email_label, 0, 1);
        TextField txtusername = new TextField();
        grid.add(txtusername, 1, 1);

        //Create password reset link button
        Button passwordResetLinkButton = new Button("Reset Request");
        HBox suBtn = new HBox(10);
        suBtn.setAlignment(Pos.CENTER);
        suBtn.getChildren().add(passwordResetLinkButton);
        suBtn.setMaxWidth(300);
        grid.add(suBtn,0,6);


        Button codeButton = new Button("Have reset code?");
        HBox codeBtn = new HBox(10);
        codeBtn.setAlignment(Pos.CENTER);
        codeBtn.getChildren().add(codeButton);
        codeBtn.setMaxWidth(300);
        grid.add(codeBtn,1,6);



        // Create cancel button
        Button cancelButton = new Button("Cancel");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.CENTER);
        clBtn.getChildren().add(cancelButton);
        clBtn.setMaxWidth(300);
        grid.add(clBtn,0,7, 2,1);

        //Clicking will set answer and close window
        passwordResetLinkButton.setOnAction(e -> {

            System.out.println(txtusername.getText());
            helper.ResetPassword(txtusername.getText());
            boolean answer = ResetPassword.display("re:Invent 2017 - Cognito Workshop", "Reset Password",txtusername.getText());
            if (answer) {
                System.out.println("Password reset successful");
            }else
            {
                System.out.println("Password reset failed");
            }

        });
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        codeButton.setOnAction(e -> {
            boolean answer = ResetPassword.display("re:Invent 2017 - Cognito Workshop", "Reset Password",txtusername.getText());
            if (answer) {
                System.out.println("Password reset successful");
            }else
            {
                System.out.println("Password reset failed");
            }

        });



        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        return answer;
    }

}
