package org.enhancedmule.anypoint.tools.deploy;

import org.enhancedmule.anypoint.tools.Environment;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.runtime.HDeploymentResult;

import java.io.File;
import java.io.IOException;

public interface DeploymentOperation {
    HDeploymentResult deploy(Environment environment, String appName, String filename, File file) throws IOException, HttpException;
}
