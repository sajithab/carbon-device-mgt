/*
*  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.identity.authenticator.backend.oauth;

public class OauthAuthenticatorConstants {
    public static final String AUTHORIZATION_HEADER_PREFIX_BEARER = "Bearer";
    public static final String AUTHORIZATION_HEADER_PREFIX_BASIC = "Basic";
    public static final String BEARER_TOKEN_TYPE = "bearer";
    public static final String BEARER_TOKEN_IDENTIFIER = "token";
    public static final String AUTHENTICATOR_NAME = "OAuthAuthenticator";
    public static final String SPLITING_CHARACTOR = " ";
    public static final String OAUTH_ENDPOINT_POSTFIX =
            "/services/OAuth2TokenValidationService.OAuth2TokenValidationServiceHttpsSoap12Endpoint/";
}
