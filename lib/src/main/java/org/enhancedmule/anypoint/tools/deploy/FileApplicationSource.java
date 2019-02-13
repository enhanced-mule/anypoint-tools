package org.enhancedmule.anypoint.tools.deploy;

import org.enhancedmule.anypoint.tools.AnypointClient;
import org.enhancedmule.anypoint.tools.api.provision.APIProvisioningDescriptor;
import org.enhancedmule.anypoint.tools.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileApplicationSource extends ApplicationSource {
    private File file;

    FileApplicationSource(AnypointClient client, File file) {
        super(client);
        this.file = file;
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public File getLocalFile() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public APIProvisioningDescriptor getAPIProvisioningDescriptor() throws IOException {
        return readDescriptorFromZip(file);
    }

    @Override
    public String getArtifactId() {
        return file.getName();
    }

    @Override
    public Map<String, Object> getSourceJson(JsonHelper jsonHelper) {
        throw new UnsupportedOperationException("getSourceJson() not supported for file source");
    }

    @Override
    public void close() throws IOException {
    }
}
