package org.wso2.carbon.apimgt.application.extension.api.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.application.extension.api.util.APIUtil;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * this filter check for permission for the request
 */
public class ApiPermissionFilter implements Filter {
    private static final Log log = LogFactory.getLog(ApiPermissionFilter.class);
    private static final String UI_EXECUTE = "ui.execute";
    private static final String PERMISSION_CONFIG_PATH = File.separator + "META-INF" + File.separator
            + "permissions.xml";
    private static final String PERMISSION_PREFIX = "/permission/admin";
    private static List<Permission> permissions;
    private static final String WEBAPP_CONTEXT = "/api-application-registration";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        InputStream permissionStream = filterConfig.getServletContext().getResourceAsStream(PERMISSION_CONFIG_PATH);
        if (permissionStream != null) {
            try {
                JAXBContext cdmContext = JAXBContext.newInstance(PermissionConfiguration.class);
                Unmarshaller unmarshaller = cdmContext.createUnmarshaller();
                PermissionConfiguration permissionConfiguration = (PermissionConfiguration)
                        unmarshaller.unmarshal(permissionStream);
                permissions = permissionConfiguration.getPermissions();
            } catch (JAXBException e) {
                log.error("invalid permissions.xml", e);
            }

        }

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            String uri = ((HttpServletRequest)servletRequest).getRequestURI();
            boolean status = false;
            String urlPermission = getPermission(uri);
            if (urlPermission != null) {
                status = isUserAuthorized(PERMISSION_PREFIX + urlPermission, UI_EXECUTE);
            }
            if (status) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }

    @Override
    public void destroy() {
        //do nothing
    }

    private static String getPermission(String url) {
        if (permissions != null) {
            for (int i = 0; i < permissions.size(); i++) {
                Permission permission = permissions.get(i);
                if ((WEBAPP_CONTEXT + permission.getUrl()).equals(url)) {
                    return permission.getPath();
                }
            }
        }
        return null;
    }

    /**
     * Check whether the client is authorized with the given permission and action.
     * @param permission           Carbon permission that requires for the use
     * @param action               Carbon permission action that requires for the given permission.
     * @return boolean - true if user is authorized else return false.
     */
    private boolean isUserAuthorized(String permission, String action) {
        PrivilegedCarbonContext context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = context.getUsername();
        try {
            UserRealm userRealm = APIUtil.getRealmService().getTenantUserRealm(PrivilegedCarbonContext
                                .getThreadLocalCarbonContext().getTenantId());
            return userRealm.getAuthorizationManager().isUserAuthorized(username, permission, action);
        } catch (UserStoreException e) {
            String errorMsg = String.format("Unable to authorize the user : %s", username, e);
            log.error(errorMsg, e);
            return false;
        }
    }

}
