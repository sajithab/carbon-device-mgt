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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

function onRequest() {
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];
    var userModule = require("/app/modules/business-controllers/user.js")["userModule"];

    var userName = request.getParameter("username");
    var user = userModule.getUser(userName)["content"];
    
    if (user) {
        var title;
        if (user.firstname && user.lastname) {
            title = user.firstname + " " + user.lastname;
        } else {
            title = user.username;
        }
        var page = {"user": user, "title": title};

        var userStore = "PRIMARY";
        if (userName.indexOf("/") > -1) {
            userStore = userName.substr(0, userName.indexOf('/'));
        }
        page["userStore"] = userStore;

        var response = userModule.getUser(userName);

        if (response["status"] == "success") {
            page["editUser"] = response["content"];
        }

        response = userModule.getRolesByUsername(userName);
        var rolesByUsername;
        if (response["status"] == "success") {
            rolesByUsername = response["content"];
        }

        response = userModule.getRolesByUserStore(userStore);
        var rolesByUserStore;
        if (response["status"] == "success") {
            rolesByUserStore = response["content"];
        }

        page["rolesByUsername"] = rolesByUsername;
        page["rolesByUserStore"] = rolesByUserStore;
    }

    page["usernameJSRegEx"] = devicemgtProps["userValidationConfig"]["usernameJSRegEx"];
    page["usernameRegExViolationErrorMsg"] = devicemgtProps["userValidationConfig"]["usernameRegExViolationErrorMsg"];
    page["firstnameJSRegEx"] = devicemgtProps["userValidationConfig"]["firstnameJSRegEx"];
    page["firstnameRegExViolationErrorMsg"] = devicemgtProps["userValidationConfig"]["firstnameRegExViolationErrorMsg"];
    page["lastnameJSRegEx"] = devicemgtProps["userValidationConfig"]["lastnameJSRegEx"];
    page["lastnameRegExViolationErrorMsg"] = devicemgtProps["userValidationConfig"]["lastnameRegExViolationErrorMsg"];

    return page;
}