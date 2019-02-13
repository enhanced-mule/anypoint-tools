package org.enhancedmule.anypoint.tools.exchange;

/**
 * Created by JacksonGenerator on 6/26/18.
 */

import com.fasterxml.jackson.annotation.JsonProperty;


public class AssetMetadata {
    @JsonProperty("fullVersion")
    private String fullVersion;
    @JsonProperty("minMuleVersion")
    private String minMuleVersion;
    @JsonProperty("featureId")
    private String featureId;
}