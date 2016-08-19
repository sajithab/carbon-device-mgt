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

/**
 * Following function would execute
 * when a user clicks on the list item
 * initial mode and with out select mode.
 */
function InitiateViewOption(url) {
    if ($(".select-enable-btn").text() == "Select") {
        $(location).attr('href', url);
    }
}

(function () {
    var cache = {};
    var permissionSet = {};
    var validateAndReturn = function (value) {
        return (value == undefined || value == null) ? "Unspecified" : value;
    };
    Handlebars.registerHelper("deviceMap", function (device) {
        device.owner = validateAndReturn(device.owner);
        device.ownership = validateAndReturn(device.ownership);
        var arr = device.properties;
        if (arr) {
            device.properties = arr.reduce(function (total, current) {
                total[current.name] = validateAndReturn(current.value);
                return total;
            }, {});
        }
    });

    //This method is used to setup permission for device listing
    $.setPermission = function (permission) {
        permissionSet[permission] = true;
    };

    $.hasPermission = function (permission) {
        return permissionSet[permission];
    };
})();

/*
 * Setting-up global variables.
 */
var deviceCheckbox = "#ast-container .ctrl-wr-asset .itm-select input[type='checkbox']";
var assetContainer = "#ast-container";

var deviceListing, currentUser, groupName, groupOwner;

/*
 * DOM ready functions.
 */
$(document).ready(function () {
    deviceListing = $("#device-listing");
    currentUser = deviceListing.data("current-user");

    groupName = getParameterByName("groupName");
    groupOwner = getParameterByName("groupOwner");

    /* Adding selected class for selected devices */
    $(deviceCheckbox).each(function () {
        addDeviceSelectedClass(this);
    });

    var permissionList = $("#permission").data("permission");
    for (var key in permissionList) {
        if (permissionList.hasOwnProperty(key)) {
            $.setPermission(key);
        }
    }

    /* for device list sorting drop down */
    $(".ctrl-filter-type-switcher").popover({
        html : true,
        content : function () {
            return $("#content-filter-types").html();
        }
    });

    $(".ast-container").on("click", ".claim-btn", function(e){
        e.stopPropagation();
        var deviceId = $(this).data("deviceid");
        var serviceURL = "/temp-controller-agent/enrollment/claim?username=" + currentUser;
        var deviceIdentifier = {id: deviceId, type: "TemperatureController"};
        invokerUtil.put(serviceURL, deviceIdentifier, function(message){
            console.log(message);
        }, function(message){
                console.log(message.content);
        });
    });
});

/*
 * On Select All Device button click function.
 *
 * @param button: Select All Device button
 */
function selectAllDevices(button) {
    if (!$(button).data('select')) {
        $(deviceCheckbox).each(function (index) {
            $(this).prop('checked', true);
            addDeviceSelectedClass(this);
        });
        $(button).data('select', true);
        $(button).html('Deselect All Devices');
    } else {
        $(deviceCheckbox).each(function (index) {
            $(this).prop('checked', false);
            addDeviceSelectedClass(this);
        });
        $(button).data('select', false);
        $(button).html('Select All Devices');
    }
}

/*
 * On listing layout toggle buttons click function.
 *
 * @param view: Selected view type
 * @param selection: Selection button
 */
function changeDeviceView(view, selection) {
    $(".view-toggle").each(function () {
        $(this).removeClass("selected");
    });
    $(selection).addClass("selected");
    if (view == "list") {
        $(assetContainer).addClass("list-view");
    } else {
        $(assetContainer).removeClass("list-view");
    }
}

/*
 * Add selected style class to the parent element function.
 *
 * @param checkbox: Selected checkbox
 */
function addDeviceSelectedClass(checkbox) {
    if ($(checkbox).is(":checked")) {
        $(checkbox).closest(".ctrl-wr-asset").addClass("selected device-select");
    } else {
        $(checkbox).closest(".ctrl-wr-asset").removeClass("selected device-select");
    }
}

