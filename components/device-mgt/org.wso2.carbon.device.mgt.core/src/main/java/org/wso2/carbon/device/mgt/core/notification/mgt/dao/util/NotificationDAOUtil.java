/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.core.notification.mgt.dao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagementDataHolder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * This class includes the utility methods required by NotificationMgmt functionalities.
 */
public class NotificationDAOUtil {

	private static final Log log = LogFactory.getLog(NotificationDAOUtil.class);

	public static void cleanupResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("Error occurred while closing result set", e);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.warn("Error occurred while closing prepared statement", e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.warn("Error occurred while closing database connection", e);
			}
		}
	}

	public static void cleanupResources(PreparedStatement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("Error occurred while closing result set", e);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				log.warn("Error occurred while closing prepared statement", e);
			}
		}
	}

	/**
	 * Get id of the current tenant.
	 *
	 * @return tenant id
	 * @throws org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException if an error is observed when getting tenant id
	 */
	public static int getTenantId() throws NotificationManagementException {
		CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
		int tenantId = context.getTenantId();
		if (tenantId != MultitenantConstants.INVALID_TENANT_ID) {
			return tenantId;
		}
		String tenantDomain = context.getTenantDomain();
		if (tenantDomain == null) {
			String msg = "Tenant domain is not properly set and thus, is null";
			throw new NotificationManagementException(msg);
		}
		TenantManager tenantManager = DeviceManagementDataHolder.getInstance().getTenantManager();
		try {
			tenantId = tenantManager.getTenantId(tenantDomain);
		} catch (UserStoreException e) {
			String msg =
					"Error occurred while retrieving id from the domain of tenant " + tenantDomain;
			throw new NotificationManagementException(msg);
		}
		return tenantId;
	}

	public static DataSource lookupDataSource(String dataSourceName,
	                                          final Hashtable<Object, Object> jndiProperties) {
		try {
			if (jndiProperties == null || jndiProperties.isEmpty()) {
				return (DataSource) InitialContext.doLookup(dataSourceName);
			}
			final InitialContext context = new InitialContext(jndiProperties);
			return (DataSource) context.lookup(dataSourceName);
		} catch (Exception e) {
			throw new RuntimeException("Error in looking up data source: " + e.getMessage(), e);
		}
	}

	public static Notification getNotification(ResultSet rs) throws SQLException {
		Notification notification = new Notification();
		notification.setNotificationId(rs.getInt("NOTIFICATION_ID"));
		notification.setOperationId(rs.getInt("OPERATION_ID"));
		notification.setDescription(rs.getString("DESCRIPTION"));
		notification.setStatus(rs.getString("STATUS"));
		return notification;
	}
}
