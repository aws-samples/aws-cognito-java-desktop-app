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

import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.*;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * The CognitoHelper class abstracts the functionality of connecting to the Cognito user pool and Federated Identities.
 */
class CognitoHelper {

    private String POOL_ID;
    private String CLIENTAPP_ID;
    private String FED_POOL_ID;
    private String CUSTOMDOMAIN;
    private String REGION;

    public CognitoHelper() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = getClass().getClassLoader().getResourceAsStream("config.properties");

            // load a properties file
            prop.load(input);

            // Read the property values
            POOL_ID = prop.getProperty("POOL_ID");
            CLIENTAPP_ID = prop.getProperty("CLIENTAPP_ID");
            FED_POOL_ID = prop.getProperty("FED_POOL_ID");
            CUSTOMDOMAIN = prop.getProperty("CUSTOMDOMAIN");
            REGION = prop.getProperty("REGION");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String GetHostedSignInURL() {
        String customurl = "https://%s.auth.%s.amazoncognito.com/login?response_type=code&client_id=%s&redirect_uri=https://sid343.reinvent-workshop.com/";

        return String.format(customurl, CUSTOMDOMAIN, REGION, CLIENTAPP_ID);
    }

    /**
     * Sign up the user to the user pool
     *
     * @param username    User name for the sign up
     * @param password    Password for the sign up
     * @param email       email used to sign up
     * @param phonenumber phone number to sign up.
     * @return whether the call was successful or not.
     */
    boolean SignUpUser(String username, String password, String email, String phonenumber) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        SignUpRequest signUpRequest = new SignUpRequest();

        signUpRequest.setClientId(CLIENTAPP_ID);
        signUpRequest.setUsername(username);
        signUpRequest.setPassword(password);
        List<AttributeType> list = new ArrayList<>();

        AttributeType attributeType = new AttributeType();
        attributeType.setName("phone_number");
        attributeType.setValue(phonenumber);
        list.add(attributeType);

        AttributeType attributeType1 = new AttributeType();
        attributeType1.setName("email");
        attributeType1.setValue(email);
        list.add(attributeType1);

        signUpRequest.setUserAttributes(list);

        try {
            SignUpResult result = cognitoIdentityProvider.signUp(signUpRequest);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * Verify the verification code sent on the user phone.
     *
     * @param username User for which we are submitting the verification code.
     * @param code     Verification code delivered to the user.
     * @return if the verification is successful.
     */
    boolean VerifyAccessCode(String username, String code) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
        confirmSignUpRequest.setUsername(username);
        confirmSignUpRequest.setConfirmationCode(code);
        confirmSignUpRequest.setClientId(CLIENTAPP_ID);

        try {
            ConfirmSignUpResult confirmSignUpResult = cognitoIdentityProvider.confirmSignUp(confirmSignUpRequest);
            System.out.println(confirmSignUpResult.toString());
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

    /**
     * Helper method to validate the user
     *
     * @param username represents the username in the cognito user pool
     * @param password represents the password in the cognito user pool
     * @return returns the JWT token after the validation
     */
    String ValidateUser(String username, String password) {
        AuthenticationHelper helper = new AuthenticationHelper(POOL_ID, CLIENTAPP_ID, "");
        return helper.PerformSRPAuthentication(username, password);
    }

    /**
     * Returns the AWS credentials
     *
     * @param idprovider the IDP provider for the login map
     * @param id         the username for the login map.
     * @return returns the credentials based on the access token returned from the user pool.
     */
    Credentials GetCredentials(String idprovider, String id) {
        AmazonCognitoIdentity provider = AmazonCognitoIdentityClientBuilder.defaultClient();
        GetIdRequest idrequest = new GetIdRequest();
        idrequest.setIdentityPoolId(FED_POOL_ID);
        idrequest.addLoginsEntry(idprovider, id);
        GetIdResult idResult = provider.getId(idrequest);

        GetCredentialsForIdentityRequest request = new GetCredentialsForIdentityRequest();
        request.setIdentityId(idResult.getIdentityId());
        request.addLoginsEntry(idprovider, id);

        GetCredentialsForIdentityResult result = provider.getCredentialsForIdentity(request);
        return result.getCredentials();
    }

    /**
     * Start reset password procedure by sending reset code
     *
     * @param username user to be reset
     * @return returns code delivery details
     */
    String ResetPassword(String username) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setUsername(username);
        forgotPasswordRequest.setClientId(CLIENTAPP_ID);
        ForgotPasswordResult forgotPasswordResult = new ForgotPasswordResult();
        try {
            forgotPasswordResult = cognitoIdentityProvider.forgotPassword(forgotPasswordRequest);
        } catch (Exception e) {
            // handle exception here
        }
        return forgotPasswordResult.toString();
    }

    /**
     * complete reset password procedure by confirming the reset code
     *
     * @param username user to be reset
     * @param newpw new password of aforementioned user
     * @param code code sent for password reset from the ResetPassword() method above
     * @return returns code delivery details
     */
    String UpdatePassword(String username, String newpw, String code) {
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ConfirmForgotPasswordRequest confirmPasswordRequest = new ConfirmForgotPasswordRequest();
        confirmPasswordRequest.setUsername(username);
        confirmPasswordRequest.setPassword(newpw);
        confirmPasswordRequest.setConfirmationCode(code);
        confirmPasswordRequest.setClientId(CLIENTAPP_ID);
        ConfirmForgotPasswordResult confirmPasswordResult = new ConfirmForgotPasswordResult();
        try {
            confirmPasswordResult = cognitoIdentityProvider.confirmForgotPassword(confirmPasswordRequest);
        } catch (Exception e) {
            // handle exception here
        }
        return confirmPasswordResult.toString();
    }
}
