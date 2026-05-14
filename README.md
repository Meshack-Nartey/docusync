# DocuSync

Generate a standalone, interactive API Explorer from a single `contract.json` file.

No Postman. No Swagger setup. Just a JSON file and one command.

---

## What it does

DocuSync reads your `contract.json` and outputs a self-contained `API_EXPLORER.html` file. Open it in any browser to read your API documentation **and** fire live requests — all in one place.

![DocuSync Preview](https://placehold.co/900x500?text=API+Explorer+Preview)

---

## Installation

**Requirements:** Java 17 or higher — [download here](https://adoptium.net)

```bash
git clone https://github.com/YOUR_USERNAME/docusync.git
cd docusync
./install.sh
```

Open a **new terminal** when it's done. That's it.

---

## Usage

In any project, create a `contract.json` then run:

```bash
docusync contract.json
```

This writes `API_EXPLORER.html` in the current directory. Open it in your browser.

```bash
# Custom output name
docusync contract.json docs.html
```

---

## contract.json format

```json
{
  "projectName": "Your API Name",
  "basePath": "http://localhost:8080/api/v1",
  "endpoints": [
    {
      "method": "GET",
      "path": "/users",
      "description": "Retrieve all users",
      "payload": null,
      "headers": {
        "Accept": "application/json"
      }
    },
    {
      "method": "POST",
      "path": "/users",
      "description": "Create a new user",
      "payload": {
        "username": "johndoe",
        "email": "john@example.com"
      },
      "headers": {
        "Content-Type": "application/json"
      }
    }
  ]
}
```

| Field | Required | Description |
|---|---|---|
| `projectName` | Yes | Displayed in the top bar |
| `basePath` | Yes | Base URL prepended to every endpoint path |
| `endpoints` | Yes | Array of endpoint objects |
| `endpoints[].method` | Yes | HTTP method: `GET`, `POST`, `PUT`, `PATCH`, `DELETE` |
| `endpoints[].path` | Yes | Endpoint path, e.g. `/users/{id}` |
| `endpoints[].description` | No | Human-readable description |
| `endpoints[].payload` | No | Sample request body (used for POST, PUT, PATCH) |
| `endpoints[].headers` | No | Default request headers |

---

## Explorer features

- **Sidebar** — all endpoints listed with colour-coded method badges
- **Editable headers & body** — modify payloads directly in the browser before sending
- **Global Auth Token** — enter your `Bearer` token once and it's applied to every request
- **Live execution** — sends real HTTP requests via `fetch()` and displays the response
- **Syntax-highlighted response** — JSON responses are formatted and colour-coded
- **Copy response** — one click to copy the raw response JSON
- **Works offline** — the HTML file has zero external dependencies (only the API calls need a network)

**Method colour coding:**

| Method | Colour |
|---|---|
| GET | Blue |
| POST | Green |
| PUT | Amber |
| PATCH | Purple |
| DELETE | Red |

> **CORS note:** Your backend must return `Access-Control-Allow-Origin: *` (or the current origin) for browser requests to succeed. This is a server-side setting, not a limitation of DocuSync.

---

## Project structure

```
docusync/
├── install.sh                      ← Run this once to install
├── contract.json                   ← Sample contract
├── pom.xml
├── mvnw                            ← Maven wrapper (no Maven install needed)
└── src/main/java/com/docusyn/
    ├── Main.java
    ├── engine/DocuSynEngine.java
    ├── parser/ContractParser.java
    ├── model/Contract.java
    ├── model/Endpoint.java
    └── generator/HtmlGenerator.java
```

---

## Uninstall

```bash
rm ~/bin/docusync.jar
# Then remove the alias line from ~/.zshrc or ~/.bashrc
```

---

## License

MIT
