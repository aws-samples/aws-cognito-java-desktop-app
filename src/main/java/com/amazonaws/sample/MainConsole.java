package com.amazonaws.sample;

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


        System.out.println("Welcome to the Cognito Sample. Please enter your choice ( 1 or 2).\n" +
                "1. Add a new user\n" +
                "2. Authenticate a user and display its Buckets\n" +
                "");
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException exp) {
            System.out.println("Please enter a choice between 1 and 2.");
            System.exit(1);
        }
        switch (choice) {
            case 1:
                Createuser(helper);
                break;
            case 2:
                Validateuser(helper);
                break;
            default:
                System.out.println("Valid Choices are 1 or 2");
        }

    }


    /**
     * This method validates the user by entering username and password
     *
     * @param helper CognitoHelper class for performing validations
     */
    private static void Validateuser(CognitoHelper helper) {
        System.out.println("Please enter the username");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        System.out.println("Please enter the password");
        String password = scanner.nextLine();

        String result = helper.ValidateUser(username, password);
        if (result != null) {
            System.out.println("User is authenticated:" + result);
        } else {
            System.out.println("Username / Password is invalid");
        }

        JSONObject payload = CognitoJWTParser.getPayload(result);
        String provider = payload.get("iss").toString().replace("https://", "");


        Credentials credentails = helper.GetCredentials(provider, result);
        ListBuckets(credentails);
    }

    /**
     * This method creates the users.
     *
     * @param helper CognitoHelper class for performing validations
     */
    private static void Createuser(CognitoHelper helper) {
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
            System.out.println("User Added");
            System.out.println("Enter your validation code on phone: ");

            String code = scanner.nextLine();
            helper.VerifyAccessCode(username, code);
            System.out.println("User Verification Successful");
        } else {
            System.out.println("User Creation Failed");
        }
    }

    /**
     * List the buckets based on credentails provided.
     *
     * @param credentails AWS credentials which are to be used to list the buckets.
     */
    private static void ListBuckets(Credentials credentails) {
        BasicSessionCredentials awsCreds = new BasicSessionCredentials(credentails.getAccessKeyId(), credentails.getSecretKey(), credentails.getSessionToken());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        for (Bucket bucket : s3Client.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
    }
}
