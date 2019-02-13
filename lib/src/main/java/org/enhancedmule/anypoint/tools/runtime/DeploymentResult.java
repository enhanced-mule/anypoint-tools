package org.enhancedmule.anypoint.tools.runtime;

import org.enhancedmule.anypoint.tools.HttpException;

public abstract class DeploymentResult {
    public void waitDeployed() throws HttpException, ApplicationDeploymentFailedException {
        waitDeployed(60000L, 1500L);
    }

    public abstract void waitDeployed(long timeout, long retryDelay) throws HttpException, ApplicationDeploymentFailedException;
}
