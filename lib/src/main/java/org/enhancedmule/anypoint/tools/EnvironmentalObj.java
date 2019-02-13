package org.enhancedmule.anypoint.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnvironmentalObj extends AnypointObject<Environment> {
    public EnvironmentalObj() {
    }

    public EnvironmentalObj(Environment environment) {
        super(environment);
    }

    @JsonIgnore
    public Environment getEnvironment() {
        return parent;
    }
}
