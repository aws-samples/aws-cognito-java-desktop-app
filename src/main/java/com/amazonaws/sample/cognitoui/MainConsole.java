package com.amazonaws.sample.cognitoui;

/*
 *  Copyright 2013-2016 Amazon.com,
 *  Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Amazon Software License (the "License").
 *  You may not use this file except in compliance with the
 *  License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 *  or in the "license" file accompanying this file. This file is
 *  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, express or implied. See the License
 *  for the specific language governing permissions and
 *  limitations under the License.
 */

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.json.JSONObject;

import java.util.Scanner;

public class MainConsole {
    public static void main(String[] args) {
        CognitoHelper helper = new CognitoHelper();

        System.out.println("Welcome to the Cognito Sample. Please enter your choice (1 or 2).\n" +
                "1. Add a new user\n" +
                "2. Authenticate a user and display its buckets\n" +
                "3. Reset password" +
                "");
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException exp) {
            System.out.println("Please enter a choice (1, 2, 3).");
            System.exit(1);
        }
        switch (choice) {
            case 1:
                CreateUser(helper);
                break;
            case 2:
                ValidateUser(helper);
                break;
            case 3:
                ResetPassword(helper);
                break;
            default:
                System.out.println("Valid choices are 1, 2, 3.");
        }
    }

    /**
     * This method creates the users.
     *
     * @param helper CognitoHelper class for performing validations
     */
    private static void CreateUser(CognitoHelper helper) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter a username: ");
        String username = scanner.nextLine();

        System.out.println("Please enter a password: ");
        String password = scanner.nextLine();

        System.out.println("Please enter an email: ");
        String email = scanner.nextLine();

        System.out.println("Please enter a phone number (+11234567890): ");
        String phonenumber = scanner.nextLine();

        boolean success = helper.SignUpUser(username, password, email, phonenumber);
        if (success) {
            System.out.println("User added.");
            System.out.println("Enter your validation code on phone: ");

            String code = scanner.nextLine();
            helper.VerifyAccessCode(username, code);
            System.out.println("User verification succeeded.");
        } else {
            System.out.println("User creation failed.");
        }
    }

    /**
     * This method validates the user by entering username and password
     *
     * @param helper CognitoHelper class for performing validations
     */
    private static void ValidateUser(CognitoHelper helper) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the username: ");
        String username = scanner.nextLine();

        System.out.println("Please enter the password: ");
        String password = scanner.nextLine();

        String result = helper.ValidateUser(username, password);
        if (result != null) {
            System.out.println("User is authenticated: " + result);
        } else {
            System.out.println("Username/password is invalid.");
        }

        JSONObject payload = CognitoJWTParser.getPayload(result);
        String provider = payload.get("iss").toString().replace("https://", "");

        Credentials credentials = helper.GetCredentials(provider, result);
        ListBuckets(credentials);
    }


    /**
     * This method allows a user to reset his/her password
     *
     * @param helper CognitoHelper class for performing validations
     */
    private static void ResetPassword(CognitoHelper helper) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the username: ");
        String username = scanner.nextLine();

        String result = helper.ResetPassword(username);
        if (result != null) {
            System.out.println("Reset password code sent: " + result);
        } else {
            System.out.println("Reset password procedure failed.");
            System.exit(1);
        }

        System.out.println("Please enter the reset code: ");
        String code = scanner.nextLine();

        System.out.println("Please enter a new password: ");
        String password = scanner.nextLine();

        String confirmation = helper.UpdatePassword(username, password, code);
        if (confirmation != null) {
            System.out.println("Reset password confirmed: " + confirmation);
        } else {
            System.out.println("Reset password procedure failed.");
            System.exit(1);
        }

    }

    /**
     * List the buckets based on credentials provided.
     *
     * @param credentials AWS credentials which are to be used to list the buckets.
     */
    private static void ListBuckets(Credentials credentials) {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(credentials.getAccessKeyId(), credentials.getSecretKey(), credentials.getSessionToken());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        for (Bucket bucket : s3Client.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
    }
}
