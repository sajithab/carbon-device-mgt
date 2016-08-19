/*
*  Copyright (c) 2016 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.device.mgt.common.scope.mgt;

import java.util.List;
import org.wso2.carbon.apimgt.api.model.Scope;

/**
 * This interface contains the basic operations related to scope management.
 */
public interface ScopeManagementService {

    /**
     * This method is used to update the given list of scopes.
     *
     * @param scopes List of scopes to be updated.
     * @throws ScopeManagementException
     */
    void updateScopes(List<Scope> scopes) throws ScopeManagementException;

    /**
     * This method is used to update the given list of scopes keys with the role name.
     *
     * @param scopeKeys List of scopes to be updated.
     * @param roleName Role name
     * @throws ScopeManagementException
     */
    void updateScopes(List<String> scopeKeys, String roleName) throws ScopeManagementException;

    /**
     * This method is used to retrieve all the scopes.
     *
     * @return List of scopes.
     * @throws ScopeManagementException
     */
    List<Scope> getAllScopes() throws ScopeManagementException;

    /**
     * This method is to retrieve the roles of the given scope
     * @param scopeKey key of the scope
     * @return List of roles
     * @throws ScopeManagementException
     */
    String getRolesOfScope(String scopeKey) throws ScopeManagementException;

    /**
     * This method is to retrieve the scopes of the given role
     * @param roleName key of the scope
     * @return List of scopes
     * @throws ScopeManagementException
     */
    List<Scope> getScopesOfRole(String roleName) throws ScopeManagementException;

    /**
     * This method is used to remove the scopes of a given user role.
     *
     * @param roleName Role name
     * @throws ScopeManagementException
     */
    void removeScopes(String roleName) throws ScopeManagementException;

}
