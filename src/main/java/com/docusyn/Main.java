package com.docusyn;

import com.docusyn.engine.DocuSynEngine;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar docusyn.jar <contract.json> [output.html]");
            System.err.println("  contract.json  Path to the input contract file");
            System.err.println("  output.html    Path for generated output (default: API_EXPLORER.html)");
            System.exit(1);
        }

        Path inputPath  = Path.of(args[0]);
        Path outputPath = args.length >= 2 ? Path.of(args[1]) : Path.of("API_EXPLORER.html");

        try {
            new DocuSynEngine().run(inputPath, outputPath);
            System.out.println("Generated: " + outputPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
