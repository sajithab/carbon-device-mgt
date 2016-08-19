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
package org.wso2.carbon.device.mgt.analytics.data.publisher.service;

import org.wso2.carbon.device.mgt.analytics.data.publisher.exception.DataPublisherConfigurationException;

/**
 * This service can be used to publish and retreive data from the Analytics Server.
 */
public interface EventsPublisherService {

    /**
     * This is used to publish an event to DAS.
     * @param streamName is the name of the stream that the data needs to pushed
     * @param version is the version of the stream
     * @param metaDataArray - meta data that needs to pushed
     * @param correlationDataArray - correlation data that needs to be pushed
     * @param payloadDataArray - payload data that needs to be pushed
     * @return
     * @throws DataPublisherConfigurationException
     */
    boolean publishEvent(String streamName, String version, Object[] metaDataArray, Object[] correlationDataArray,
                         Object[] payloadDataArray) throws DataPublisherConfigurationException;

}
