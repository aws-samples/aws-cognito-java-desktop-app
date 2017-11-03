# Lab Guide: Implementing Cognito Authentication in Java
In this lab guide, we aim to build a basic console application that allows the user to do the following:
1. Create a new user (akin to an Active Director or LDAP user)
2. Get access and secret key credentials associated with that user to access AWS resources
3. Reset the user's password


## Step 1: Basic Setup and Initialization
In this first lab, we will do some basic setup before we getting into some of the Java code. We will need to setup the following:
1. IAM roles that will be used by Cognito
2. Setup a user pool
3. Setup an application client in that user pool that will be used by our application
4. Setup a federated identities pool that will link users in the user pool to an IAM role


### Create S3 bucket
The goal of this application will be to list the objects in an S3 bucket that you create. Let's start by creating that bucket and uploading a few files to that bucket.

1. In the AWS Console, hover over the Services drop-down and select S3.
2. Click the "Create bucket" button.
3. Enter the name of your bucket. Note that it will need to be a regionally unique bucket name.
4. Leave all other parameters default and create the bucket.
5. Back at the S3 main dashboard where the full list of S3 buckets is visible, click on the row of your bucket name (not the link). This should bring up a pop-up on the right side of the browser that has a "Copy Bucket ARN" button.
6. Click that button and save the ARN in a side text document, e.g. "arn:aws:s3:::reinvent-cognito-workshop-1"


### Create IAM Policies
Next we will pre-create IAM policies that we will use with our Cognito user pools. Cognito allows you to have both authenticated and unauthenticated users. We will create an IAM role for each of those identity types.

1. In the AWS Console, hover over the Services drop-down and select IAM.
2. On the left navigation bar, click the "Policies" link.
3. We are going to first create two policies. The first will be for the authenticated policy. Click the "Create policy" button.
    a. Click the "Select" button for "Create Your Own Policy"
    b. Enter a policy name, e.g. "policy-reinvent-cognito-authenticated"
    c. For the policy document, you can use the template below.
    d. Be sure to update "[S3_BUCKET_NAME]" with your bucket name. It occurs twice in the document.
```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "mobileanalytics:PutEvents",
        "cognito-sync:*",
        "cognito-identity:*"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListAllMyBuckets"
      ],
      "Resource": [
        "arn:aws:s3:::*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::[S3_BUCKET_NAME]"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::[S3_BUCKET_NAME]/*"
      ]
    }
  ]
}
```
4. We are going to repeat the process now for the unauthenticated policy. Click the "Create policy" button.
    a. Click the "Select" button for "Create Your Own Policy"
    b. Enter a policy name, e.g. "policy-reinvent-cognito-unauthenticated"
    c. For the policy document, you can use the template below.
    d. Be sure to update "[S3_BUCKET_NAME]" with your bucket name. It occurs twice in the document.
```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "mobileanalytics:PutEvents",
        "cognito-sync:*"
      ],
      "Resource": [
        "*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListAllMyBuckets"
      ],
      "Resource": [
        "arn:aws:s3:::*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::[S3_BUCKET_NAME]"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject"
      ],
      "Resource": [
        "arn:aws:s3:::[S3_BUCKET_NAME]/*"
      ]
    }
  ]
}
```


### Create user pool
1. In the AWS Console, hover over the Services drop-down and select Cognito.
2. We are going to start with User Pools, so click "Manage your User Pools".
3. In the top right, click "Create a user pool".
4. Enter a user pool name, e.g. "reinvent-user-pool-1"
5. Click the "Review defaults" button.
6. Click the "Create pool" button.
7. Make note of the Pool Id, e.g. "us-east-1_JuJjoK4XS".


### Create application client
1. Scroll down to "App clients" and click the "Add app client..." link. Note that We could have done this before but to keep process simple, we deferred that until now.
2. Click the "Add an app client" link.
3. Enter an app client name, e.g. "reinvent-app-client-1".
4. Uncheck the "Generate client secret" checkbox. To simplify implementation later, we are disabling this feature. For production uses, this feature should be implemented.
5. Click the "Create app client" button.
6. Make note of the App client id, e.g. "7a9v27fkto1b7l572i1369l89v".


