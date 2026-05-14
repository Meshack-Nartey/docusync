package com.docusyn.parser;

import com.docusyn.model.Contract;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;

public class ContractParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public Contract parse(Path inputPath) throws IOException {
        Contract contract = mapper.readValue(inputPath.toFile(), Contract.class);
        validate(contract);
        return contract;
    }

    private void validate(Contract contract) {
        if (contract.projectName == null || contract.projectName.isBlank()) {
            throw new IllegalArgumentException("contract.json must have a non-empty 'projectName'");
        }
        if (contract.basePath == null || contract.basePath.isBlank()) {
            throw new IllegalArgumentException("contract.json must have a non-empty 'basePath'");
        }
        if (contract.endpoints == null || contract.endpoints.isEmpty()) {
            throw new IllegalArgumentException("contract.json must have at least one endpoint");
        }
        for (int i = 0; i < contract.endpoints.size(); i++) {
            var ep = contract.endpoints.get(i);
            if (ep.method == null || ep.method.isBlank()) {
                throw new IllegalArgumentException("Endpoint[" + i + "] is missing 'method'");
            }
            if (ep.path == null || ep.path.isBlank()) {
                throw new IllegalArgumentException("Endpoint[" + i + "] is missing 'path'");
            }
        }
    }
}
