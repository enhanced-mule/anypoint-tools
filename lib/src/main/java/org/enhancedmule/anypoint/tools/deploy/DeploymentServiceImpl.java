package org.enhancedmule.anypoint.tools.deploy;

import org.enhancedmule.anypoint.tools.AbstractService;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningConfig;
import org.enhancedmule.anypoint.tools.api.provision.ProvisioningException;
import org.enhancedmule.anypoint.tools.runtime.HDeploymentResult;
import org.enhancedmule.anypoint.tools.runtime.Server;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class DeploymentServiceImpl extends AbstractService implements DeploymentService {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentServiceImpl.class);

    /**
     * Deploy application with optional automatic api provisioning
     *
     * @param target                target to deploy to
     * @param appName               Application name
     * @param file                  Application archive file
     * @param filename              Application archive filename
     * @param apiProvisioningConfig API Provisioning config (if null no API provisioning will be done)
     * @return Deployment result
     * @throws IOException   If an error occurs reading the application file
     * @throws HttpException If an error occurs commnunicating with anypoint
     */
    @Override
    public HDeploymentResult deployOnPrem(Server target, @NotNull String appName, @NotNull File file, @NotNull String filename,
                                          APIProvisioningConfig apiProvisioningConfig) throws IOException, HttpException, ProvisioningException {
//        OnPremDeploymentOperation deploymentRequest = new OnPremDeploymentOperation(target);
//        return deploy(target.getParent(), appName, file, filename, apiProvisioningConfig, deploymentRequest);
        return null;
    }

}