### Create federated identities pool
1. In the top left of your browser, you should see "User Pools | Federated Identities". Click the grayed out "Federated Identities" link.
2. Click the "Create new identity pool" button.
3. Enter a federated identities pool name, e.g. "reinvent_fed_ids_pool_1". Note that dashes are not supported, so we used underscore instead.
4. Note that this lab will not cover unauthenticated identities so we can leave the "Enable access to unauthenticated identities" checkbox unchecked.
5. Under Authentication Providers, we are going to choose the Cognito tab.
6. Enter the Pool Id that we took note of above in the User Pool section, e.g. "us-east-1_JuJjoK4XS".
7. Enter the App client id that we took note of above in the Application Client section, e.g. "7a9v27fkto1b7l572i1369l89v".
8. Click the "Create Pool" button.
9. Cognito will inform you that Cognito identities need access to your resources via IAM roles. Click the "View Details" link to view the role details.
10. Let's create new roles for both authenticated and unauthenticated identities. Enter names for each one, e.g. "role-reinvent-cognito-authenticated" and "role-reinvent-cognito-unauthenticated"
    * Note that we will leave the policies as Cognito defaults for now but will replace those policies with the ones we made above.
11. Click the "Allow" button.
12. Make note of the Federated Identities pool id, which can be found when you click on the pool, then click on "Edit identity pool", e.g. "us-east-1:0488063e-b4c6-4b1f-86aa-79dd82a88dac".


### Update Cognito roles with previously created policies
1. In the AWS Console, hover over the Services drop-down and select IAM.
2. On the left navigation bar, click the "Roles" link.
3. Search for the "role-reinvent-cognito-authenticated". Remove the oneclick_* policy and add the policy that we created previously: "policy-reinvent-cognito-authenticated".
4. Search for the "role-reinvent-cognito-unauthenticated". Remove the oneclick_* policy and add the policy that we created previously: "policy-reinvent-cognito-unauthenticated".


## Step 2: Setting up CognitoHelper class
We now have all of the pre-requisite AWS components setup and are ready to build the code! The bulk of the work that we will be doing will be in the CognitoHelper.java file. For the purposes of this lab, the class will be initialized with barebones class, and we will add the code snippets for each piece of functionality.

TODO: We need to update the code to read these parameters from a properties file.
First we need to setup some identifier Strings to allow our code to connect with the AWS components that we configured in Step 1. Using the values that we noted above:
```
private String POOL_ID= "us-east-1_JuJjoK4XS";
private String CLIENTAPP_ID="7a9v27fkto1b7l572i1369l89v";
private String FED_POOL_ID="us-east-1:0488063e-b4c6-4b1f-86aa-79dd82a88dac";
```

Note that in the steps that follow, we will iterate through the following process for each method:
1. Create a client to talk to either the User Pool or Federated Identities Pool
2. Create a request that will be sent to that service
3. Submit the request to the service using that client


## Step 3: Creating New Users
Creating the user is a two-step process:
1. Initial user sign-up request
2. A code is sent to the user via SMS and that code needs to be validated to confirm the user sign-up process

Next we are going to create a method to create new users. The interface for that method is as follows:
```
    boolean SignUpUser(String username, String password, String email, String phonenumber) { }
```

The first thing that we will do is setup the Cognito Identity Provider client that allows us to make calls into the User Pool service.
```
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
```

Next we will will create a user sign up request and populate it with fields from our user interface.
```
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
```

And finally we will submit the sign-up request to the Cognito Identity Provider client and get the result. Assuming all went well, no exception will be thrown and the method will return true.
```
        try {
            SignUpResult result = cognitoIdentityProvider.signUp(signUpRequest);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
```

TODO: I do not see the VerifyAccessCode() method being called in the MainConsole java application, so I am unsure if he is performing this step in the UI. Possible that this will just be handled by the hosted UI instead?
The sign up process now requires that we validate the user. This will be done by sending a validation code to the user's phone number via SMS message. We now need to write the method that will check the validation code to insure that we have a good user sign-up request. The interface for that method is as follows:
```
    boolean VerifyAccessCode(String username, String code) { }
```

