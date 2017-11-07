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
public class ResetPassword {
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
        Label scenetitle = new Label();
        scenetitle.setText(message);

        grid.add(scenetitle, 0, 0, 2, 1);

        // PIN field
        Label lblusername = new Label("Username:");
        grid.add(lblusername, 0, 1);
        TextField txtusername = new TextField();
        txtusername.setText(username);
        grid.add(txtusername, 1, 1);


        // PIN field
        Label lblpincode = new Label("PIN Code:");
        grid.add(lblpincode, 0, 2);
        TextField txtpincode = new TextField();
        grid.add(txtpincode, 1, 2);

        // password field
        Label lblpassword = new Label("Password:");
        grid.add(lblpassword, 0, 3);
        TextField txtpassword = new PasswordField();
        grid.add(txtpassword, 1, 3);



        //Create signup button
        Button resetPassword = new Button("Reset Password");
        HBox suBtn = new HBox(10);
        //       suBtn.setAlignment(Pos.BOTTOM_LEFT);
        suBtn.getChildren().add(resetPassword);
        suBtn.setMaxWidth(190);
        grid.add(suBtn,0,6);
        // Create cancel button
        Button cancelButton = new Button("Cancel");
        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.BOTTOM_RIGHT);
        clBtn.getChildren().add(cancelButton);
        suBtn.setMaxWidth(190);
        grid.add(clBtn,1,6 );
        Label lblmessage = new Label();

        grid.add(lblmessage, 0, 7, 2, 1);
        //Clicking will set answer and close window
        resetPassword.setOnAction(e -> {
            answer = true;
            try {
                helper.UpdatePassword(txtusername.getText(), txtpassword.getText(), txtpincode.getText());
                lblmessage.setText("Password reset successfully!");
            }catch (Exception exp)
            {
                System.out.println(exp);
                answer=false;

            }

        });
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
