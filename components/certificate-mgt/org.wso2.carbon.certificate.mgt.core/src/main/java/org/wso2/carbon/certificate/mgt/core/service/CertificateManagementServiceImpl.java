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
package org.wso2.carbon.certificate.mgt.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.wso2.carbon.certificate.mgt.core.dao.CertificateDAO;
import org.wso2.carbon.certificate.mgt.core.dao.CertificateManagementDAOException;
import org.wso2.carbon.certificate.mgt.core.dao.CertificateManagementDAOFactory;
import org.wso2.carbon.certificate.mgt.core.dto.CertificateResponse;
import org.wso2.carbon.certificate.mgt.core.dto.SCEPResponse;
import org.wso2.carbon.certificate.mgt.core.exception.CertificateManagementException;
import org.wso2.carbon.certificate.mgt.core.exception.KeystoreException;
import org.wso2.carbon.certificate.mgt.core.exception.TransactionManagementException;
import org.wso2.carbon.certificate.mgt.core.impl.CertificateGenerator;
import org.wso2.carbon.certificate.mgt.core.impl.KeyStoreReader;
import org.wso2.carbon.certificate.mgt.core.util.CertificateManagementConstants;
import org.wso2.carbon.certificate.mgt.core.util.CertificateManagerUtil;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.List;

public class CertificateManagementServiceImpl implements CertificateManagementService {

    private static final Log log = LogFactory.getLog(CertificateManagementServiceImpl.class);
    private static CertificateManagementServiceImpl certificateManagementServiceImpl;
    private static KeyStoreReader keyStoreReader;
    private static CertificateGenerator certificateGenerator;

    private CertificateManagementServiceImpl() {
    }

    public static CertificateManagementServiceImpl getInstance() {
        if (certificateManagementServiceImpl == null) {
            certificateManagementServiceImpl = new CertificateManagementServiceImpl();
            keyStoreReader = new KeyStoreReader();
            certificateGenerator = new CertificateGenerator();
        }
        return certificateManagementServiceImpl;
    }

    public Certificate getCACertificate() throws KeystoreException {
        return keyStoreReader.getCACertificate();
    }

    public Certificate getRACertificate() throws KeystoreException {
        return keyStoreReader.getRACertificate();
    }

    public List<X509Certificate> getRootCertificates(byte[] ca, byte[] ra) throws KeystoreException {
        return certificateGenerator.getRootCertificates(ca, ra);
    }

    public X509Certificate generateX509Certificate() throws KeystoreException {
        return certificateGenerator.generateX509Certificate();
    }

    public SCEPResponse getCACertSCEP() throws KeystoreException {
        return certificateGenerator.getCACert();
    }

    public byte[] getCACapsSCEP() {
        return CertificateManagementConstants.POST_BODY_CA_CAPS.getBytes();
    }

    public byte[] getPKIMessageSCEP(InputStream inputStream) throws KeystoreException {
        return certificateGenerator.getPKIMessage(inputStream);
    }

    public X509Certificate generateCertificateFromCSR(PrivateKey privateKey,
                                                      PKCS10CertificationRequest request,
                                                      String issueSubject) throws KeystoreException {
        return certificateGenerator.generateCertificateFromCSR(privateKey, request, issueSubject);
    }

    public Certificate getCertificateByAlias(String alias) throws KeystoreException {
        return keyStoreReader.getCertificateByAlias(alias);
    }

    public boolean verifySignature(String headerSignature) throws KeystoreException {
        return certificateGenerator.verifySignature(headerSignature);
    }

    public CertificateResponse verifyPEMSignature(X509Certificate requestCertificate) throws KeystoreException {
        return certificateGenerator.verifyPEMSignature(requestCertificate);
    }

    @Override
    public CertificateResponse verifySubjectDN(String requestDN) throws KeystoreException {
        return certificateGenerator.verifyCertificateDN(requestDN);
    }

