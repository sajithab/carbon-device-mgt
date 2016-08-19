/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.certificate.mgt.core.dao;

import org.wso2.carbon.certificate.mgt.core.bean.Certificate;
import org.wso2.carbon.certificate.mgt.core.dto.CertificateResponse;
import org.wso2.carbon.certificate.mgt.core.service.PaginationResult;

import java.util.List;

/**
 * This class represents the key operations associated with persisting certificate related
 * information.
 */
public interface CertificateDAO {

    /**
     * This can be used to store a certificate in the database, where it will be stored against the serial number
     * of the certificate.
     *
     * @param certificate Holds the certificate and relevant details.
     * @throws CertificateManagementDAOException
     *
     */
    void addCertificate(List<Certificate> certificate)
            throws CertificateManagementDAOException;

    /**
     * Usage is to obtain a certificate stored in the database by providing the common name.
     *
     * @param serialNumber Serial number of the certificate.
     * @return representation of the certificate.
     * @throws CertificateManagementDAOException
     *
     */
    CertificateResponse retrieveCertificate(String serialNumber) throws CertificateManagementDAOException;

    /**
     * Get all the certificates in a paginated manner.
     *
     * @param rowNum Stating index of the paginated result.
     * @param limit Number of records to return.
     * @return Pagination result with data and the count of results.
     * @throws CertificateManagementDAOException
     *
     */
    PaginationResult getAllCertificates(int rowNum, int limit) throws CertificateManagementDAOException;

    /**
     * Get all the certificates.
     *
     * @return List of certificates
     * @throws CertificateManagementDAOException
     *
     */
    List<CertificateResponse> getAllCertificates() throws CertificateManagementDAOException;

    /**
     * Delete a certificate identified by a serial number()
     *
     * @param serialNumber serial number
     * @return whether the certificate was removed or not.
     */
    boolean removeCertificate(String serialNumber) throws CertificateManagementDAOException;

    List<CertificateResponse> searchCertificate(String serialNumber) throws CertificateManagementDAOException;

}
