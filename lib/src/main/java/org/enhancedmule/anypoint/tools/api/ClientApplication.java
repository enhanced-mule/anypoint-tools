package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.enhancedmule.anypoint.tools.AnypointClient;
import org.enhancedmule.anypoint.tools.AnypointObject;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.Organization;
import org.enhancedmule.anypoint.tools.util.JsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClientApplication extends AnypointObject<Organization> {
    private Integer id;
    private String name;
    private String description;
    private String url;
    private String clientId;
    private String clientSecret;

    public ClientApplication(AnypointClient client) {
        super(client);
    }

    public ClientApplication(Organization parent) {
        super(parent);
    }

    public ClientApplication() {
    }

    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @JsonIgnore
    public String getUriPath() {
        return parent.getUriPath() + "/applications/" + id;
    }

    public static ClientApplication create(@NotNull Organization organization, @NotNull String name, String url, String description, List<String> redirectUri, String apiEndpoints) throws HttpException {
        AnypointClient client = organization.getClient();
        Map<String, Object> req = client.getJsonHelper().buildJsonMap().set("name", name.trim()).set("url", url)
                .set("description", description != null ? description : "")
                .set("redirectUri", redirectUri).set("apiEndpoints", apiEndpoints)
                .toMap();
        String json = client.getHttpHelper().httpPost(organization.getUriPath() + "/applications", req);
        return client.getJsonHelper().readJson(new ClientApplication(organization), json);
    }

    public static List<ClientApplication> find(Organization organization, String filter) throws HttpException {
        // workaround for the fact that filters sometimes don't work in anypoint... *joy*
        ClientApplicationList list = new ClientApplicationList(organization, null);
        Iterator<ClientApplication> i = list.iterator();
        ArrayList<ClientApplication> matchingClientApplications = new ArrayList<>();
        while (i.hasNext()) {
            ClientApplication clientApplication = i.next();
            if (clientApplication.getName().contains(filter)) {
                matchingClientApplications.add(clientApplication);
            }
        }
        return matchingClientApplications;
    }

    public void delete() throws HttpException {
        httpHelper.httpDelete(getUriPath());
    }

    public APIContract requestAPIAccess(API apiVersion) throws HttpException {
        return requestAPIAccess(apiVersion, null, false);
    }

    public APIContract requestAPIAccess(API apiVersion, SLATier tier) throws HttpException {
        return requestAPIAccess(apiVersion, tier, true);
    }

    public APIContract requestAPIAccess(API apiVersion, SLATier tier, boolean acceptedTerms) throws HttpException {
        JsonHelper.MapBuilder mapBuilder = jsonHelper.buildJsonMap()
                .set("apiId", apiVersion.getId())
                .set("environmentId", apiVersion.getParent().getId())
                .set("acceptedTerms", acceptedTerms)
                .set("organizationId", apiVersion.getParent().getParent().getId())
                .set("groupId", apiVersion.getGroupId())
                .set("assetId", apiVersion.getAssetId())
                .set("version", apiVersion.getAssetVersion())
                .set("productAPIVersion", apiVersion.getProductVersion());
        if (tier != null && tier.getId() == null) {
            throw new IllegalArgumentException("Tier is missing tier id");
        }
        Long tierId = tier != null ? tier.getId() : null;
        if (tierId == null) {
            SLATierList apiTiers = apiVersion.findSLATiers();
            if (apiTiers.size() == 1) {
                tierId = apiTiers.iterator().next().getId();
            }
        }
        if (tierId != null) {
            mapBuilder.set("requestedTierId", tierId);
        }
        Map<String, Object> req = mapBuilder.toMap();
        String json = httpHelper.httpPost("/exchange/api/v1/organizations/" + parent.getId() + "/applications/" + id + "/contracts", req);
        return jsonHelper.readJson(new APIContract(apiVersion), json);
    }
}
