/* eslint-disable no-console */
const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");
const { getProjectMeta, ensureItemInProject, setSingleSelectField } = require("./projectv2");

/* =========================================================
 * ENV
 * ======================================================= */

const [OWNER, REPO] = (process.env.GITHUB_REPOSITORY || "").split("/");

const GH_TOKEN = process.env.GITHUB_TOKEN;
const PROJECT_TOKEN = process.env.PROJECT_TOKEN;

const DEFAULT_BRANCH = process.env.DEFAULT_BRANCH || "main";
const MODE = (process.env.TICKETS_MODE || process.argv[2] || "sync").toLowerCase(); // sync | merge
const SYNC_MODE = (process.env.SYNC_MODE || "changed").toLowerCase(); // changed | full

const PR_NUMBER = process.env.PR_NUMBER;

const PROJECT_KIND = process.env.PROJECT_KIND || "user"; // user | organization
const PROJECT_OWNER = process.env.PROJECT_OWNER;
const PROJECT_NUMBER = process.env.PROJECT_NUMBER;

const FIELD_STATUS = process.env.PROJECT_FIELD_STATUS || "Status";
const FIELD_AREA = process.env.PROJECT_FIELD_AREA || "Area";
const STATUS_MERGED = process.env.PROJECT_STATUS_MERGED || "Merged";

const WATCH_ROOTS = [
    "work/bugs/",
    "work/features/",
    "work/tasks/",
];

if (!OWNER || !REPO) {
    console.error("Missing GITHUB_REPOSITORY");
    process.exit(1);
}
if (!GH_TOKEN) {
    console.error("Missing GITHUB_TOKEN");
    process.exit(1);
}
if (MODE === "merge" && SYNC_MODE !== "full" && !PR_NUMBER) {
    console.error("Missing PR_NUMBER for merge mode");
    process.exit(1);
}

/* =========================================================
 * HELPERS
 * ======================================================= */

function sh(cmd) {
    return execSync(cmd, { stdio: ["ignore", "pipe", "pipe"] })
        .toString("utf8")
        .trim();
}

async function ghRest(pathname, { method = "GET", body, token = GH_TOKEN } = {}) {
    const url = `https://api.github.com${pathname}`;
    const headers = {
        Accept: "application/vnd.github+json",
        Authorization: `Bearer ${token}`,
        "X-GitHub-Api-Version": "2022-11-28",
    };

    const r = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await r.text();
    if (!r.ok) {
        console.error(text);
        throw new Error(`GitHub REST ${method} ${pathname} failed: ${r.status}`);
    }

    return text ? JSON.parse(text) : null;
}

async function ensureLabel(name, color = "ededed") {
    try {
        await ghRest(`/repos/${OWNER}/${REPO}/labels/${encodeURIComponent(name)}`, { method: "GET" });
    } catch {
        await ghRest(`/repos/${OWNER}/${REPO}/labels`, {
            method: "POST",
            body: { name, color },
        });
    }
}

function isWatchedPath(p) {
    return WATCH_ROOTS.some(prefix => p.startsWith(prefix));
}

/* =========================================================
 * DOMAIN
 * ======================================================= */

const AREAS = [
    { prefix: "work/bugs/", areaLabel: "area:bugs", areaValue: "bugs" },
    { prefix: "work/features/", areaLabel: "area:features", areaValue: "features" },
    { prefix: "work/tasks/", areaLabel: "area:tasks", areaValue: "tasks" },
];

function parseTicket(filePath) {
    const text = fs.readFileSync(filePath, "utf8");

    const h1 = text.match(/^#\s+(.+)$/m)?.[1]?.trim();
    if (!h1) return null;

    const id =
        path.basename(filePath).match(/^([A-Z]+-\d+)/)?.[1] ||
        h1.match(/^([A-Z]+-\d+)/)?.[1];

    if (!id) return null;

    const status = text.match(/^[*-]\s+Status:\s*(.+)$/mi)?.[1]?.trim() || null;

    const area = AREAS.find(a => filePath.startsWith(a.prefix));

    return {
        id,
        title: h1,
        status,
        areaLabel: area?.areaLabel || null,
        areaValue: area?.areaValue || null,
        filePath,
    };
}

function extractTicketIdFromPathOrFile(filePath) {
    const fromPath = filePath.match(/\/([A-Z]+-\d+)\b/)?.[1];
    if (fromPath) return fromPath;
    if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
        return parseTicket(filePath)?.id || null;
    }
    return null;
}

function setStatusMergedInFile(filePath) {
    const txt = fs.readFileSync(filePath, "utf8");
    // Match "Status: Done" with any casing and optional trailing spaces/comments
    const replaced = txt.replace(/^([\-\*]\s+Status:\s*)Done(\s*)$/mi, "$1Merged$2");
    if (replaced !== txt) {
        fs.writeFileSync(filePath, replaced, "utf8");
        return true;
    }
    return false;
}

