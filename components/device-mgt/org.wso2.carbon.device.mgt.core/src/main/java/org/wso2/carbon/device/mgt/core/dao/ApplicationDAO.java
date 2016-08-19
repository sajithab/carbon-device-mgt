/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.device.mgt.core.dao;

import org.wso2.carbon.device.mgt.common.app.mgt.Application;

import java.util.List;

public interface ApplicationDAO {

    int addApplication(Application application, int tenantId) throws DeviceManagementDAOException;

    List<Integer> addApplications(List<Application> applications, int tenantId) throws DeviceManagementDAOException;

    List<Integer> removeApplications(List<Application> apps, int tenantId) throws DeviceManagementDAOException;

    Application getApplication(String identifier, int tenantId) throws DeviceManagementDAOException;

    Application getApplication(String identifier, String version,int tenantId) throws DeviceManagementDAOException;

    List<Application> getInstalledApplications(int deviceId) throws DeviceManagementDAOException;
}
