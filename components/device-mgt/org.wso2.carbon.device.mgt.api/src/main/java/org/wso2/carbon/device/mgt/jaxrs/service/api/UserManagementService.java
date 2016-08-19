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
package org.wso2.carbon.device.mgt.jaxrs.service.api;

import io.swagger.annotations.*;
import org.wso2.carbon.apimgt.annotations.api.API;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.device.mgt.jaxrs.beans.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@API(name = "User Management", version = "1.0.0", context = "/api/device-mgt/v1.0/users", tags = {"devicemgt_admin"})

@Path("/users")
@Api(value = "User Management", description = "User management related operations can be found here.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserManagementService {

    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add a user.",
            notes = "A new user can be added to the user management system via this resource",
            tags = "User Management")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 201,
                            message = "Created. \n User has successfully been created",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The URL of the role added."),
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource has been modified the last time.\n" +
                                                    "Used by caches, or in conditional requests.")}),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict. \n User already exists.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The entity of the request was in a not " +
                                    "supported format.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while adding a new user.",
                            response = ErrorResponse.class)
            })
    @Scope(key = "user:manage", name = "Add users", description = "")
    Response addUser(
            @ApiParam(
                    name = "user",
                    value = "Information of the user to be added",
                    required = true) UserInfo user);

    @GET
    @Path("/{username}")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting details of a user.",
            notes = "If you wish to get the details of a specific user that is registered with EMM,"
                    + " you can do so using the REST API.",
            response = BasicUserInfo.class,
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Successfully fetched the requested role.",
                    response = BasicUserInfo.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource has been modified the last time.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 304,
                    message = "Not Modified. \n Empty body because the client has already the latest version of the requested resource."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable.\n The requested media type is not supported",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server ErrorResponse. \n Server error occurred while" +
                            " fetching the requested user.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:view", name = "View users", description = "")
    Response getUser(
            @ApiParam(
                    name = "username",
                    value = "Username of the user to be fetched.",
                    required = true)
            @PathParam("username") String username,
            @ApiParam(
                    name = "If-Modified-Since",
                    value = "Validates if the requested variant has not been modified since the time specified",
                    required = false)
            @HeaderParam("If-Modified-Since") String ifModifiedSince);

    @PUT
    @Path("/{username}")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update details of a user",
            notes = "There will be situations where you will want to update the user details. In such "
                    + "situation you can update the user details using this REST API.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n User has been updated successfully",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "Content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource has been modified the last time.\n" +
                                            "Used by caches, or in conditional requests.")}),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The entity of the request was in a not supported format.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while updating the user.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:manage", name = "Add users", description = "")
    Response updateUser(
            @ApiParam(
                    name = "username",
                    value = "Username of the user to be updated.",
                    required = true)
            @PathParam("username") String username,
            @ApiParam(
                    name = "userData",
                    value = "User related details.",
                    required = true) UserInfo userData);

    @DELETE
    @Path("/{username}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Deleting a user.",
            notes = "In a situation where an employee leaves the organization you will need to remove the"
                    + " user details from EMM. In such situations you can use this REST API to remove a user.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n User has successfully been removed"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource to be deleted does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while removing the user.",
                    response = ErrorResponse.class
            )
    })
    @Scope(key = "user:manage", name = "Add users", description = "")
    Response removeUser(
            @ApiParam(name = "username", value = "Username of the user to be deleted.", required = true)
            @PathParam("username") String username);

    @GET
    @Path("/{username}/roles")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get the role list of a user.",
            notes = "A user can be assigned to one or more role in EMM. Using this REST API you are "
                    + "able to get the role/roles a user is assigned to.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Successfully fetched the role list assigned to the user.",
                    response = RoleList.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource has been modified the last time.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 304,
                    message = "Not Modified. \n Empty body because the client has already the latest version of the requested resource."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource to be deleted does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable.\n The requested media type is not supported",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Server error occurred while fetching the role list" +
                            " assigned to the user.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:view", name = "View users", description = "")
    Response getRolesOfUser(
            @ApiParam(name = "username", value = "Username of the user.", required = true)
            @PathParam("username") String username);

    @GET
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get user list",
            notes = "If you wish to get the details of all the users registered with EMM, you can do so "
                    + "using the REST API",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Successfully fetched the requested role.",
                    response = BasicUserInfoList.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource has been modified the last time.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 304,
                    message = "Not Modified. \n Empty body because the client already has the latest version of the requested resource."),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable.\n The requested media type is not supported",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Server error occurred while fetching the user list.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:view", name = "View users", description = "")
    Response getUsers(
            @ApiParam(
                    name = "filter",
                    value = "Username of the user details to be fetched.",
                    required = false)
            @QueryParam("filter") String filter,
            @ApiParam(
                    name = "If-Modified-Since",
                    value = "Timestamp of the last modified date",
                    required = false)
            @HeaderParam("If-Modified-Since") String timestamp,
            @ApiParam(
                    name = "offset",
                    value = "Starting point within the complete list of items qualified.",
                    required = false)
            @QueryParam("offset") int offset,
            @ApiParam(
                    name = "limit",
                    value = "Maximum size of resource array to return.",
                    required = false)
            @QueryParam("limit") int limit);

    @GET
    @Path("/search/usernames")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Search for a username.",
            notes = "If you are unsure of the "
                    + "user name of a user and need to retrieve the details of a specific user, you can "
                    + "search for that user by giving a character or a few characters in the username. "
                    + "You will be given a list of users having the user name with the exact order of the "
                    + "characters you provided.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Successfully fetched the username list that matches the given filter.",
                    response = String.class,
                    responseContainer = "List",
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "ETag",
                                    description = "Entity Tag of the response resource.\n" +
                                            "Used by caches, or in conditional requests."),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource has been modified the last time.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 304,
                    message = "Not Modified. \n Empty body because the client has already the latest version of the requested resource."),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable.\n The requested media type is not supported",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Server error occurred while fetching the username " +
                            "list that matches the given filter.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:view", name = "View users", description = "")
    Response getUserNames(
            @ApiParam(
                    name = "filter",
                    value = "Username/part of the user name to search.",
                    required = true)
            @QueryParam("filter") String filter,
            @ApiParam(
                    name = "If-Modified-Since",
                    value = "Timestamp of the last modified date",
                    required = false)
            @HeaderParam("If-Modified-Since") String timestamp,
            @ApiParam(
                    name = "offset",
                    value = "Starting point within the complete list of items qualified.",
                    required = false)
            @QueryParam("offset") int offset,
            @ApiParam(
                    name = "limit",
                    value = "Maximum size of resource array to return.",
                    required = false)
            @QueryParam("limit") int limit);

    @PUT
    @Path("/{username}/credentials")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Changing the user password.",
            notes = "A user is able to change the password to secure their EMM profile via this REST API.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Credentials of the user have been updated successfully"),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource to be deleted does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The entity of the request was in a not supported format.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while updating credentials of the user.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:view", name = "View users", description = "")
    Response resetPassword(
            @ApiParam(
                    name = "username",
                    value = "Username of the user.",
                    required = true)
            @PathParam("username") String username,
            @ApiParam(
                    name = "credentials",
                    value = "Credential.",
                    required = true) OldPasswordResetWrapper credentials);

    @POST
    @Path("/send-invitation")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Send invitation mail.",
            notes = "A user is able to send invitation mail via this REST API.",
            tags = "User Management")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. \n Invitation mails have been sent."),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n Resource to be deleted does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The entity of the request was in a not supported format.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while updating credentials of the user.",
                    response = ErrorResponse.class)
    })
    @Scope(key = "user:manage", name = "Add users", description = "")
    Response inviteExistingUsersToEnrollDevice(
            @ApiParam(
                    name = "users",
                    value = "List of users",
                    required = true) List<String> usernames);

}
