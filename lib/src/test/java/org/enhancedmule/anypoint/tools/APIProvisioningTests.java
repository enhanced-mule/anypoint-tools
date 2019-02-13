package org.enhancedmule.anypoint.tools;

import org.enhancedmule.anypoint.tools.api.SLATierLimits;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningConfig;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningDescriptor;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningResult;
import org.enhancedmule.anypoint.tools.api.provision.SLATierDescriptor;
import org.enhancedmule.anypoint.tools.util.AbstractAnypointTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APIProvisioningTests extends AbstractAnypointTest {
    @Test
    public void testProvisioning() throws Exception {
        createAPIAsset(TESTAPI1, true);
        createAPIAsset(TESTAPI2, true);
        APIProvisioningConfig config = new APIProvisioningConfig();
        config.setVariable("url", "http://foo");
        // provision api 1
        APIProvisioningDescriptor apd1 = addClientIdPolicy(new APIProvisioningDescriptor(TESTAPI1, V1));
        APIProvisioningResult res1 = provision(config, apd1);
        // provision api 2
        APIProvisioningDescriptor apd2 = addClientIdPolicy(new APIProvisioningDescriptor(TESTAPI2, V1));
        apd2.addAccess(res1.getApi());
        apd2.addSlaTier(new SLATierDescriptor("testtier", false, new SLATierLimits(true, 1, 1)));
        APIProvisioningResult res2 = provision(config, apd2);
        // test changing client id expression
        assertEquals(2, env.findAPIs(null).size());
        checkPolicy(TESTAPI2, V1, ATTRIBUTES_HEADERS_CLIENT_SECRET);
        apd2.getPolicies().get(0).getData().put(CLIENT_SECRET_EXPRESSION, ATTRIBUTES_HEADERS_CLIENT_SECRET2);
        provision(config, apd2);
        checkPolicy(TESTAPI2, V1, ATTRIBUTES_HEADERS_CLIENT_SECRET2);
        // request access to api2 from api1
        apd1.addAccess(res2.getApi());
        provision(config, apd1);
//        res2.getApi().refresh();
    }
}