First we will create the Cognito Identity Provider client again.
```
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
```

And similar to the process above, we will create a confirm sign-up request, using the user name, validation code, and the application client id.
```
        ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
        confirmSignUpRequest.setUsername(username);
        confirmSignUpRequest.setConfirmationCode(code);
        confirmSignUpRequest.setClientId(CLIENTAPP_ID);
```

Finally we will submit the confirm sign-up request to the Cognito Identity Provider client and get the result.  Assuming all went well, no exception will be thrown and the method will return true.
```
        try {
            ConfirmSignUpResult confirmSignUpResult = cognitoIdentityProvider.confirmSignUp(confirmSignUpRequest);
            System.out.println(confirmSignUpResult.toString());
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
```


## Step 4: Using Credentials
Next we are going to create a function to get AWS credentials associated with the user that we just created. The interface for that method is as follows:
```
    Credentials GetCredentials(String idprovider, String id) { }
```

First we will create the Cognito Identity client. Note that this is the Cognito Identity client that will now be talking to the Federated Identities service.
```
        AmazonCognitoIdentity provider = AmazonCognitoIdentityClientBuilder.defaultClient();
```

Next we create the request to get credentials from the federated identities pool using the user identity from the user pool.
```
        GetIdRequest idrequest = new GetIdRequest();
        idrequest.setIdentityPoolId(FED_POOL_ID);
        idrequest.addLoginsEntry(idprovider, id);
        GetIdResult idResult = provider.getId(idrequest);

        GetCredentialsForIdentityRequest request = new GetCredentialsForIdentityRequest();
        request.setIdentityId(idResult.getIdentityId());
        request.addLoginsEntry(idprovider, id);
```

Finally we will submit the credentials request to the Cognito Identity client and get the result.  Assuming all went well, we will get the credentials and the method will return the credentials.
```
        GetCredentialsForIdentityResult result = provider.getCredentialsForIdentity(request);
        return result.getCredentials();
    }
```
And finally, we are going to create a helper function to use SRP authentication.
```
    String ValidateUser(String username, String password) {
        AuthenticationHelper helper = new AuthenticationHelper(POOL_ID, CLIENTAPP_ID, "");
        return helper.PerformSRPAuthentication(username, password);
    }
```


## Step 5: Resetting Passwords
Next we implement the capability to reset a user's password. This is a two step process:
1. Initiate the forget password process, which will send an SMS message with a code to the user
2. Use that code to then set a new password


First we implemented the method to initiate the reset password process. The interface for that method is as follows:
```
    String ResetPassword(String username) { }
```

First we will create the Cognito Identity Provider client. This client will be talking to the user pools service.
```
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
```

Next we create a forgot password request and populate with the username and the application client id.
```
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setUsername(username);
        forgotPasswordRequest.setClientId(CLIENTAPP_ID);
        ForgotPasswordResult forgotPasswordResult = new ForgotPasswordResult();
```

And finally we submit the request.
```
        try {
            forgotPasswordResult = cognitoIdentityProvider.forgotPassword(forgotPasswordRequest);
        } catch (Exception e) {
            // handle exception here
        }
        return forgotPasswordResult.toString();
```

Next we take the code that was sent via SMS and update the user's password. The interface for that method is as follows:
```
    String UpdatePassword(String username, String newpw, String code) { }
```

First we will create the Cognito Identity Provider client again. This client will be talking to the user pools service.
```
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.defaultClient();
```

Next we create the request to update the password.
```
        ConfirmForgotPasswordRequest confirmPasswordRequest = new ConfirmForgotPasswordRequest();
        confirmPasswordRequest.setUsername(username);
        confirmPasswordRequest.setPassword(newpw);
        confirmPasswordRequest.setConfirmationCode(code);
        confirmPasswordRequest.setClientId(CLIENTAPP_ID);
        ConfirmForgotPasswordResult confirmPasswordResult = new ConfirmForgotPasswordResult();
```

And finally we submit the request.
```
        try {
            confirmPasswordResult = cognitoIdentityProvider.confirmForgotPassword(confirmPasswordRequest);
        } catch (Exception e) {
            // handle exception here
        }
        return confirmPasswordResult.toString();
    }
```

