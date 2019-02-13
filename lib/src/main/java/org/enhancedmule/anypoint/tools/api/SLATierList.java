package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.util.URLBuilder;
import org.enhancedmule.anypoint.tools.HttpException;
import org.enhancedmule.anypoint.tools.util.PaginatedList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SLATierList extends PaginatedList<SLATier, API> {
    public SLATierList(API api) throws HttpException {
        super(api);
        download();
    }

    @NotNull
    @Override
    protected URLBuilder buildUrl() {
        return new URLBuilder("/apimanager/api/v1/organizations/" + parent.getParent().getParent().getId() +
                "/environments/" + parent.getParent().getId() + "/apis/" + parent.getId() + "/tiers");
    }

    @JsonProperty
    public List<SLATier> getTiers() {
        return list;
    }

    public void setTiers(List<SLATier> tiers) {
        this.list = tiers;
    }
}