async function loadProjectMeta() {
    if (!PROJECT_TOKEN || !PROJECT_OWNER || !PROJECT_NUMBER) return null;
    return getProjectMeta({
        token: PROJECT_TOKEN,
        kind: PROJECT_KIND,
        owner: PROJECT_OWNER,
        number: PROJECT_NUMBER,
        statusFieldName: FIELD_STATUS,
        areaFieldName: FIELD_AREA,
    });
}

async function syncToProject(projectMeta, issueNodeId, statusName, areaName) {
    if (!projectMeta || !PROJECT_TOKEN || !issueNodeId) return;

    const itemId = await ensureItemInProject({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        contentNodeId: issueNodeId,
    });

    if (projectMeta.statusFieldId && statusName) {
        const optId = projectMeta.statusOptions.get(statusName);
        if (optId) {
            await setSingleSelectField({
                token: PROJECT_TOKEN,
                projectId: projectMeta.projectId,
                itemId,
                fieldId: projectMeta.statusFieldId,
                optionId: optId,
            });
        } else {
            console.warn(`[ProjectV2] Status option not found: "${statusName}"`);
        }
    }

    if (projectMeta.areaFieldId && areaName) {
        const optId = projectMeta.areaOptions.get(areaName);
        if (optId) {
            await setSingleSelectField({
                token: PROJECT_TOKEN,
                projectId: projectMeta.projectId,
                itemId,
                fieldId: projectMeta.areaFieldId,
                optionId: optId,
            });
        } else {
            console.warn(`[ProjectV2] Area option not found: "${areaName}"`);
        }
    }
}

async function syncMergedToProject(projectMeta, issueNodeId) {
    if (!projectMeta?.statusFieldId) return;
    const opt = projectMeta.statusOptions.get(STATUS_MERGED);
    if (!opt) {
        console.warn(`[ProjectV2] Status option not found: "${STATUS_MERGED}"`);
        return;
    }
    const itemId = await ensureItemInProject({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        contentNodeId: issueNodeId,
    });
    await setSingleSelectField({
        token: PROJECT_TOKEN,
        projectId: projectMeta.projectId,
        itemId,
        fieldId: projectMeta.statusFieldId,
        optionId: opt,
    });
}

/* =========================================================
 * FILE SELECTION
 * ======================================================= */

function collectSyncFiles() {
    if (SYNC_MODE === "full") {
        console.log("Running FULL sync (bootstrap mode)");
        const out = sh(`find work/bugs work/features work/tasks -type f 2>/dev/null || true`);
        return out ? out.split("\n").filter(Boolean) : [];
    }

    console.log("Running CHANGED sync");
    sh(`git fetch origin ${DEFAULT_BRANCH} --depth=1 || true`);

    const out = sh(`git diff --name-only origin/${DEFAULT_BRANCH}...HEAD || true`);
    const files = out ? out.split("\n").filter(Boolean) : [];

    return files.filter(isWatchedPath);
}

async function collectMergeFiles() {
    if (SYNC_MODE === "full") {
        console.log("Running FULL sync for merge (bootstrap mode)");
        const out = sh(`find work/bugs work/features work/tasks -type f 2>/dev/null || true`);
        const files = out ? out.split("\n").filter(Boolean) : [];
        return files.filter(f => {
            if (!fs.existsSync(f) || fs.statSync(f).isDirectory()) return false;
            const content = fs.readFileSync(f, "utf8");
            return content.includes("Status: Merged") || content.includes("Status: Done");
        });
    }

    const files = [];
    let page = 1;
    while (true) {
        const res = await ghRest(`/repos/${OWNER}/${REPO}/pulls/${PR_NUMBER}/files?per_page=100&page=${page}`);
        if (!Array.isArray(res) || res.length === 0) break;
        for (const f of res) files.push(f.filename);
        page += 1;
    }
    return files.filter(isWatchedPath);
}

/* =========================================================
 * MODES
 * ======================================================= */

