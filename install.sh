#!/usr/bin/env bash
set -e

# ── Colours ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

info()    { echo -e "${CYAN}${BOLD}[docusync]${RESET} $*"; }
success() { echo -e "${GREEN}${BOLD}[docusync]${RESET} $*"; }
warn()    { echo -e "${YELLOW}${BOLD}[docusync]${RESET} $*"; }
error()   { echo -e "${RED}${BOLD}[docusync] ERROR:${RESET} $*"; exit 1; }

echo ""
echo -e "${BOLD}  DocuSync Installer${RESET}"
echo    "  ───────────────────────────────────────────"
echo ""

# ── 1. Check Java ─────────────────────────────────────────────────────────────
if ! command -v java &>/dev/null; then
  error "Java is not installed. Install Java 17+ from https://adoptium.net and re-run this script."
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_VERSION" -lt 17 ] 2>/dev/null; then
  error "Java 17 or higher is required (found Java $JAVA_VERSION). Upgrade at https://adoptium.net"
fi

info "Java $JAVA_VERSION found ✓"

# ── 2. Build the JAR ─────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Prefer Maven wrapper if present, otherwise fall back to system mvn
if [ -f "$SCRIPT_DIR/mvnw" ]; then
  MVN="$SCRIPT_DIR/mvnw"
elif command -v mvn &>/dev/null; then
  MVN="mvn"
else
  error "Maven is not installed and no Maven wrapper found.\nInstall Maven from https://maven.apache.org/download.cgi or re-clone the repo."
fi

info "Building JAR..."
"$MVN" -f "$SCRIPT_DIR/pom.xml" package -q -DskipTests
success "JAR built ✓"

# ── 3. Install JAR ───────────────────────────────────────────────────────────
INSTALL_DIR="$HOME/bin"
mkdir -p "$INSTALL_DIR"
cp "$SCRIPT_DIR/target/docusync-1.0.0.jar" "$INSTALL_DIR/docusync.jar"
success "JAR installed → $INSTALL_DIR/docusync.jar ✓"

# ── 4. Add shell alias ────────────────────────────────────────────────────────
ALIAS_LINE='alias docusync="java -jar ~/bin/docusync.jar"'
ALIAS_COMMENT="# DocuSync — API Explorer generator"
ADDED=false

add_to_file() {
  local FILE="$1"
  if [ -f "$FILE" ]; then
    if grep -q "docusync" "$FILE" 2>/dev/null; then
      warn "Alias already exists in $FILE — skipping"
    else
      echo "" >> "$FILE"
      echo "$ALIAS_COMMENT" >> "$FILE"
      echo "$ALIAS_LINE" >> "$FILE"
      success "Alias added to $FILE ✓"
      ADDED=true
    fi
  fi
}

# Detect current shell and target the right config file
CURRENT_SHELL="$(basename "$SHELL")"
case "$CURRENT_SHELL" in
  zsh)  add_to_file "$HOME/.zshrc" ;;
  bash) add_to_file "$HOME/.bashrc"; add_to_file "$HOME/.bash_profile" ;;
  *)
    # Unknown shell — try common files
    add_to_file "$HOME/.zshrc"
    add_to_file "$HOME/.bashrc"
    ;;
esac

# ── 5. Done ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}${BOLD}  Installation complete!${RESET}"
echo    "  ───────────────────────────────────────────"
echo ""
echo    "  Open a new terminal, then run:"
echo ""
echo -e "  ${BOLD}  docusync contract.json${RESET}"
echo ""
echo    "  The tool reads your contract.json and writes API_EXPLORER.html"
echo    "  Open that file in any browser — no server needed."
echo ""
