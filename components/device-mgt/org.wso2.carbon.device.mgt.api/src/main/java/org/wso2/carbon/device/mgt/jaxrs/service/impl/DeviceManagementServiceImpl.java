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
package org.wso2.carbon.device.mgt.jaxrs.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.search.SearchContext;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchManagerService;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchMgtException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.jaxrs.beans.DeviceCompliance;
import org.wso2.carbon.device.mgt.jaxrs.beans.DeviceList;
import org.wso2.carbon.device.mgt.jaxrs.beans.ErrorResponse;
import org.wso2.carbon.device.mgt.jaxrs.beans.OperationList;
import org.wso2.carbon.device.mgt.jaxrs.service.api.DeviceManagementService;
import org.wso2.carbon.device.mgt.jaxrs.service.impl.util.RequestValidationUtil;
import org.wso2.carbon.device.mgt.jaxrs.util.DeviceMgtAPIUtils;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceData;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceManagementServiceImpl implements DeviceManagementService {

    private static final Log log = LogFactory.getLog(DeviceManagementServiceImpl.class);

    @GET
    @Override
    public Response getDevices(
            @QueryParam("name") @Size(max = 45) String name,
            @QueryParam("type") @Size(max = 45) String type,
            @QueryParam("user") @Size(max = 45) String user,
            @QueryParam("ownership") @Size(max = 45) String ownership,
            @QueryParam("status") @Size(max = 45) String status,
            @QueryParam("since") String since,
            @HeaderParam("If-Modified-Since") String ifModifiedSince,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        try {
//            RequestValidationUtil.validateSelectionCriteria(type, user, roleName, ownership, status);
            RequestValidationUtil.validatePaginationParameters(offset, limit);
            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            PaginationRequest request = new PaginationRequest(offset, limit);
            PaginationResult result;
            DeviceList devices = new DeviceList();

            if (name != null && !name.isEmpty()) {
                request.setDeviceName(name);
            }
            if (type != null && !type.isEmpty()) {
                request.setDeviceType(type);
            }
            if (user != null && !user.isEmpty()) {
                request.setOwner(user);
            }
            if (ownership != null && !ownership.isEmpty()) {
                RequestValidationUtil.validateOwnershipType(ownership);
                request.setOwnership(ownership);
            }
            if (status != null && !status.isEmpty()) {
                RequestValidationUtil.validateStatus(status);
                request.setStatus(status);
            }

            if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
                Date sinceDate;
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                try {
                    sinceDate = format.parse(ifModifiedSince);
                } catch (ParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(
                            new ErrorResponse.ErrorResponseBuilder().setMessage("Invalid date " +
                                    "string is provided in 'If-Modified-Since' header").build()).build();
                }
                request.setSince(sinceDate);
                result = dms.getAllDevices(request);
                if (result == null || result.getData() == null || result.getData().size() <= 0) {
                    return Response.status(Response.Status.NOT_MODIFIED).entity("No device is modified " +
                            "after the timestamp provided in 'If-Modified-Since' header").build();
                }
            } else if (since != null && !since.isEmpty()) {
                Date sinceDate;
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                try {
                    sinceDate = format.parse(since);
                } catch (ParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(
                            new ErrorResponse.ErrorResponseBuilder().setMessage("Invalid date " +
                                    "string is provided in 'since' filter").build()).build();
                }
                request.setSince(sinceDate);
                result = dms.getAllDevices(request);
                if (result == null || result.getData() == null || result.getData().size() <= 0) {
                    return Response.status(Response.Status.OK).entity("No device is modified " +
                            "after the timestamp provided in 'since' filter").build();
                }
            } else {
                result = dms.getAllDevices(request);
                int resultCount = result.getRecordsTotal();
                if (resultCount == 0) {
                    Response.status(Response.Status.OK).entity(devices).build();
                }
            }

            devices.setList((List<Device>) result.getData());
            devices.setCount(result.getRecordsTotal());
            return Response.status(Response.Status.OK).entity(devices).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching all enrolled devices";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

    @GET
    @Path("/user-devices")
    public Response getDeviceByUser(@QueryParam("offset") int offset,
                                    @QueryParam("limit") int limit) {

        RequestValidationUtil.validatePaginationParameters(offset, limit);
        PaginationRequest request = new PaginationRequest(offset, limit);
        PaginationResult result;
        DeviceList devices = new DeviceList();

        String currentUser = CarbonContext.getThreadLocalCarbonContext().getUsername();
        request.setOwner(currentUser);

        try {
            result = DeviceMgtAPIUtils.getDeviceManagementService().getDevicesOfUser(request);
            devices.setList((List<Device>) result.getData());
            devices.setCount(result.getRecordsTotal());
            return Response.status(Response.Status.OK).entity(devices).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching all enrolled devices";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }


    @GET
    @Path("/{type}/{id}")
    @Override
    public Response getDevice(
            @PathParam("type") @Size(max = 45) String type,
            @PathParam("id") @Size(max = 45) String id,
            @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        Device device;
        try {
            RequestValidationUtil.validateDeviceIdentifier(type, id);

            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            device = dms.getDevice(new DeviceIdentifier(id, type));
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the device information.";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        if (device == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(
                    new ErrorResponse.ErrorResponseBuilder().setCode(404l).setMessage("Requested device of type '" +
                            type + "', which carries id '" + id + "' does not exist").build()).build();
        }
        return Response.status(Response.Status.OK).entity(device).build();
    }

    @GET
    @Path("/{type}/{id}/features")
    @Override
    public Response getFeaturesOfDevice(
            @PathParam("type") @Size(max = 45) String type,
            @PathParam("id") @Size(max = 45) String id,
            @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        List<Feature> features;
        DeviceManagementProviderService dms;
        try {
            RequestValidationUtil.validateDeviceIdentifier(type, id);

            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            FeatureManager fm = dms.getFeatureManager(type);
            if (fm == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage("No feature manager is " +
                                "registered with the given type '" + type + "'").build()).build();
            }
            features = fm.getFeatures();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of features of '" + type + "' device, which " +
                    "carries the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        return Response.status(Response.Status.OK).entity(features).build();
    }

    @POST
    @Path("/search-devices")
    @Override
    public Response searchDevices(@QueryParam("offset") int offset,
                                  @QueryParam("limit") int limit, SearchContext searchContext) {
        SearchManagerService searchManagerService;
        List<Device> devices;
        DeviceList deviceList = new DeviceList();
        try {
            searchManagerService = DeviceMgtAPIUtils.getSearchManagerService();
            devices = searchManagerService.search(searchContext);
        } catch (SearchMgtException e) {
            String msg = "Error occurred while searching for devices that matches the provided selection criteria";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        deviceList.setList(devices);
        return Response.status(Response.Status.OK).entity(deviceList).build();
    }

    @GET
    @Path("/{type}/{id}/applications")
    @Override
    public Response getInstalledApplications(
            @PathParam("type") @Size(max = 45) String type,
            @PathParam("id") @Size(max = 45) String id,
            @HeaderParam("If-Modified-Since") String ifModifiedSince,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        List<Application> applications;
        //ApplicationList appList;
        ApplicationManagementProviderService amc;
        try {
            RequestValidationUtil.validateDeviceIdentifier(type, id);

            amc = DeviceMgtAPIUtils.getAppManagementService();
            applications = amc.getApplicationListForDevice(new DeviceIdentifier(id, type));

            //TODO: return app list
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while fetching the apps of the '" + type + "' device, which carries " +
                    "the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        return Response.status(Response.Status.OK).entity(applications).build();
    }

    @GET
    @Path("/{type}/{id}/operations")
    @Override
    public Response getDeviceOperations(
            @PathParam("type") @Size(max = 45) String type,
            @PathParam("id") @Size(max = 45) String id,
            @HeaderParam("If-Modified-Since") String ifModifiedSince,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        OperationList operationsList = new OperationList();
        RequestValidationUtil.validatePaginationParameters(offset, limit);
        PaginationRequest request = new PaginationRequest(offset, limit);
        PaginationResult result;
        DeviceManagementProviderService dms;
        try {
            RequestValidationUtil.validateDeviceIdentifier(type, id);

            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            result = dms.getOperations(new DeviceIdentifier(id, type), request);

            operationsList.setList((List<? extends Operation>) result.getData());
            operationsList.setCount(result.getRecordsTotal());
            return Response.status(Response.Status.OK).entity(operationsList).build();
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the '" + type + "' device, which " +
                    "carries the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

    @GET
    @Path("/{type}/{id}/effective-policy")
    @Override
    public Response getEffectivePolicyOfDevice(@PathParam("type") @Size(max = 45) String type,
                                               @PathParam("id") @Size(max = 45) String id,
                                               @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        try {
            RequestValidationUtil.validateDeviceIdentifier(type, id);

            PolicyManagerService policyManagementService = DeviceMgtAPIUtils.getPolicyManagementService();
            Policy policy = policyManagementService.getAppliedPolicyToDevice(new DeviceIdentifier(id, type));

            return Response.status(Response.Status.OK).entity(policy).build();
        } catch (PolicyManagementException e) {
            String msg = "Error occurred while retrieving the current policy associated with the '" + type +
                    "' device, which carries the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

    @GET
    @Path("{type}/{id}/compliance-data")
    public Response getComplianceDataOfDevice(@PathParam("type") @Size(max = 45) String type,
                                              @PathParam("id") @Size(max = 45) String id) {

        RequestValidationUtil.validateDeviceIdentifier(type, id);
        PolicyManagerService policyManagementService = DeviceMgtAPIUtils.getPolicyManagementService();
        Policy policy;
        ComplianceData complianceData = null;
        DeviceCompliance deviceCompliance = new DeviceCompliance();

        try {
            policy = policyManagementService.getAppliedPolicyToDevice(new DeviceIdentifier(id, type));
        } catch (PolicyManagementException e) {
            String msg = "Error occurred while retrieving the current policy associated with the '" + type +
                    "' device, which carries the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }

        if (policy == null) {
            deviceCompliance.setDeviceID(id);
            deviceCompliance.setComplianceData(null);
            //deviceCompliance.setCode(0001l); //code 0001 means no compliance data related to the device
            return Response.status(Response.Status.OK).entity(deviceCompliance).build();
        } else {
            try {
                policyManagementService = DeviceMgtAPIUtils.getPolicyManagementService();
                complianceData = policyManagementService.getDeviceCompliance(
                        new DeviceIdentifier(id, type));
                deviceCompliance.setDeviceID(id);
                deviceCompliance.setComplianceData(complianceData);
                //deviceCompliance.setCode(0002l); //code 0002 means there are compliance data related to the device
                return Response.status(Response.Status.OK).entity(deviceCompliance).build();
            } catch (PolicyComplianceException e) {
                String error = "Error occurred while getting the compliance data.";
                log.error(error, e);
                return Response.serverError().entity(
                        new ErrorResponse.ErrorResponseBuilder().setMessage(error).build()).build();
            }
        }
    }

}
