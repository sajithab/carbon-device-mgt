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

function onRequest(context) {
    var log = new Log("detail.js");
    var deviceType = context.uriParams.deviceType;
    var deviceId = request.getParameter("id");

    if (deviceType != null && deviceType != undefined && deviceId != null && deviceId != undefined) {
        var deviceModule = require("/app/modules/business-controllers/device.js")["deviceModule"];
        var device = deviceModule.viewDevice(deviceType, deviceId);

        if (device) {
            var viewModel = {};
            var deviceInfo = device.properties.DEVICE_INFO;
            if (deviceInfo != undefined && String(deviceInfo.toString()).length > 0) {
                deviceInfo = parse(stringify(deviceInfo));
                if (device.type == "ios") {
                    deviceInfo = parse(deviceInfo);
                    viewModel.imei = device.properties.IMEI;
                    viewModel.phoneNumber = deviceInfo.PhoneNumber;
                    viewModel.udid = deviceInfo.UDID;
                    viewModel.BatteryLevel = Math.round(deviceInfo.BatteryLevel * 100);
                    viewModel.DeviceCapacity = Math.round(deviceInfo.DeviceCapacity * 100) / 100;
                    viewModel.AvailableDeviceCapacity = Math.round(deviceInfo.AvailableDeviceCapacity * 100) / 100;
                    viewModel.DeviceCapacityUsed = Math.round((viewModel.DeviceCapacity
                                                               - viewModel.AvailableDeviceCapacity) * 100) / 100;
                    viewModel.DeviceCapacityPercentage = Math.round(viewModel.AvailableDeviceCapacity
                                                                    / viewModel.DeviceCapacity * 10000) / 100;
                    viewModel.location = {
                        latitude: device.properties.LATITUDE,
                        longitude: device.properties.LONGITUDE
                    };
                } else if (device.type == "android") {
                    viewModel.imei = device.properties.IMEI;
                    viewModel.model = device.properties.DEVICE_MODEL;
                    viewModel.vendor = device.properties.VENDOR;
                    viewModel.internal_memory = {};
                    viewModel.external_memory = {};
                    viewModel.location = {
                        latitude: device.properties.LATITUDE,
                        longitude: device.properties.LONGITUDE
                    };
                    var info = {};
                    var infoList = parse(deviceInfo);
                    if (infoList != null && infoList != undefined) {
                        for (var j = 0; j < infoList.length; j++) {
                            info[infoList[j].name] = infoList[j].value;
                        }
                    }
                    deviceInfo = info;
                    viewModel.BatteryLevel = deviceInfo.BATTERY_LEVEL;
                    viewModel.internal_memory.FreeCapacity = Math.round(deviceInfo.INTERNAL_AVAILABLE_MEMORY * 100)/100;
                    viewModel.internal_memory.DeviceCapacityPercentage = Math.round(deviceInfo.INTERNAL_AVAILABLE_MEMORY
                                                                                    / deviceInfo.INTERNAL_TOTAL_MEMORY * 10000) / 100;
                    viewModel.external_memory.FreeCapacity = Math.round(deviceInfo.EXTERNAL_AVAILABLE_MEMORY * 100) / 100;
                    viewModel.external_memory.DeviceCapacityPercentage = Math.round(deviceInfo.EXTERNAL_AVAILABLE_MEMORY
                                                                                    / deviceInfo.EXTERNAL_TOTAL_MEMORY * 10000) / 100;
                } else if (device.type == "windows") {
                    viewModel.imei = device.properties.IMEI;
                    viewModel.model = device.properties.DEVICE_MODEL;
                    viewModel.vendor = device.properties.VENDOR;
                    viewModel.internal_memory = {};
                    viewModel.external_memory = {};
                    viewModel.location = {
                        latitude: device.properties.LATITUDE,
                        longitude: device.properties.LONGITUDE
                    };
                    /*var info = {};
                     if (deviceInfo != null && deviceInfo != undefined){
                     var infoList = parse(deviceInfo);
                     if (infoList != null && infoList != undefined) {
                     for (var j = 0; j < infoList.length; j++) {
                     info[infoList[j].name] = infoList[j].value;
                     }
                     }
                     deviceInfo = info;
                     viewModel.BatteryLevel = deviceInfo.BATTERY_LEVEL;
                     viewModel.internal_memory.FreeCapacity = Math.round((deviceInfo.INTERNAL_TOTAL_MEMORY -
                     deviceInfo.INTERNAL_AVAILABLE_MEMORY) * 100) / 100;
                     viewModel.internal_memory.DeviceCapacityPercentage = Math.round(deviceInfo.INTERNAL_AVAILABLE_MEMORY
                     / deviceInfo.INTERNAL_TOTAL_MEMORY * 10000) / 100;
                     viewModel.external_memory.FreeCapacity = Math.round((deviceInfo.EXTERNAL_TOTAL_MEMORY -
                     deviceInfo.EXTERNAL_AVAILABLE_MEMORY) * 100) / 100;
                     viewModel.external_memory.DeviceCapacityPercentage = Math.round(deviceInfo.EXTERNAL_AVAILABLE_MEMORY
                     / deviceInfo.EXTERNAL_TOTAL_MEMORY * 10000) / 100;
                     }*/
                }else if (device.type == "TemperatureController") {
                    viewModel.system = device.properties.IMEI;
                    viewModel.machine = device.properties.DEVICE_MODEL;
                    viewModel.vendor = device.properties.VENDOR;
                    viewModel.internal_memory = {};
                    viewModel.external_memory = {};
                }
                device.viewModel = viewModel;
            }
        }

        log.info(device);
        var utility = require("/app/modules/utility.js").utility;
        var configs = utility.getDeviceTypeConfig(deviceType);
        return {"device": device, "label" : configs["deviceType"]["label"]};
    }
}