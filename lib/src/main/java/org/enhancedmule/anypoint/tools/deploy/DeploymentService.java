package org.enhancedmule.anypoint.tools.deploy;

import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.Service;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningConfig;
import org.enhancedmule.anypoint.tools.api.provision.ProvisioningException;
import org.enhancedmule.anypoint.tools.runtime.HDeploymentResult;
import org.enhancedmule.anypoint.tools.runtime.Server;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public interface DeploymentService extends Service {
    HDeploymentResult deployOnPrem(Server target, @NotNull String name, @NotNull File file, @NotNull String filename,
                                   APIProvisioningConfig apiProvisioningConfig)
            throws IOException, HttpException, ProvisioningException;

}
