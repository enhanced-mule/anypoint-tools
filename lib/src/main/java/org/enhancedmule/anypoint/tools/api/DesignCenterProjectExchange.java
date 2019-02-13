package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.util.InvalidStateException;
import org.enhancedmule.anypoint.tools.AnypointObject;
import org.enhancedmule.anypoint.tools.HttpException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesignCenterProjectExchange extends AnypointObject<DesignCenterProject> {
    public static final Pattern majorVersionRegex = Pattern.compile("(\\d*?)\\.");
    private final String branch;
    private String main;
    private String name;
    private String classifier;
    private String groupId;
    private String assetId;
    private String version;
    private String apiVersion;
    private String nextVersion;
    private boolean enableCreatePlatformApis;
    private boolean isPublishedVersion;

    public DesignCenterProjectExchange(DesignCenterProject designCenterProject, String branch) {
        super(designCenterProject);
        this.branch = branch;
    }

    @JsonIgnore
    public String getBranch() {
        return branch;
    }

    @JsonProperty
    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @JsonProperty
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @JsonProperty
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty
    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @JsonProperty
    public String getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(String nextVersion) {
        this.nextVersion = nextVersion;
    }

    @JsonProperty
    public boolean isEnableCreatePlatformApis() {
        return enableCreatePlatformApis;
    }

    public void setEnableCreatePlatformApis(boolean enableCreatePlatformApis) {
        this.enableCreatePlatformApis = enableCreatePlatformApis;
    }

    @JsonProperty
    public boolean isPublishedVersion() {
        return isPublishedVersion;
    }

    public void setPublishedVersion(boolean publishedVersion) {
        isPublishedVersion = publishedVersion;
    }

    public void publish() throws HttpException {
        publish(null, null);
    }

    public void publish(String version, String apiVersion) throws HttpException {
        if (version == null) {
            version = nextVersion;
        }
        if (apiVersion == null) {
            Matcher m = majorVersionRegex.matcher(version);
            if (!m.find()) {
                throw new InvalidStateException("Invalid version " + version);
            }
            apiVersion = "v" + m.group(1);
        }
        parent.publishExchange(branch, assetId, name, main, version, apiVersion);
    }
}