    public X509Certificate extractCertificateFromSignature(String headerSignature) throws KeystoreException {
        return certificateGenerator.extractCertificateFromSignature(headerSignature);
    }

    public String extractChallengeToken(X509Certificate certificate) {
        return certificateGenerator.extractChallengeToken(certificate);
    }

    public X509Certificate getSignedCertificateFromCSR(String binarySecurityToken) throws KeystoreException {
        return certificateGenerator.getSignedCertificateFromCSR(binarySecurityToken);
    }

    public CertificateResponse getCertificateBySerial(String serial) throws KeystoreException {
        return keyStoreReader.getCertificateBySerial(serial);
    }

    public void saveCertificate(List<org.wso2.carbon.certificate.mgt.core.bean.Certificate> certificate)
            throws KeystoreException {
        certificateGenerator.saveCertInKeyStore(certificate);
    }

    public X509Certificate pemToX509Certificate(String pem) throws KeystoreException {
        return certificateGenerator.pemToX509Certificate(pem);
    }

    public CertificateResponse retrieveCertificate(String serialNumber) throws CertificateManagementException {
        CertificateDAO certificateDAO;
        try {
            CertificateManagementDAOFactory.openConnection();
            certificateDAO = CertificateManagementDAOFactory.getCertificateDAO();
            return certificateDAO.retrieveCertificate(serialNumber);
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the underlying data source";
            throw new CertificateManagementException(msg, e);
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while looking up for the certificate carrying the serial number '" +
                    serialNumber + "' in the underlying certificate repository";
            throw new CertificateManagementException(msg, e);
        } finally {
            CertificateManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public PaginationResult getAllCertificates(int rowNum, int limit) throws CertificateManagementException {
        try {
            CertificateManagementDAOFactory.openConnection();
            CertificateDAO certificateDAO = CertificateManagementDAOFactory.getCertificateDAO();
            return certificateDAO.getAllCertificates(rowNum, CertificateManagerUtil.validateCertificateListPageSize(limit));
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the underlying data source";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while looking up for the list of certificates managed in the underlying " +
                         "certificate repository";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } finally {
            CertificateManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public boolean removeCertificate(String serialNumber) throws CertificateManagementException {
        try {
            CertificateManagementDAOFactory.beginTransaction();
            CertificateDAO certificateDAO = CertificateManagementDAOFactory.getCertificateDAO();
            boolean status = certificateDAO.removeCertificate(serialNumber);
            CertificateManagementDAOFactory.commitTransaction();
            return status;
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while removing certificate carrying serial number '" + serialNumber + "'";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } catch (CertificateManagementDAOException e) {
            CertificateManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while removing the certificate carrying serial number '" + serialNumber +
                    "' from the certificate repository";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        }
    }

    @Override
    public List<CertificateResponse> getCertificates() throws CertificateManagementException {
        try {
            CertificateManagementDAOFactory.openConnection();
            CertificateDAO certificateDAO = CertificateManagementDAOFactory.getCertificateDAO();
            return certificateDAO.getAllCertificates();
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the underlying data source";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while looking up for the list of certificates managed in the " +
                    "underlying certificate repository";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } finally {
            CertificateManagementDAOFactory.closeConnection();
        }
    }

    @Override
    public List<CertificateResponse> searchCertificates(String serialNumber) throws CertificateManagementException {
        try {
            CertificateManagementDAOFactory.openConnection();
            CertificateDAO certificateDAO = CertificateManagementDAOFactory.getCertificateDAO();
            return certificateDAO.searchCertificate(serialNumber);
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the underlying data source";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } catch (CertificateManagementDAOException e) {
            String msg = "Error occurred while searching for the list of certificates carrying the serial number '" +
                    serialNumber + "' in the underlying certificate repository";
            log.error(msg, e);
            throw new CertificateManagementException(msg, e);
        } finally {
            CertificateManagementDAOFactory.closeConnection();
        }
    }

}
