package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.util.URLBuilder;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.util.PaginatedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APIContractList extends PaginatedList<APIContract, API> {
    public APIContractList(API api) throws HttpException {
        super(api);
        limit = 50;
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        URLBuilder url = new URLBuilder("/apimanager/api/v1/organizations/" + parent.getParent().getParent().getId() + "/environments/" + parent.getParent().getId() + "/apis/" + parent.getId() + "/contracts")
                .param("ascending", "true");
        return url;
    }

    @JsonProperty
    public List<APIContract> getContracts() {
        return list;
    }

    public void setContracts(List<APIContract> list) {
        this.list = list;
    }
}
