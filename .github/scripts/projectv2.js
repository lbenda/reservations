/* eslint-disable no-console */
const GRAPHQL_URL = "https://api.github.com/graphql";

async function gql(token, query, variables = {}) {
    const r = await fetch(GRAPHQL_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ query, variables }),
    });

    const json = await r.json();
    if (!r.ok || json.errors) {
        const msg = JSON.stringify(json, null, 2);
        throw new Error(`GraphQL error: ${msg}`);
    }
    return json.data;
}

async function getProjectMeta({ token, kind, owner, number, statusFieldName, areaFieldName }) {
    const query = `
    query($owner:String!, $number:Int!) {
      ownerNode: ${kind}(login:$owner) {
        projectV2(number:$number) {
          id
          fields(first: 50) {
            nodes {
              __typename
              ... on ProjectV2Field { id name }
              ... on ProjectV2SingleSelectField {
                id name
                options { id name }
              }
            }
          }
        }
      }
    }
  `;

    const data = await gql(token, query, { owner, number: Number(number) });
    const project = data.ownerNode?.projectV2;
    if (!project?.id) throw new Error("ProjectV2 not found (check PROJECT_KIND/OWNER/NUMBER).");

    const fields = project.fields.nodes || [];
    const statusField = fields.find(f => f.name === statusFieldName);
    const areaField = fields.find(f => f.name === areaFieldName);

    const statusOptions = new Map();
    if (statusField?.options) for (const o of statusField.options) statusOptions.set(o.name, o.id);

    const areaOptions = new Map();
    if (areaField?.options) for (const o of areaField.options) areaOptions.set(o.name, o.id);

    return {
        projectId: project.id,
        statusFieldId: statusField?.id || null,
        areaFieldId: areaField?.id || null,
        statusOptions,
        areaOptions,
    };
}

function sleep(ms) {
    return new Promise(r => setTimeout(r, ms));
}

async function resolveIssueNodeWithRetry(token, contentNodeId) {
    const findQuery = `
    query($id:ID!) {
      node(id:$id) {
        ... on Issue {
          id
          projectItems(first: 50) {
            nodes { id project { id } }
          }
        }
      }
    }
  `;

    const delays = [1000, 2000, 4000, 8000, 8000]; // ~23s max
    let lastErr = null;

    for (let i = 0; i < delays.length; i++) {
        try {
            const data = await gql(token, findQuery, { id: contentNodeId });
            if (data?.node) return data; // success
            lastErr = new Error("node is null");
        } catch (e) {
            lastErr = e;
        }
        await sleep(delays[i]);
    }

    throw lastErr || new Error("Could not resolve node after retries");
}

async function ensureItemInProject({ token, projectId, contentNodeId }) {
    // 1) Find existing via Issue.projectItems (with retry)
    const found = await resolveIssueNodeWithRetry(token, contentNodeId);
    const nodes = found.node?.projectItems?.nodes || [];
    const existing = nodes.find(n => n?.project?.id === projectId);
    if (existing?.id) return existing.id;

    // 2) Add to project if missing
    const addMutation = `
    mutation($projectId:ID!, $contentId:ID!) {
      addProjectV2ItemById(input:{projectId:$projectId, contentId:$contentId}) {
        item { id }
      }
    }
  `;
    const added = await gql(token, addMutation, { projectId, contentId: contentNodeId });
    return added.addProjectV2ItemById.item.id;
}

async function setSingleSelectField({ token, projectId, itemId, fieldId, optionId }) {
    if (!fieldId || !optionId) return;

    // updateProjectV2ItemFieldValue is the official mutation. :contentReference[oaicite:5]{index=5}
    const mutation = `
    mutation($projectId:ID!, $itemId:ID!, $fieldId:ID!, $optionId:String!) {
      updateProjectV2ItemFieldValue(
        input:{
          projectId:$projectId,
          itemId:$itemId,
          fieldId:$fieldId,
          value:{ singleSelectOptionId:$optionId }
        }
      ) {
        projectV2Item { id }
      }
    }
  `;
    await gql(token, mutation, { projectId, itemId, fieldId, optionId });
}

module.exports = {
    getProjectMeta,
    ensureItemInProject,
    setSingleSelectField,
};
