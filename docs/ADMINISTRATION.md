# Administration

## 1️⃣ GitHub Project (v2)

### Project Creation
Create a **GitHub Project (v2)**:
- either **User project**
- or **Organization project**

Remember:
- **OWNER** (user or organization)
- **PROJECT NUMBER** (project number in URL)

Example URL: `https://github.com/orgs/my-org/projects/5`

Change configuration in [tickets-one-merge.yml](../.github/workflows/tickets-one-merge.yml), [tickets-sync.yml](../.github/workflows/tickets-sync.yml), 
→ `PROJECT_KIND=organization`
→ `PROJECT_OWNER=my-org`
→ `PROJECT_NUMBER=5`

---

### Required Fields in Project

The project must have the following fields:

#### 🟦 Status (Single select)
Must contain the **same values** used in Markdown files. 

**Important:** You must manually add these options to your GitHub Project v2 board settings:

- Todo
- In Progress
- Done
- Merged
- Blocked
  *(or others as needed)*

> Values are **case-sensitive** – they must match exactly.

---

#### 🟩 Area (Single select)

Recommended values:
- bugs
- features
- tasks

These values correspond to file locations:

```
work/bugs/
work/features/
work/tasks/
```

---

## 2️⃣ GitHub Personal Access Token (PAT) for Projects v2

GitHub Actions **cannot use `GITHUB_TOKEN` for Projects v2**.
You must create a **Personal Access Token (classic)**.

### Token Creation
1. GitHub → **Settings**
2. **Developer settings**
3. **Personal access tokens**
4. **Tokens (classic)** → *Generate new token*

### Minimum Permissions
Check:
- ✅ `project`
- ✅ `repo` *(required for working with issues in private repositories)*

For **organization projects** you may also need:
- ✅ `read:org`

---

### Adding Token to Repository
In the repository:
1. **Settings**
2. **Secrets and variables → Actions**
3. **New repository secret**

Name: `PROJECT_TOKEN`
Value: `<your PAT token>`

---

## 3️⃣ GitHub Actions Configuration

Workflow files are located in: `.github/workflows/`


### Required Variables in Workflow

The following variables must be correctly set in workflow files:

```yaml
PROJECT_KIND: organization   # or "user"
PROJECT_OWNER: my-org        # organization or user name
PROJECT_NUMBER: "5"          # Project v2 number

PROJECT_FIELD_STATUS: Status
PROJECT_FIELD_AREA: Area
PROJECT_STATUS_MERGED: Merged # Name of the status for merged tickets
```

---

### Automated "Merged" Status

The system is designed to keep your Markdown files and GitHub Project in sync automatically:

1. **Developer sets status to `Done`**: When a developer finishes a task, they set the status to `Done` in the Markdown file within their branch.
2. **Pull Request Merge**: When the PR is merged into `master`, the **Tickets - on merge to master** workflow triggers.
3. **Automated Update**:
   - The workflow updates the corresponding GitHub Issue to `closed`.
   - It sets the Project v2 status to **Merged**.
   - It **automatically modifies the Markdown file** in the `master` branch, changing `Status: Done` to `Status: Merged`.
   - It commits and pushes this change back to `master`.

### Manual Full Sync to Merged

If you have many existing tickets that are already merged but still marked as `Done` in the files, you can perform a one-time full synchronization:

1. Go to the **Actions** tab in your GitHub repository.
2. Select the **Tickets - on merge to master** workflow.
3. Click **Run workflow**.
4. Select **full** in the `Sync mode` dropdown.
5. Click **Run workflow**.

This will scan all ticket files, change `Status: Done` to `Status: Merged`, and commit the changes. It will also close corresponding GitHub issues and update the Project board.

---

## 4️⃣ Workflow Permissions

Workflows use these permissions:

Automatically set

```yaml
permissions:
  contents: read | write
  issues: write
```

__Access to Project v2__

Handled via:

PROJECT_TOKEN (PAT)
