/*
 * Copyright 2013-2017 Amazon.com, Inc. or its affiliates.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amazonaws.sample.cognitoui;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Local http client.
 */

final class AuthHttpClient {

    String httpPost(final URL uri, final Map<String, String> bodyParams) throws Exception {
        if (uri == null || bodyParams == null || bodyParams.size() < 1) {
            throw new Exception("Invalid http request parameters");
        }

        final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) uri.openConnection();
        DataOutputStream httpOutputStream = null;
        BufferedReader br = null;
        try {
            // Request header
            httpsURLConnection.setRequestMethod(Constants.HTTP_REQUEST_TYPE_POST);
            httpsURLConnection.setDoOutput(true);
            Map<String, String> headerParams = getHttpHeader();
            for (Map.Entry<String, String> param : headerParams.entrySet()) {
                httpsURLConnection.addRequestProperty(param.getKey(), param.getValue());
            }

            // Request body
            StringBuilder reqBuilder = new StringBuilder();
            int index = bodyParams.size();
            for (Map.Entry<String, String> param : bodyParams.entrySet()) {
                reqBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(param.getValue(), "UTF-8"));
                if (index-- > 1) {
                    reqBuilder.append('&');
                }
            }
            String requestBody = reqBuilder.toString();

            httpOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());

            httpOutputStream.writeBytes(requestBody);
            httpOutputStream.flush();

            // Parse response
            Map<String, List<String>> respHeaders = httpsURLConnection.getHeaderFields();

            int responseCode = httpsURLConnection.getResponseCode();

            if (responseCode >= HttpURLConnection.HTTP_OK &&
                    responseCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {

                // Return response from the http call
                InputStream httpDataStream;
                if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                    httpDataStream = httpsURLConnection.getInputStream();
                } else {
                    httpDataStream = httpsURLConnection.getErrorStream();
                }
                br = new BufferedReader(new InputStreamReader(httpDataStream));

                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }

                String jsonresult = responseOutput.toString();
                return new JSONObject(jsonresult).get("id_token").toString();

            } else {
                // Throw a Exception
                throw new Exception(httpsURLConnection.getResponseMessage());
            }

        } catch (final Exception e) {
            throw e;
        } finally {
            if (httpOutputStream != null) {
                httpOutputStream.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * Generates header for the http request.
     *
     * @return Header parameters as a {@link Map<String, String>}.
     */
    private Map<String, String> getHttpHeader() {
        Map<String, String> httpHeaderParams = new HashMap<String, String>();
        httpHeaderParams.put(Constants.HTTP_HEADER_PROP_CONTENT_TYPE,
                Constants.HTTP_HEADER_PROP_CONTENT_TYPE_DEFAULT);
        return httpHeaderParams;
    }


}