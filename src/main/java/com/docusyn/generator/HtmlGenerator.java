package com.docusyn.generator;

import com.docusyn.model.Contract;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

public class HtmlGenerator {

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public String generate(Contract contract) throws IOException {
        String contractJson = mapper.writeValueAsString(contract)
                // Prevent the embedded JSON from breaking the <script> block
                .replace("</", "<\\/");

        String projectName = escapeHtml(contract.projectName);

        return TEMPLATE
                .replace("{{PROJECT_NAME}}", projectName)
                .replace("{{CONTRACT_JSON}}", contractJson);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // -------------------------------------------------------------------------
    // Single-file HTML template — CSS and JS are fully inlined.
    // Placeholders: {{PROJECT_NAME}}  {{CONTRACT_JSON}}
    // -------------------------------------------------------------------------
    private static final String TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>{{PROJECT_NAME}} — API Explorer</title>
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    :root {
      --bg:             #f1f5f9;
      --surface:        #ffffff;
      --border:         #e2e8f0;
      --text:           #1e293b;
      --text-muted:     #64748b;
      --sidebar-bg:     #0d1424;
      --sidebar-hover:  #1a2438;
      --topbar-bg:      #1a2438;
      --font-ui:   -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      --font-mono: 'Fira Code', 'Cascadia Code', 'Consolas', 'Courier New', monospace;
      --radius: 10px;
      --shadow: 0 1px 3px rgba(0,0,0,0.07), 0 1px 2px rgba(0,0,0,0.05);
      --shadow-md: 0 4px 12px rgba(0,0,0,0.08), 0 2px 4px rgba(0,0,0,0.05);
    }

    body {
      font-family: var(--font-ui);
      background: var(--bg);
      color: var(--text);
      height: 100vh;
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }

    /* ── Topbar ──────────────────────────────────────────────────────────── */
    .topbar {
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      padding: 0 24px;
      height: 58px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      flex-shrink: 0;
      border-bottom: 1px solid rgba(255,255,255,0.06);
      box-shadow: 0 2px 8px rgba(0,0,0,0.25);
    }

    .topbar-left  { display: flex; align-items: center; gap: 16px; min-width: 0; }
    .topbar-right { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }

    .logo {
      font-size: 0.95rem;
      font-weight: 800;
      color: #60a5fa;
      letter-spacing: -0.5px;
      display: flex;
      align-items: center;
      gap: 7px;
      flex-shrink: 0;
    }

    .divider { width: 1px; height: 22px; background: rgba(255,255,255,0.1); flex-shrink: 0; }

    .project-name {
      font-size: 0.88rem;
      font-weight: 600;
      color: #f1f5f9;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .base-path {
      font-family: var(--font-mono);
      font-size: 0.7rem;
      color: #64748b;
      background: rgba(0,0,0,0.25);
      padding: 3px 9px;
      border-radius: 5px;
      border: 1px solid rgba(255,255,255,0.07);
      white-space: nowrap;
    }

    .ep-count-chips {
      display: flex;
      gap: 5px;
      flex-shrink: 0;
    }

    .ep-count-chip {
      font-family: var(--font-mono);
      font-size: 0.58rem;
      font-weight: 700;
      padding: 2px 7px;
      border-radius: 20px;
      opacity: 0.85;
    }

    .auth-label { font-size: 0.7rem; color: #64748b; white-space: nowrap; }

    .auth-wrap {
      position: relative;
      display: flex;
      align-items: center;
    }

    .auth-wrap svg {
      position: absolute;
      left: 9px;
      color: #475569;
      pointer-events: none;
    }

    .auth-input {
      font-family: var(--font-mono);
      font-size: 0.7rem;
      background: rgba(0,0,0,0.3);
      border: 1px solid rgba(255,255,255,0.08);
      color: #e2e8f0;
      border-radius: 7px;
      padding: 7px 10px 7px 30px;
      width: 260px;
      outline: none;
      transition: border-color 0.15s, background 0.15s;
    }

    .auth-input:focus {
      border-color: #3b82f6;
      background: rgba(59,130,246,0.08);
    }

    .auth-input::placeholder { color: #334155; }

    /* ── Layout ──────────────────────────────────────────────────────────── */
    .layout { display: flex; flex: 1; overflow: hidden; }

    /* ── Sidebar ─────────────────────────────────────────────────────────── */
    .sidebar {
      width: 288px;
      background: var(--sidebar-bg);
      flex-shrink: 0;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      border-right: 1px solid rgba(255,255,255,0.05);
    }

    /* Search */
    .sidebar-search {
      padding: 12px 12px 8px;
      flex-shrink: 0;
    }

    .search-wrap {
      position: relative;
      display: flex;
      align-items: center;
    }

    .search-wrap svg {
      position: absolute;
      left: 9px;
      color: #475569;
      pointer-events: none;
    }

    .search-input {
      width: 100%;
      background: rgba(255,255,255,0.05);
      border: 1px solid rgba(255,255,255,0.07);
      color: #cbd5e1;
      font-family: var(--font-ui);
      font-size: 0.78rem;
      border-radius: 7px;
      padding: 7px 10px 7px 30px;
      outline: none;
      transition: border-color 0.15s, background 0.15s;
    }

    .search-input:focus {
      border-color: #3b82f6;
      background: rgba(59,130,246,0.07);
    }

    .search-input::placeholder { color: #334155; }

    /* Method filter pills */
    .filter-row {
      display: flex;
      gap: 4px;
      padding: 0 12px 10px;
      flex-wrap: wrap;
      flex-shrink: 0;
    }

    .filter-pill {
      font-family: var(--font-mono);
      font-size: 0.57rem;
      font-weight: 700;
      padding: 2px 7px;
      border-radius: 20px;
      border: 1px solid transparent;
      cursor: pointer;
      background: rgba(255,255,255,0.05);
      color: #64748b;
      transition: all 0.12s;
      letter-spacing: 0.3px;
    }

    .filter-pill:hover { background: rgba(255,255,255,0.1); color: #94a3b8; }

    .filter-pill.active {
      color: #fff;
      border-color: transparent;
    }

    /* Section label */
    .sidebar-label {
      padding: 4px 14px 6px;
      font-size: 0.58rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 1.8px;
      color: #334155;
      flex-shrink: 0;
      border-top: 1px solid rgba(255,255,255,0.04);
    }

    /* Endpoint list */
    .endpoint-list {
      list-style: none;
      overflow-y: auto;
      flex: 1;
      padding-bottom: 16px;
    }

    .endpoint-list::-webkit-scrollbar { width: 3px; }
    .endpoint-list::-webkit-scrollbar-track { background: transparent; }
    .endpoint-list::-webkit-scrollbar-thumb { background: #1e293b; border-radius: 2px; }

    .sidebar-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 14px;
      cursor: pointer;
      border-left: 3px solid transparent;
      transition: background 0.12s, border-color 0.12s;
      user-select: none;
    }

    .sidebar-item:hover { background: var(--sidebar-hover); }

    .sidebar-item.active {
      background: rgba(59, 130, 246, 0.1);
      border-left-color: #3b82f6;
    }

    .sidebar-item-text { flex: 1; min-width: 0; }

    .ep-path {
      font-family: var(--font-mono);
      font-size: 0.74rem;
      color: #64748b;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      display: block;
    }

    .ep-desc-preview {
      font-size: 0.65rem;
      color: #334155;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      display: block;
      margin-top: 1px;
    }

    .sidebar-item.active .ep-path { color: #e2e8f0; }
    .sidebar-item.active .ep-desc-preview { color: #64748b; }

    .no-results {
      padding: 20px 16px;
      font-size: 0.78rem;
      color: #334155;
      text-align: center;
    }

    /* ── Method badges ───────────────────────────────────────────────────── */
    .badge {
      font-family: var(--font-mono);
      font-weight: 700;
      border-radius: 5px;
      letter-spacing: 0.3px;
      flex-shrink: 0;
      text-align: center;
    }

    .badge-sm  { font-size: 0.56rem; padding: 2px 5px; min-width: 48px; }
    .badge-lg  { font-size: 0.8rem;  padding: 5px 14px; border-radius: 7px; }

    /* ── Main content ────────────────────────────────────────────────────── */
    .main {
      flex: 1;
      overflow-y: auto;
      padding: 32px 40px 48px;
      background: var(--bg);
    }

    .main::-webkit-scrollbar { width: 6px; }
    .main::-webkit-scrollbar-track { background: transparent; }
    .main::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }

    .detail {
      max-width: 800px;
      margin: 0 auto;
      animation: fadeUp 0.18s ease both;
    }

    @keyframes fadeUp {
      from { opacity: 0; transform: translateY(6px); }
      to   { opacity: 1; transform: translateY(0); }
    }

    /* ── Endpoint header ─────────────────────────────────────────────────── */
    .ep-header {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
      margin-bottom: 8px;
    }

    .ep-full-url {
      font-family: var(--font-mono);
      font-size: 0.92rem;
      color: var(--text);
      word-break: break-all;
      flex: 1;
    }

    .ep-desc {
      font-size: 0.86rem;
      color: var(--text-muted);
      line-height: 1.7;
      margin-bottom: 18px;
    }

    /* ── Cards ───────────────────────────────────────────────────────────── */
    .card {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      margin-bottom: 14px;
      overflow: hidden;
      box-shadow: var(--shadow);
    }

    .card-header {
      padding: 9px 16px;
      border-bottom: 1px solid var(--border);
      background: #f8fafc;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .card-title {
      font-size: 0.66rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 1.2px;
      color: #94a3b8;
    }

    .card-hint {
      font-size: 0.65rem;
      color: #cbd5e1;
    }

    /* ── Code editors ────────────────────────────────────────────────────── */
    .code-editor {
      width: 100%;
      background: #0d1424;
      color: #cbd5e1;
      font-family: var(--font-mono);
      font-size: 0.79rem;
      line-height: 1.7;
      border: none;
      padding: 14px 16px;
      resize: vertical;
      outline: none;
      min-height: 80px;
      display: block;
      transition: background 0.15s;
    }

    .code-editor:focus { background: #0f172a; }

    /* ── CORS note ───────────────────────────────────────────────────────── */
    .cors-note {
      background: #fefce8;
      border: 1px solid #fde68a;
      border-radius: var(--radius);
      padding: 10px 14px;
      font-size: 0.75rem;
      color: #78350f;
      margin-bottom: 14px;
      line-height: 1.6;
      display: flex;
      gap: 9px;
      align-items: flex-start;
    }

    .cors-note code {
      font-family: var(--font-mono);
      font-size: 0.72rem;
      background: #fef9c3;
      padding: 1px 5px;
      border-radius: 3px;
    }

    /* ── Execute row ─────────────────────────────────────────────────────── */
    .execute-row {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 18px;
      flex-wrap: wrap;
    }

    .execute-btn {
      background: var(--method-color, #3b82f6);
      color: #fff;
      border: none;
      border-radius: var(--radius);
      padding: 10px 22px;
      font-family: var(--font-ui);
      font-size: 0.84rem;
      font-weight: 600;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: opacity 0.15s, transform 0.1s, box-shadow 0.15s;
      box-shadow: 0 2px 6px rgba(0,0,0,0.15);
    }

    .execute-btn:hover:not(:disabled) {
      opacity: 0.88;
      transform: translateY(-1px);
      box-shadow: var(--shadow-md);
    }

    .execute-btn:active:not(:disabled) { transform: translateY(0); }
    .execute-btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .curl-btn {
      background: transparent;
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 9px 16px;
      font-size: 0.78rem;
      color: var(--text-muted);
      cursor: pointer;
      font-family: var(--font-ui);
      display: inline-flex;
      align-items: center;
      gap: 6px;
      transition: background 0.12s, color 0.12s, border-color 0.12s;
    }

    .curl-btn:hover {
      background: var(--surface);
      color: var(--text);
      border-color: #94a3b8;
      box-shadow: var(--shadow);
    }

    .shortcut-hint {
      font-size: 0.68rem;
      color: #94a3b8;
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .kbd {
      background: #e2e8f0;
      border: 1px solid #cbd5e1;
      border-bottom-width: 2px;
      border-radius: 4px;
      padding: 1px 5px;
      font-family: var(--font-mono);
      font-size: 0.65rem;
      color: #475569;
    }

    /* ── Response ────────────────────────────────────────────────────────── */
    .response-meta {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 9px 14px;
      border-bottom: 1px solid var(--border);
      background: #f8fafc;
      min-height: 42px;
      flex-wrap: wrap;
    }

    .status-pill {
      font-family: var(--font-mono);
      font-size: 0.73rem;
      font-weight: 700;
      padding: 3px 11px;
      border-radius: 20px;
      letter-spacing: 0.3px;
    }

    .s2xx { background: #dcfce7; color: #15803d; }
    .s3xx { background: #dbeafe; color: #1d4ed8; }
    .s4xx { background: #fef9c3; color: #b45309; }
    .s5xx { background: #fee2e2; color: #b91c1c; }
    .s-err { background: #fee2e2; color: #b91c1c; }

    .resp-stat {
      font-family: var(--font-mono);
      font-size: 0.7rem;
      color: var(--text-muted);
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .resp-stat-sep { color: #e2e8f0; }

    .meta-actions { margin-left: auto; display: flex; gap: 6px; }

    .meta-btn {
      background: transparent;
      border: 1px solid var(--border);
      border-radius: 6px;
      padding: 3px 10px;
      font-size: 0.7rem;
      color: var(--text-muted);
      cursor: pointer;
      font-family: var(--font-ui);
      transition: background 0.1s, color 0.1s;
    }

    .meta-btn:hover { background: #f1f5f9; color: var(--text); }

    .response-body {
      background: #0d1424;
      color: #cbd5e1;
      font-family: var(--font-mono);
      font-size: 0.79rem;
      line-height: 1.7;
      padding: 16px 18px;
      overflow: auto;
      max-height: 480px;
      white-space: pre;
    }

    .response-body::-webkit-scrollbar { width: 6px; height: 6px; }
    .response-body::-webkit-scrollbar-track { background: #0d1424; }
    .response-body::-webkit-scrollbar-thumb { background: #1e293b; border-radius: 3px; }

    /* ── JSON colours ────────────────────────────────────────────────────── */
    .jk { color: #7dd3fc; }
    .js { color: #86efac; }
    .jn { color: #fca5a5; }
    .jb { color: #c4b5fd; }
    .jz { color: #475569; }
    .jc { color: #334155; font-style: italic; }

    /* ── Spinner ─────────────────────────────────────────────────────────── */
    .spinner {
      display: inline-block;
      width: 13px; height: 13px;
      border: 2px solid rgba(255,255,255,0.3);
      border-top-color: #fff;
      border-radius: 50%;
      animation: spin 0.5s linear infinite;
    }

    @keyframes spin { to { transform: rotate(360deg); } }

    /* ── Welcome / empty ─────────────────────────────────────────────────── */
    .welcome {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      gap: 12px;
      color: var(--text-muted);
      text-align: center;
    }

    .welcome-icon {
      width: 48px; height: 48px;
      background: #f1f5f9;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 4px;
    }

    .welcome h2 { font-size: 1rem; color: var(--text); font-weight: 600; }
    .welcome p  { font-size: 0.82rem; max-width: 280px; line-height: 1.65; }
  </style>
</head>
<body>
<div id="root"></div>

<script>
const CONTRACT = {{CONTRACT_JSON}};

// ── Constants ─────────────────────────────────────────────────────────────────
const METHOD_STYLES = {
  GET:     { bg: '#3b82f6', text: '#fff' },
  POST:    { bg: '#22c55e', text: '#fff' },
  PUT:     { bg: '#f59e0b', text: '#fff' },
  PATCH:   { bg: '#8b5cf6', text: '#fff' },
  DELETE:  { bg: '#ef4444', text: '#fff' },
  HEAD:    { bg: '#14b8a6', text: '#fff' },
  OPTIONS: { bg: '#64748b', text: '#fff' },
};

function methodStyle(m) {
  return METHOD_STYLES[(m || '').toUpperCase()] || { bg: '#64748b', text: '#fff' };
}

const BODY_METHODS = new Set(['POST', 'PUT', 'PATCH']);

// ── State ─────────────────────────────────────────────────────────────────────
let activeIdx    = 0;
let activeFilter = 'ALL';
let searchQuery  = '';

// ── Utilities ─────────────────────────────────────────────────────────────────
function h(s) {
  if (s == null) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;')
                  .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function prettyJson(value) {
  try {
    const p = typeof value === 'string' ? JSON.parse(value) : value;
    return JSON.stringify(p, null, 2);
  } catch { return typeof value === 'string' ? value : JSON.stringify(value, null, 2); }
}

function syntaxHighlight(str) {
  const esc = str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
  return esc.replace(
    /("(\\\\u[a-fA-F0-9]{4}|\\\\[^u]|[^\\\\"])*"(\\s*:)?|\\b(true|false|null)\\b|-?\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?)/g,
    m => {
      if (/^"/.test(m)) return /:$/.test(m) ? `<span class="jk">${m}</span>` : `<span class="js">${m}</span>`;
      if (/true|false/.test(m)) return `<span class="jb">${m}</span>`;
      if (/null/.test(m))       return `<span class="jz">${m}</span>`;
      return `<span class="jn">${m}</span>`;
    }
  );
}

function statusClass(code) {
  if (code >= 200 && code < 300) return 's2xx';
  if (code >= 300 && code < 400) return 's3xx';
  if (code >= 400 && code < 500) return 's4xx';
  if (code >= 500)               return 's5xx';
  return 's-err';
}

function formatBytes(n) {
  if (n < 1024) return n + ' B';
  return (n / 1024).toFixed(1) + ' KB';
}

// Build method count chips for the topbar
function buildMethodChips() {
  const counts = {};
  CONTRACT.endpoints.forEach(ep => {
    const m = (ep.method || '').toUpperCase();
    counts[m] = (counts[m] || 0) + 1;
  });
  return Object.entries(counts).map(([m, n]) => {
    const s = methodStyle(m);
    return `<span class="ep-count-chip" style="background:${s.bg}22;color:${s.bg};border:1px solid ${s.bg}44">${n} ${m}</span>`;
  }).join('');
}

// Build filter pills for the sidebar
function buildFilterPills() {
  const methods = [...new Set(CONTRACT.endpoints.map(e => (e.method||'').toUpperCase()))];
  const all = `<button class="filter-pill${activeFilter==='ALL'?' active':''}" style="${activeFilter==='ALL'?'background:#3b82f6;color:#fff':''}" data-method="ALL">All</button>`;
  const pills = methods.map(m => {
    const s = methodStyle(m);
    const isActive = activeFilter === m;
    const style = isActive ? `background:${s.bg};color:${s.text}` : '';
    return `<button class="filter-pill${isActive?' active':''}" style="${style}" data-method="${m}">${m}</button>`;
  });
  return all + pills.join('');
}

// ── Sidebar item ──────────────────────────────────────────────────────────────
function renderSidebarItem(ep, idx) {
  const s = methodStyle(ep.method);
  const active = idx === activeIdx ? ' active' : '';
  const desc = ep.description ? `<span class="ep-desc-preview">${h(ep.description)}</span>` : '';
  return `
    <li class="sidebar-item${active}" data-idx="${idx}" title="${h(ep.description || '')}">
      <span class="badge badge-sm" style="background:${s.bg};color:${s.text}">${h(ep.method)}</span>
      <span class="sidebar-item-text">
        <span class="ep-path">${h(ep.path)}</span>
        ${desc}
      </span>
    </li>`;
}

// ── Endpoint detail ───────────────────────────────────────────────────────────
function renderDetail(ep, idx) {
  const s        = methodStyle(ep.method);
  const hasBody  = BODY_METHODS.has((ep.method || '').toUpperCase());
  const fullUrl  = (CONTRACT.basePath || '') + (ep.path || '');
  const hdrVal   = ep.headers ? prettyJson(ep.headers) : '{}';
  const bodyVal  = ep.payload ? prettyJson(ep.payload) : '{}';
  const isMac    = /Mac|iPod|iPhone|iPad/.test(navigator.platform);
  const shortcut = isMac ? '⌘ Return' : 'Ctrl + Enter';

  return `
    <div class="detail">
      <div class="ep-header">
        <span class="badge badge-lg" style="background:${s.bg};color:${s.text}">${h(ep.method)}</span>
        <span class="ep-full-url">${h(fullUrl)}</span>
      </div>
      ${ep.description ? `<p class="ep-desc">${h(ep.description)}</p>` : ''}

      <div class="cors-note">
        <span style="flex-shrink:0;margin-top:1px">⚠️</span>
        <span><strong>CORS Note:</strong> The server at <code>${h(CONTRACT.basePath)}</code> must return
        <code>Access-Control-Allow-Origin: *</code>. If requests fail instantly, this is a
        server-side CORS configuration issue — not a bug in this tool.</span>
      </div>

      <div class="card">
        <div class="card-header">
          <span class="card-title">Request Headers</span>
          <span class="card-hint">JSON object</span>
        </div>
        <textarea class="code-editor" id="hdr-${idx}" rows="5" spellcheck="false">${h(hdrVal)}</textarea>
      </div>

      ${hasBody ? `
      <div class="card">
        <div class="card-header">
          <span class="card-title">Request Body</span>
          <span class="card-hint">JSON</span>
        </div>
        <textarea class="code-editor" id="body-${idx}" rows="9" spellcheck="false">${h(bodyVal)}</textarea>
      </div>` : ''}

      <div class="execute-row">
        <button class="execute-btn" id="exec-btn"
                style="--method-color:${s.bg}"
                onclick="executeRequest(${idx})">
          <svg width="11" height="11" viewBox="0 0 12 12" fill="currentColor"><path d="M2 1l9 5-9 5V1z"/></svg>
          Send Request
        </button>
        <button class="curl-btn" onclick="copyCurl(${idx})">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
          </svg>
          Copy as cURL
        </button>
        <span class="shortcut-hint">
          <span class="kbd">${shortcut}</span> to send
        </span>
      </div>

      <div class="card" id="resp-card-${idx}" style="display:none">
        <div class="response-meta" id="resp-meta-${idx}"></div>
        <div class="response-body" id="resp-body-${idx}"></div>
      </div>
    </div>`;
}

// ── Full render ───────────────────────────────────────────────────────────────
function render() {
  const ep = CONTRACT.endpoints[activeIdx];
  document.getElementById('root').innerHTML = `
    <div class="topbar">
      <div class="topbar-left">
        <div class="logo">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
               stroke="#60a5fa" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/>
          </svg>
          DocuSync
        </div>
        <div class="divider"></div>
        <div class="project-name">${h(CONTRACT.projectName)}</div>
        <div class="base-path">${h(CONTRACT.basePath)}</div>
        <div class="ep-count-chips">${buildMethodChips()}</div>
      </div>
      <div class="topbar-right">
        <span class="auth-label">Auth Token</span>
        <div class="auth-wrap">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor"
               stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          <input class="auth-input" id="auth-token" type="text"
                 placeholder="Bearer your-token-here"
                 value="${h(window.__authToken || '')}" />
        </div>
      </div>
    </div>

    <div class="layout">
      <nav class="sidebar">
        <div class="sidebar-search">
          <div class="search-wrap">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <input class="search-input" id="search-input" type="text"
                   placeholder="Search endpoints…" value="${h(searchQuery)}" />
          </div>
        </div>
        <div class="filter-row" id="filter-row">${buildFilterPills()}</div>
        <div class="sidebar-label">Endpoints · ${CONTRACT.endpoints.length}</div>
        <ul class="endpoint-list" id="ep-list">
          ${CONTRACT.endpoints.map((e, i) => renderSidebarItem(e, i)).join('')}
        </ul>
      </nav>

      <main class="main">
        ${ep ? renderDetail(ep, activeIdx)
             : `<div class="welcome">
                  <div class="welcome-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#94a3b8"
                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/>
                    </svg>
                  </div>
                  <h2>No endpoints</h2>
                  <p>Add endpoints to your contract.json and re-run docusync.</p>
                </div>`}
      </main>
    </div>`;

  attachListeners();
  applyFilter();
}

function attachListeners() {
  // Sidebar clicks
  document.querySelectorAll('.sidebar-item').forEach(el => {
    el.addEventListener('click', () => selectEndpoint(+el.dataset.idx));
  });

  // Auth input persistence
  const authEl = document.getElementById('auth-token');
  if (authEl) authEl.addEventListener('input', () => { window.__authToken = authEl.value; });

  // Search
  const searchEl = document.getElementById('search-input');
  if (searchEl) {
    searchEl.focus();
    searchEl.addEventListener('input', e => {
      searchQuery = e.target.value.toLowerCase();
      applyFilter();
    });
  }

  // Method filter pills
  document.querySelectorAll('.filter-pill').forEach(pill => {
    pill.addEventListener('click', () => {
      activeFilter = pill.dataset.method;
      document.querySelectorAll('.filter-pill').forEach(p => {
        const m = p.dataset.method;
        const s = m === 'ALL' ? { bg: '#3b82f6' } : methodStyle(m);
        const on = m === activeFilter;
        p.className = 'filter-pill' + (on ? ' active' : '');
        p.style.cssText = on ? `background:${s.bg};color:#fff` : '';
      });
      applyFilter();
    });
  });
}

// Show/hide sidebar items based on search + method filter (no re-render)
function applyFilter() {
  let visible = 0;
  document.querySelectorAll('.sidebar-item').forEach(el => {
    const ep = CONTRACT.endpoints[+el.dataset.idx];
    const matchMethod = activeFilter === 'ALL' || (ep.method||'').toUpperCase() === activeFilter;
    const matchSearch = !searchQuery
      || ep.path.toLowerCase().includes(searchQuery)
      || (ep.method||'').toLowerCase().includes(searchQuery)
      || (ep.description||'').toLowerCase().includes(searchQuery);
    const show = matchMethod && matchSearch;
    el.style.display = show ? '' : 'none';
    if (show) visible++;
  });

  const list = document.getElementById('ep-list');
  let noRes = list.querySelector('.no-results');
  if (visible === 0) {
    if (!noRes) {
      noRes = document.createElement('li');
      noRes.className = 'no-results';
      noRes.textContent = 'No endpoints match your search.';
      list.appendChild(noRes);
    }
  } else if (noRes) {
    noRes.remove();
  }
}

function selectEndpoint(idx) {
  window.__authToken = document.getElementById('auth-token')?.value || '';
  searchQuery = document.getElementById('search-input')?.value?.toLowerCase() || '';
  activeIdx = idx;
  render();
  document.querySelector('.sidebar-item.active')?.scrollIntoView({ block: 'nearest' });
}

// ── Execute ───────────────────────────────────────────────────────────────────
async function executeRequest(idx) {
  const ep        = CONTRACT.endpoints[idx];
  const authToken = (document.getElementById('auth-token')?.value || '').trim();
  const url       = (CONTRACT.basePath || '') + (ep.path || '');

  let headers = {};
  try { headers = JSON.parse(document.getElementById(`hdr-${idx}`)?.value || '{}'); } catch {}
  if (authToken) headers['Authorization'] = authToken;

  let body;
  if (BODY_METHODS.has((ep.method||'').toUpperCase())) {
    body = document.getElementById(`body-${idx}`)?.value;
  }

  const btn      = document.getElementById('exec-btn');
  const respCard = document.getElementById(`resp-card-${idx}`);
  const respMeta = document.getElementById(`resp-meta-${idx}`);
  const respBody = document.getElementById(`resp-body-${idx}`);

  btn.disabled = true;
  btn.innerHTML = `<span class="spinner"></span> Sending…`;
  respCard.style.display = 'block';
  respMeta.innerHTML = `<span class="resp-stat" style="color:#64748b">Waiting for response…</span>`;
  respBody.innerHTML = '';

  const t0 = Date.now();

  try {
    const opts = { method: ep.method.toUpperCase(), headers };
    if (body !== undefined) opts.body = body;

    const res  = await fetch(url, opts);
    const ms   = Date.now() - t0;
    const text = await res.text();
    const size = new TextEncoder().encode(text).length;

    let displayHtml;
    try {
      displayHtml = syntaxHighlight(JSON.stringify(JSON.parse(text), null, 2));
    } catch {
      displayHtml = h(text) || `<span class="jc">// (empty body)</span>`;
    }

    respMeta.innerHTML = `
      <span class="status-pill ${statusClass(res.status)}">${res.status} ${h(res.statusText)}</span>
      <span class="resp-stat">
        <span>${ms} ms</span>
        <span class="resp-stat-sep">·</span>
        <span>${formatBytes(size)}</span>
      </span>
      <div class="meta-actions">
        <button class="meta-btn" onclick="copyResponse(${idx})">Copy</button>
      </div>`;
    respBody.innerHTML = displayHtml;

  } catch (err) {
    const ms = Date.now() - t0;
    respMeta.innerHTML = `
      <span class="status-pill s-err">Network Error</span>
      <span class="resp-stat">${ms} ms</span>`;
    respBody.innerHTML =
      `<span class="js">"${h(err.message)}"</span>\n\n` +
      `<span class="jc">// Could not reach ${h(url)}\n` +
      `// 1. Is the server running?\n` +
      `// 2. Does it allow CORS from this origin?\n` +
      `// Open DevTools → Console for the full stack trace.</span>`;
  }

  btn.disabled = false;
  btn.innerHTML = `<svg width="11" height="11" viewBox="0 0 12 12" fill="currentColor"><path d="M2 1l9 5-9 5V1z"/></svg> Send Request`;
}

// ── cURL builder ──────────────────────────────────────────────────────────────
function copyCurl(idx) {
  const ep        = CONTRACT.endpoints[idx];
  const authToken = (document.getElementById('auth-token')?.value || '').trim();
  const url       = (CONTRACT.basePath || '') + (ep.path || '');

  let headers = {};
  try { headers = JSON.parse(document.getElementById(`hdr-${idx}`)?.value || '{}'); } catch {}
  if (authToken) headers['Authorization'] = authToken;

  let cmd = `curl -X ${ep.method.toUpperCase()}`;
  Object.entries(headers).forEach(([k, v]) => { cmd += ` \\\\\\n  -H "${k}: ${v}"`; });

  if (BODY_METHODS.has((ep.method||'').toUpperCase())) {
    const body = document.getElementById(`body-${idx}`)?.value || '';
    if (body.trim()) cmd += ` \\\\\\n  -d '${body.replace(/'/g, "'\\\\''")}' `;
  }

  cmd += ` \\\\\\n  "${url}"`;

  navigator.clipboard.writeText(cmd).then(() => {
    const btn = document.querySelector('.curl-btn');
    if (btn) {
      const orig = btn.innerHTML;
      btn.textContent = 'Copied!';
      setTimeout(() => { btn.innerHTML = orig; }, 1800);
    }
  });
}

// ── Copy response ─────────────────────────────────────────────────────────────
function copyResponse(idx) {
  const text = document.getElementById(`resp-body-${idx}`)?.innerText || '';
  navigator.clipboard.writeText(text).then(() => {
    const btn = document.querySelector('.meta-btn');
    if (btn) { btn.textContent = 'Copied!'; setTimeout(() => { btn.textContent = 'Copy'; }, 1500); }
  });
}

// ── Keyboard shortcut: Cmd/Ctrl + Enter ───────────────────────────────────────
document.addEventListener('keydown', e => {
  if ((e.metaKey || e.ctrlKey) && (e.key === 'Enter')) {
    e.preventDefault();
    document.getElementById('exec-btn')?.click();
  }
});

// ── Boot ──────────────────────────────────────────────────────────────────────
render();
</script>
</body>
</html>
""";
}
