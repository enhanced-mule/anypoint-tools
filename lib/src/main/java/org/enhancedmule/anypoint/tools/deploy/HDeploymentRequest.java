package org.enhancedmule.anypoint.tools.deploy;

import com.kloudtek.unpack.transformer.SetPropertyTransformer;
import com.kloudtek.unpack.transformer.Transformer;
import org.enhancedmule.anypoint.tools.AnypointClient;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.NotFoundException;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningConfig;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningResult;
import org.enhancedmule.anypoint.tools.runtime.DeploymentResult;
import org.enhancedmule.anypoint.tools.runtime.HApplication;
import org.enhancedmule.anypoint.tools.runtime.HDeploymentResult;
import org.enhancedmule.anypoint.tools.runtime.Server;
import org.enhancedmule.anypoint.tools.util.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HDeploymentRequest extends DeploymentRequest {
    private static final Logger logger = LoggerFactory.getLogger(HDeploymentRequest.class);
    private Server target;

    public HDeploymentRequest(Server target, String appName, ApplicationSource file, String filename, Map<String, String> properties, APIProvisioningConfig apiProvisioningConfig) {
        super(target.getParent(), appName, file, filename, properties, apiProvisioningConfig);
        this.target = target;
    }

    @Override
    protected void preDeploy(APIProvisioningResult result, APIProvisioningConfig config, List<Transformer> transformers) {
        if (properties != null && !properties.isEmpty()) {
            transformers.add(new SetPropertyTransformer(apiProvisioningConfig.getConfigFile(),
                    new HashMap<>(properties)));
        }
    }

    @Override
    protected DeploymentResult doDeploy() throws IOException, HttpException {
        HttpHelper.MultiPartRequest request;
        long start = System.currentTimeMillis();
        AnypointClient client = environment.getClient();
        HttpHelper httpHelper = client.getHttpHelper();
        try {
            logger.debug("Searching for pre-existing application named " + appName);
            HApplication application = target.findApplication(appName);
            logger.debug("Found application named {} : {}", appName, application.getId());
            request = httpHelper.createMultiPartPatchRequest("/hybrid/api/v1/applications/" + application.getId(),
                    target.getParent());
        } catch (NotFoundException e) {
            logger.debug("Couldn't find application named {}", appName);
            request = httpHelper.createMultiPartPostRequest("/hybrid/api/v1/applications", environment);
        }
        HttpHelper.MultiPartRequest multiPartRequest = request.addText("artifactName", appName)
                .addText("targetId", target.getId());
        String json = executeRequest(start, multiPartRequest);
        HApplication application = target.getClient().getJsonHelper().readJson(new HApplication(target), json, "/data");
        return new HDeploymentResult(application);
    }
}
