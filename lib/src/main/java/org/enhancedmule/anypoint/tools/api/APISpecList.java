package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.util.URLBuilder;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.Organization;
import org.enhancedmule.anypoint.tools.util.PaginatedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APISpecList extends PaginatedList<APISpec, Organization> {
    private final String filter;

    public APISpecList(Organization organization, String filter) throws HttpException {
        super(organization);
        this.filter = filter;
        limit = 50;
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        URLBuilder url = new URLBuilder("/apimanager/xapi/v1/organizations/" + parent.getId() + "/apiSpecs")
                .param("ascending", "true");
        if (filter != null) {
            url.param("searchTerm", filter);
        }
        return url;
    }

    @JsonProperty
    public List<APISpec> getApiDefinitions() {
        return list;
    }

    public void setApiDefinitions(List<APISpec> list) {
        this.list = list;
    }
}
