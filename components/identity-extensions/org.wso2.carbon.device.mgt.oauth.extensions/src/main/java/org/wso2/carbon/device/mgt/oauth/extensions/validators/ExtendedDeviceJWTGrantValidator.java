/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License
 */

package org.wso2.carbon.device.mgt.oauth.extensions.validators;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;
import org.wso2.carbon.device.mgt.oauth.extensions.OAuthConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Grant validator for JSON Web Tokens
 * For JWT Grant to be valid the required parameters are
 * grant_type and assertion
 */
public class ExtendedDeviceJWTGrantValidator extends AbstractValidator<HttpServletRequest> {

    public ExtendedDeviceJWTGrantValidator() {
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        requiredParams.add(OAuth.OAUTH_ASSERTION);
    }
}