package org.enhancedmule.anypoint.tools.cloudhub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CHOrganization {
    private CHPlan plan;

    @JsonProperty
    public CHPlan getPlan() {
        return plan;
    }

    public void setPlan(CHPlan plan) {
        this.plan = plan;
    }
}