function toTitleCase(str) {
    return str.replace(/\w\S*/g, function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
}

function loadDevices(searchType, searchParam){
    var serviceURL;
    if (groupName && groupOwner && $.hasPermission("LIST_OWN_DEVICES")) {
        serviceURL = "/devicemgt_admin/groups/owner/" + groupOwner + "/name/" + groupName + "/devices";
    } else if ($.hasPermission("LIST_DEVICES")) {
        serviceURL = "/devicemgt_admin/devices";
    } else if ($.hasPermission("LIST_OWN_DEVICES")) {
        //Get authenticated users devices
        serviceURL = "/devicemgt_admin/users/devices?username=" + currentUser;
    } else {
        $("#loading-content").remove();
        $('#device-table').addClass('hidden');
        $('#device-listing-status-msg').text('Permission denied.');
        $("#device-listing-status").removeClass(' hidden');
        return;
    }

    function getPropertyValue(deviceProperties, propertyName) {
        if (!deviceProperties) {
            return;
        }
        var property;
        for (var i = 0; i < deviceProperties.length; i++) {
            property = deviceProperties[i];
            if (property.name == propertyName) {
                return property.value;
            }
        }
        return {};
    }

    function getDeviceTypeLabel(type){
        var deviceTypes = deviceListing.data("deviceTypes");
        for (var i = 0; i < deviceTypes.length; i++){
            if (deviceTypes[i].type == type){
                return deviceTypes[i].label;
            }
        }
        return type;
    }

    function getDeviceTypeCategory(type) {
        var deviceTypes = deviceListing.data("deviceTypes");
        for (var i = 0; i < deviceTypes.length; i++) {
            if (deviceTypes[i].type == type) {
                return deviceTypes[i].category;
            }
        }
        return type;
    }

    function getDeviceTypeThumb(type) {
        var deviceTypes = deviceListing.data("deviceTypes");
        for (var i = 0; i < deviceTypes.length; i++) {
            if (deviceTypes[i].type == type) {
                return deviceTypes[i].thumb;
            }
        }
        return type;
    }

    $('#device-grid').datatables_extended ({
        serverSide: true,
        processing: false,
        searching: true,
        ordering:  false,
        filter: false,
        pageLength : 16,
        ajax: { url : '/devicemgt/api/devices', data : {url : serviceURL},
                dataSrc: function ( json ) {
                    $('#device-grid').removeClass('hidden');
                    $("#loading-content").remove();
                    var $list = $("#device-table :input[type='search']");
                    $list.each(function(){
                        $(this).addClass("hidden");
                    });
                    return json.data;
                }
        },
        columnDefs: [
            { targets: 0, data: 'name', className: 'remove-padding icon-only content-fill' , render: function ( data, type, row, meta ) {
                return '<div class="thumbnail icon"><img class="square-element text fw " src="' + getDeviceTypeThumb(row.type) + '"/></div>';
            }},
            { targets: 1, data: 'name', className: 'fade-edge' , render: function ( name, type, row, meta ) {
                var model = getPropertyValue(row.properties, 'DEVICE_MODEL');
                var vendor = getPropertyValue(row.properties, 'VENDOR');
                var html = '<h4>' + name + '</h4>';
                if (model) {
                    html += '<div>(' + vendor + '-' + model + ')</div>';
                }
                return html;
            }},
            { targets: 2, data: 'enrolmentInfo.owner', className: 'fade-edge remove-padding-top'},
            {
                targets: 3, data: 'enrolmentInfo.status', className: 'fade-edge remove-padding-top',
                render: function ( status, type, row, meta ) {
                var html;
                switch (status) {
                    case 'ACTIVE' :
                        html = '<span><i class="fw fw-ok icon-success"></i> Active</span>';
                        break;
                    case 'INACTIVE' :
                        html = '<span><i class="fw fw-warning icon-warning"></i> Inactive</span>';
                        break;
                    case 'BLOCKED' :
                        html = '<span><i class="fw fw-remove icon-danger"></i> Blocked</span>';
                        break;
                    case 'REMOVED' :
                        html = '<span><i class="fw fw-delete icon-danger"></i> Removed</span>';
                        break;
                }
                return html;
            }},
            {
                targets: 4, data: 'type', className: 'fade-edge remove-padding-top',
                render: function ( status, type, row, meta ) {
                    return getDeviceTypeLabel(row.type);
                }
            },
            {
                targets: 5, data: 'enrolmentInfo.ownership', className: 'fade-edge remove-padding-top',
                render: function (status, type, row, meta) {
                    if (getDeviceTypeCategory(row.type) == 'mobile') {
                        return row.enrolmentInfo.ownership;
                    } else {
                        return null;
                    }
                }
            },
            { targets: 6, data: 'enrolmentInfo.status' , className: 'text-right content-fill text-left-on-grid-view no-wrap' ,
                render: function ( status, type, row, meta ) {
                var deviceType = row.type;
                var deviceIdentifier = row.deviceIdentifier;
                var html = '<span></span>';
                if (status != 'REMOVED') {
                    html = '<a href="device/' + deviceType + '?id=' + deviceIdentifier + '" data-click-event="remove-form"' +
                    ' class="btn padding-reduce-on-grid-view"><span class="fw-stack"><i class="fw fw-ring fw-stack-2x"></i>' +
                        '<i class="fw fw-view fw-stack-1x"></i></span><span class="hidden-xs hidden-on-grid-view">View</span></a>';
                    html += '<a href="device/' + deviceType + '/analytics?deviceId=' + deviceIdentifier + '&deviceName=' + row.name + '" ' +
                            'data-click-event="remove-form" class="btn padding-reduce-on-grid-view"><span class="fw-stack">' +
                            '<i class="fw fw-ring fw-stack-2x"></i><i class="fw fw-statistics fw-stack-1x"></i></span>' +
                            '<span class="hidden-xs hidden-on-grid-view">Analytics</span>';

                    if (!groupName || !groupOwner) {
                        html += '<a href="#" data-click-event="remove-form" class="btn padding-reduce-on-grid-view group-device-link" ' +
                                'data-deviceid="' + deviceIdentifier + '" data-devicetype="' + deviceType + '" data-devicename="' +
                                row.name + '"><span class="fw-stack"><i class="fw fw-ring fw-stack-2x"></i>' +
                                '<i class="fw fw-grouping fw-stack-1x"></i></span>' +
                                '<span class="hidden-xs hidden-on-grid-view">Group</span></a>';
                    }

                    html += '<a href="#" data-click-event="remove-form" class="btn padding-reduce-on-grid-view edit-device-link" ' +
                            'data-deviceid="' + deviceIdentifier + '" data-devicetype="' + deviceType + '" data-devicename="' + row.name + '">' +
                            '<span class="fw-stack"><i class="fw fw-ring fw-stack-2x"></i>' +
                            '<i class="fw fw-edit fw-stack-1x"></i></span>' +
                            '<span class="hidden-xs hidden-on-grid-view">Edit</span></a>';
                    html += '<a href="#" data-click-event="remove-form" class="btn padding-reduce-on-grid-view remove-device-link" ' +
                            'data-deviceid="' + deviceIdentifier + '" data-devicetype="' + deviceType + '" data-devicename="' + row.name + '">' +
                            '<span class="fw-stack"><i class="fw fw-ring fw-stack-2x"></i>' +
                            '<i class="fw fw-delete fw-stack-1x"></i></span>' +
                            '<span class="hidden-xs hidden-on-grid-view">Delete</span>';
                }
                return html;
            }}
        ],
        "createdRow": function( row, data, dataIndex ) {
            $(row).attr('data-type', 'selectable');
            $(row).attr('data-deviceid', data.deviceIdentifier);
            $(row).attr('data-devicetype', data.type);
            var model = getPropertyValue(data.properties, 'DEVICE_MODEL');
            var vendor = getPropertyValue(data.properties, 'VENDOR');
            var owner = data.enrolmentInfo.owner;
            var status = data.enrolmentInfo.status;
            var ownership = data.enrolmentInfo.ownership;
            var deviceType = data.type;
            var category = getDeviceTypeCategory(deviceType);
            $.each($('td', row), function (colIndex) {
                switch(colIndex) {
                    case 1:
                        $(this).attr('data-search', model + ',' + vendor);
                        $(this).attr('data-display', model);
                        break;
                    case 2:
                        $(this).attr('data-grid-label', "Owner");
                        $(this).attr('data-search', owner);
                        $(this).attr('data-display', owner);
                        break;
                    case 3:
                        $(this).attr('data-grid-label', "Status");
                        $(this).attr('data-search', status);
                        $(this).attr('data-display', status);
                        break;
                    case 4:
                        $(this).attr('data-grid-label', "Type");
                        $(this).attr('data-search', deviceType);
                        $(this).attr('data-display', getDeviceTypeLabel(deviceType));
                        break;
                    case 5:
                        if (category == 'mobile') {
                            $(this).attr('data-grid-label', "Ownership");
                            $(this).attr('data-search', ownership);
                            $(this).attr('data-display', ownership);
                        }
                        break;
                }
            });
        },
        "fnDrawCallback": function( oSettings ) {
            $(".icon .text").res_text(0.2);
            attachDeviceEvents();
        }
    });
    $(deviceCheckbox).click(function () {
        addDeviceSelectedClass(this);
    });
}

function openCollapsedNav() {
    $('.wr-hidden-nav-toggle-btn').addClass('active');
    $('#hiddenNav').slideToggle('slideDown', function () {
        if ($(this).css('display') == 'none') {
            $('.wr-hidden-nav-toggle-btn').removeClass('active');
        }
    });
}

/*
 * DOM ready functions.
 */
$(document).ready(function () {
    /* Adding selected class for selected devices */
    $(deviceCheckbox).each(function () {
        addDeviceSelectedClass(this);
    });

    var permissionList = $("#permission").data("permission");
    for (var key in permissionList) {
        if (permissionList.hasOwnProperty(key)){
            $.setPermission(key);
        }
    }

    loadDevices();

    /* for device list sorting drop down */
    $(".ctrl-filter-type-switcher").popover({
        html : true,
        content : function () {
            return $("#content-filter-types").html();
        }
    });

    /* for data tables*/
    $('[data-toggle="tooltip"]').tooltip();

    $("[data-toggle=popover]").popover();

    $(".ctrl-filter-type-switcher").popover ({
        html : true,
        content: function() {
            return $('#content-filter-types').html();
        }
    });

    $('#nav').affix ({
        offset: {
            top: $('header').height()
        }
    });

});

var modalPopup = ".wr-modalpopup";
var modalPopupContainer = modalPopup + " .modalpopup-container";
var modalPopupContent = modalPopup + " .modalpopup-content";
var body = "body";

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    $(modalPopupContent).css('max-height', ($(body).height() - ($(body).height() / 100 * 30)));
    $(modalPopupContainer).css('margin-top', (-($(modalPopupContainer).height() / 2)));
}

