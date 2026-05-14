package com.docusyn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contract {
    public String projectName;
    public String basePath;
    public List<Endpoint> endpoints;
}
