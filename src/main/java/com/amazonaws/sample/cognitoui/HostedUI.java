package com.amazonaws.sample.cognitoui;


import com.amazonaws.services.cognitoidentity.model.Credentials;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;


public class HostedUI {

    static void display(String title, String url) {

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

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        grid.add(browser, 1, 1, 2, 5);

        HBox clBtn = new HBox(10);
        clBtn.setAlignment(Pos.BOTTOM_RIGHT);

        Button cancelButton = new Button("OK");

        cancelButton.setOnAction(e -> {
            window.close();
        });
        clBtn.getChildren().add(cancelButton);


        Button credbutton = new Button("Get Credentials");
        credbutton.setOnAction(e -> {

            System.out.println(webEngine.getLocation());
            try {
                URL signinURL = new URL(webEngine.getLocation());
                String code = signinURL.getQuery().split("=")[1];
                System.out.println(code);
                CognitoHelper helper = new CognitoHelper();
                Credentials cred = helper.GetCredentials(code);
                MainForm.ShowUserBuckets(cred);

            } catch (Exception exp) {
                System.out.println(exp);

            }
            //window.close();
        });
        clBtn.getChildren().add(credbutton);
        grid.add(clBtn, 1, 6);

        webEngine.load(url);
        window.setScene(scene);
        window.showAndWait();

    }
}


