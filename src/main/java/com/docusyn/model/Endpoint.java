package com.docusyn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Endpoint {
    public String method;
    public String path;
    public String description;
    public JsonNode payload;
    public JsonNode headers;
}
