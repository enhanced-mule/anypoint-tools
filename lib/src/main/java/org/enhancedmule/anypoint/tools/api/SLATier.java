package org.enhancedmule.anypoint.tools.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.enhancedmule.anypoint.tools.AnypointClient;
import org.enhancedmule.anypoint.tools.AnypointObject;

import java.util.ArrayList;
import java.util.List;

public class SLATier extends AnypointObject<API> {
    private Long id;
    private String name;

    public SLATier(AnypointClient client) {
        super(client);
    }

    public SLATier(API parent) {
        super(parent);
    }

    public SLATier() {
    }

    public static List<String> getNames(List<SLATier> slaTier) {
        ArrayList<String> names = new ArrayList<>(slaTier.size());
        for (SLATier tier : slaTier) {
            if (tier.getName() != null) {
                names.add(tier.getName());
            }
        }
        return names;
    }

    @JsonProperty
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
