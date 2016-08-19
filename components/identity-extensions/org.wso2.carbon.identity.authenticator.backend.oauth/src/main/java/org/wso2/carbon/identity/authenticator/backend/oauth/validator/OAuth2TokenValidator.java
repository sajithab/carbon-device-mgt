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
package org.wso2.carbon.identity.authenticator.backend.oauth.validator;

import java.rmi.RemoteException;

/**
 * Declares the contract for OAuth2TokenValidator implementations.
 */
public interface OAuth2TokenValidator {

    /**
     * This method gets a string accessToken and validates it and generate the OAuth2ClientApplicationDTO
     * containing the validity and user details if valid.
     *
     * @param accessToken which need to be validated.
     * @return OAuthValidationResponse with the validated results.
     */
    OAuthValidationResponse validateToken(String accessToken) throws RemoteException;
}
