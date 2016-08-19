/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.jaxrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;

@ApiModel(value = "NotificationContext")
public class NotificationContext {

    private DeviceIdentifier deviceId;
    private Notification notification;

    public NotificationContext(DeviceIdentifier deviceId, Notification notification) {
        this.deviceId = deviceId;
        this.notification = notification;
    }

    @ApiModelProperty(value = "deviceId")
    @JsonProperty("deviceId")
    public DeviceIdentifier getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceIdentifier deviceId) {
        this.deviceId = deviceId;
    }

    @ApiModelProperty(value = "notification")
    @JsonProperty("notification")
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}
