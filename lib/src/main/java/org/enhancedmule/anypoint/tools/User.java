package org.enhancedmule.anypoint.tools;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class User extends AnypointObject {
    @JsonProperty
    private String id;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String email;
    @JsonProperty
    private String username;
    @JsonProperty
    private String enabled;
    @JsonProperty
    private Organization organization;
    @JsonProperty
    private List<Organization> memberOfOrganizations;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Organization> getMemberOfOrganizations() {
        return memberOfOrganizations;
    }

    public void setMemberOfOrganizations(List<Organization> memberOfOrganizations) {
        this.memberOfOrganizations = memberOfOrganizations;
    }

    @Override
    public void setClient(AnypointClient client) {
        super.setClient(client);
        if (organization != null) {
            organization.setClient(client);
        }
    }
}