/*
 * show popup function.
 */
function showPopup() {
    $(modalPopup).show();
    setPopupMaxHeight();
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modalPopupContent).html('');
    $(modalPopup).hide();
}

/**
 * Following functions should be triggered after AJAX request is made.
 */
function attachDeviceEvents() {

    /**
     * Following click function would execute
     * when a user clicks on "Group" link
     * on Device Management page in WSO2 DeviceMgt Console.
     */
    if ($("a.group-device-link").length > 0) {
        $("a.group-device-link").click(function () {
            var deviceId = $(this).data("deviceid");
            var deviceType = $(this).data("devicetype");
            $(modalPopupContent).html($('#group-device-modal-content').html());
            $('#user-groups').html('<div style="height:100px" data-state="loading" data-loading-text="Loading..." data-loading-style="icon-only" data-loading-inverse="true"></div>');
            $("a#group-device-yes-link").hide();
            showPopup();

            var serviceURL;
            if ($.hasPermission("LIST_ALL_GROUPS")) {
                serviceURL = "/devicemgt_admin/groups/all";
            } else if ($.hasPermission("LIST_GROUPS")) {
                //Get authenticated users groups
                serviceURL = "/devicemgt_admin/groups/user/" + currentUser + "/all";
            }

            invokerUtil.get(serviceURL, function (data) {
                var groups = JSON.parse(data);
                var str = '<br /><select id="assign-group-selector" style="color:#3f3f3f;padding:5px;width:250px;">';
                for (var i = 0; i < groups.length; i++) {
                    str += '<option value="' + groups[i].owner + "/name/" + groups[i].name + '">' +
                           groups[i].name + '</option>';
                }
                str += '</select>';
                $('#user-groups').html(str);
                $("a#group-device-yes-link").show();
                $("a#group-device-yes-link").click(function () {
                    var selectedGroup = $('#assign-group-selector').val();
                    serviceURL = "/devicemgt_admin/groups/owner/" + selectedGroup + "/devices";
                    var device = {"id": deviceId, "type": deviceType};
                    invokerUtil.post(serviceURL, device, function (data) {
                        $(modalPopupContent).html($('#group-associate-device-200-content').html());
                        setTimeout(function () {
                            hidePopup();
                            location.reload(false);
                        }, 2000);
                    }, function (jqXHR) {
                        displayDeviceErrors(jqXHR);
                    });
                });
            }, function (jqXHR) {
                if (jqXHR.status == 404) {
                    $(modalPopupContent).html($('#group-404-content').html());
                    $("a#cancel-link").click(function () {
                        hidePopup();
                    });
                } else {
                    displayDeviceErrors(jqXHR);
                }
            });

            $("a#group-device-cancel-link").click(function () {
                hidePopup();
            });
        });

    }

    /**
     * Following click function would execute
     * when a user clicks on "Remove" link
     * on Device Management page in WSO2 MDM Console.
     */
    $("a.remove-device-link").click(function () {
        var deviceId = $(this).data("deviceid");
        var deviceType = $(this).data("devicetype");
        var serviceURL = "/devicemgt_admin/devices/type/" + deviceType + "/id/" + deviceId;

        $(modalPopupContent).html($('#remove-device-modal-content').html());
        showPopup();

        $("a#remove-device-yes-link").click(function () {
            invokerUtil.delete(serviceURL, function (message) {
                $(modalPopupContent).html($('#remove-device-200-content').html());
                setTimeout(function () {
                    hidePopup();
                    location.reload(false);
                }, 2000);
            }, function (message) {
                displayDeviceErrors(jqXHR);
            });
        });

        $("a#remove-device-cancel-link").click(function () {
            hidePopup();
        });
    });

    /**
     * Following click function would execute
     * when a user clicks on "Edit" link
     * on Device Management page in WSO2 MDM Console.
     */
    $("a.edit-device-link").click(function () {
        var deviceId = $(this).data("deviceid");
        var deviceType = $(this).data("devicetype");
        var deviceName = $(this).data("devicename");
        var serviceURL = "/devicemgt_admin/devices/type/" + deviceType + "/id/" + deviceId;

        $(modalPopupContent).html($('#edit-device-modal-content').html());
        $('#edit-device-name').val(deviceName);
        showPopup();

        $("a#edit-device-yes-link").click(function () {
            var newDeviceName = $('#edit-device-name').val();
            invokerUtil.put(serviceURL, {"name": newDeviceName}, function (message) {
                $(modalPopupContent).html($('#edit-device-200-content').html());
                setTimeout(function () {
                    hidePopup();
                    location.reload(false);
                }, 2000);
            }, function (message) {
                displayDeviceErrors(jqXHR);
            });
        });

        $("a#edit-device-cancel-link").click(function () {
            hidePopup();
        });
    });
}

function displayDeviceErrors(jqXHR) {
    showPopup();
    if (jqXHR.status == 400) {
        $(modalPopupContent).html($('#device-400-content').html());
        $("a#device-400-link").click(function () {
            hidePopup();
        });
    } else if (jqXHR.status == 403) {
        $(modalPopupContent).html($('#device-403-content').html());
        $("a#device-403-link").click(function () {
            hidePopup();
        });
    } else if (jqXHR.status == 409) {
        $(modalPopupContent).html($('#device-409-content').html());
        $("a#device-409-link").click(function () {
            hidePopup();
        });
    } else {
        $(modalPopupContent).html($('#device-unexpected-error-content').html());
        $("a#device-unexpected-error-link").click(function () {
            hidePopup();
        });
        console.log("Error code: " + jqXHR.status);
    }
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
