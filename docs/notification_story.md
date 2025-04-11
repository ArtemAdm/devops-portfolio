
# Development Story — Jenkins MS Teams Notification Framework

## Introduction

The initial request from developers was quite simple — add notifications to Microsoft Teams when a Jenkins pipeline is triggered. The main requirement was to tag the user who started the build.

However, as the implementation progressed, the requirements became more complex — and the project evolved into a full-fledged automated notification framework integrated into DevOps processes.

---

## Solution Architecture

GitLab → Jenkins → YouTrack API → Azure AD (Microsoft Graph API) → MS Teams (Adaptive Cards)

---

## Key Development Stages and Challenges

### Detecting the User Who Triggered the Pipeline

Function `getPipelineUser.groovy` determines:

- Who triggered the pipeline
- User's email address
- Handles both manual runs and Git webhook-triggered runs

### Fetching Issue Data from YouTrack

Function `getYoutrackClient.groovy` and class `Youtrack.groovy` allow:

- Extracting issue ID from commit messages (`ABC-123` format)
- Fetching QA Engineers from the "QA" field
- Fetching Notify participants from the "Notify" field

### Resolving Teams Email Addresses vs YouTrack Emails

Function `getUserMailAzure.groovy` and class `MicrosoftGraphClient.groovy`:

- Fetch Full Name from YouTrack
- Retrieve the correct email address from Azure AD via Microsoft Graph API

### Processing Commit Changes

Function `processCommits.groovy`:

- Retrieves the list of commits between builds
- Determines commit authors
- Generates MS Teams `<at>` mentions for commit authors

### Preparing Notification Data

Functions:

| Function | Purpose |
|----------|---------|
| buildMentionEntities.groovy | Generate MS Teams mentions |
| buildMentionSummary.groovy  | Generate summary text |
| initNotificationTemplate.groovy | Initialize template data |
| setNotificationTemplate.groovy | Final data assembly for sending notifications |

### Sending Notification to Teams

Function `notifyMsTeams.groovy` sends Adaptive Card to MS Teams.

Features:

- Build Status:
  - Success → Green background with checkmark icon
  - Failed → Red background with cross icon

- Additional Details:
  - Pipeline parameters (environment, cleanDB, skipTests, etc.)
  - Build duration
  - Error logs (if failed)
  - Commit list with authors
  - Action buttons (Jenkins, Test Report, Grafana, Sentry, ArgoCD)

Action buttons are dynamically generated via `generateActions.groovy` — if some URL is not provided, the button is skipped.

---

## Behavior in Success vs Failed

| Status | Behavior |
|--------|----------|
| Success | QA from YouTrack, commit authors, Lead QA, Project Lead are tagged |
| Failed  | DevOps, Lead QA, Project Lead are tagged, error logs are collected |

In case of notification sending error — a fallback notification is sent with minimal information to allow quick DevOps response.

---

## Results and Takeaways

This notification system has become an essential part of CI/CD processes:

- Fully automated notifications in MS Teams
- Flexible and extendable solution
- Used for deploys in stage/prod environments
- Supporting Backend, Frontend, and Test pipelines

---

## Possible Improvements

- Add Slack notification support
- Extract configuration to separate config files
- Support for multiple notification templates
- Unit tests for Shared Library functions
- Helm Chart for fast deployment of Jenkins Shared Library
