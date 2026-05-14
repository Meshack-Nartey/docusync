package com.docusyn.engine;

import com.docusyn.generator.HtmlGenerator;
import com.docusyn.model.Contract;
import com.docusyn.parser.ContractParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocuSynEngine {

    private final ContractParser parser = new ContractParser();
    private final HtmlGenerator generator = new HtmlGenerator();

    public void run(Path inputPath, Path outputPath) throws IOException {
        if (!Files.exists(inputPath)) {
            throw new IllegalArgumentException("Input file not found: " + inputPath);
        }

        System.out.println("Parsing: " + inputPath.toAbsolutePath());
        Contract contract = parser.parse(inputPath);

        System.out.printf("Contract: \"%s\" — %d endpoint(s)%n",
            contract.projectName, contract.endpoints.size());

        String html = generator.generate(contract);

        Files.writeString(outputPath, html);
    }
}
