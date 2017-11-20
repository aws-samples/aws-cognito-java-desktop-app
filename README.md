# aws-cognito-java-desktop-app

This is a sample application which provides a basic implementation of the use of cognito user
pools using the java SDK.

This application supports
1. Adding the user to the cognito user pool.
2. Confirming the user using their cell phone number
3. Performing the login using the newly created user.
4. Getting the AWS credential for the user and displaying the user resource based on the
AWS credentails. 

To test this application, update the following entries in the CognitoHelper.java

private String POOL_ID= ""; 

private String CLIENTAPP_ID="";

private String FED_POOL_ID="";