async function runSync() {
    const files = collectSyncFiles();
    if (files.length === 0) {
        console.log("No ticket files to process.");
        return;
    }

    for (const base of ["tickets", "area:bugs", "area:features", "area:tasks"]) {
        await ensureLabel(base);
    }

    const projectMeta = await loadProjectMeta();

    for (const file of files) {
        if (!fs.existsSync(file) || fs.statSync(file).isDirectory()) continue;

        const ticket = parseTicket(file);
        if (!ticket) {
            console.log(`Skip (cannot parse): ${file}`);
            continue;
        }

        const ticketLabel = `ticket:${ticket.id}`;
        await ensureLabel(ticketLabel);

        const statusLabel = ticket.status ? `status:${ticket.status}` : null;
        if (statusLabel) await ensureLabel(statusLabel);

        const q = encodeURIComponent(`repo:${OWNER}/${REPO} is:issue label:"${ticketLabel}"`);
        const search = await ghRest(`/search/issues?q=${q}`, { method: "GET" });
        const existing = search.items?.[0] || null;

        const link = `https://github.com/${OWNER}/${REPO}/blob/${DEFAULT_BRANCH}/${ticket.filePath}`;
        const body = `Ticket je veden v repozitáři.\n\n➡ ${link}\n`;

        let issue;

        if (!existing) {
            issue = await ghRest(`/repos/${OWNER}/${REPO}/issues`, {
                method: "POST",
                body: {
                    title: ticket.title,
                    body,
                    labels: [
                        "tickets",
                        ticketLabel,
                        ticket.areaLabel,
                        statusLabel,
                    ].filter(Boolean),
                },
            });

            console.log(`Created issue #${issue.number} (${ticket.id})`);
        } else {
            issue = await ghRest(`/repos/${OWNER}/${REPO}/issues/${existing.number}`, {
                method: "PATCH",
                body: { title: ticket.title, body },
            });

            const oldLabels = (issue.labels || []).map(l => (typeof l === "string" ? l : l.name));
            const kept = oldLabels.filter(l => !l.startsWith("status:"));
            const next = Array.from(new Set([
                ...kept,
                "tickets",
                ticketLabel,
                ...(ticket.areaLabel ? [ticket.areaLabel] : []),
                ...(statusLabel ? [statusLabel] : []),
            ]));

            await ghRest(`/repos/${OWNER}/${REPO}/issues/${issue.number}/labels`, {
                method: "PUT",
                body: next,
            });

            console.log(`Updated issue #${issue.number} (${ticket.id})`);
        }

        const canonical = await ghRest(`/repos/${OWNER}/${REPO}/issues/${issue.number}`, { method: "GET" });
        console.log(`Project sync for ${ticket.id}: issue #${issue.number}, node_id=${canonical?.node_id}`);

        if (projectMeta && canonical?.node_id) {
            await syncToProject(projectMeta, canonical.node_id, ticket.status, ticket.areaValue);
        }
    }
}

async function runMerge() {
    const files = await collectMergeFiles();
    if (files.length === 0) {
        console.log("No ticket files in merged PR.");
        return;
    }

    await ensureLabel("status:Merged");

    const projectMeta = await loadProjectMeta();
    const touchedForCommit = [];

    for (const filePath of files) {
        const id = extractTicketIdFromPathOrFile(filePath);

        if (id) {
            const ticketLabel = `ticket:${id}`;
            const q = encodeURIComponent(`repo:${OWNER}/${REPO} is:issue label:"${ticketLabel}"`);
            const search = await ghRest(`/search/issues?q=${q}`, { method: "GET" });
            const found = (search.items || [])[0];

            if (found) {
                const issueNum = found.number;
                const current = await ghRest(`/repos/${OWNER}/${REPO}/issues/${issueNum}`, { method: "GET" });

                const oldLabels = (current.labels || []).map(l => (typeof l === "string" ? l : l.name));
                const kept = oldLabels.filter(l => !l.startsWith("status:"));
                const nextLabels = [...new Set([...kept, "status:Merged"])];

                await ghRest(`/repos/${OWNER}/${REPO}/issues/${issueNum}`, {
                    method: "PATCH",
                    body: { state: "closed" },
                });
                await ghRest(`/repos/${OWNER}/${REPO}/issues/${issueNum}/labels`, {
                    method: "PUT",
                    body: nextLabels,
                });

                console.log(`Closed issue #${issueNum} for ${id}`);

                if (projectMeta && PROJECT_TOKEN && current.node_id) {
                    await syncMergedToProject(projectMeta, current.node_id);
                }
            }
        }

        if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
            const changed = setStatusMergedInFile(filePath);
            if (changed) {
                console.log(`Updated status to Merged in file: ${filePath}`);
                touchedForCommit.push(filePath);
            }
        }
    }

    if (touchedForCommit.length > 0) {
        sh(`git config user.name "github-actions[bot]"`);
        sh(`git config user.email "github-actions[bot]@users.noreply.github.com"`);
        sh(`git add ${touchedForCommit.map(x => `"${x}"`).join(" ")}`);
        sh(`git commit -m "chore(tickets): mark merged tickets as Merged"`);
        sh(`git push origin ${DEFAULT_BRANCH}`);
        console.log(`Committed Merged status for ${touchedForCommit.length} file(s).`);
    } else {
        console.log("No files needed Status->Merged change.");
    }
}

/* =========================================================
 * MAIN
 * ======================================================= */

async function main() {
    if (MODE === "merge") {
        await runMerge();
        return;
    }
    if (MODE === "sync") {
        await runSync();
        return;
    }
    console.error(`Unknown TICKETS_MODE: ${MODE}`);
    process.exit(1);
}

main().catch(err => {
    console.error(err);
    process.exit(1);
});
