### 🎨 Adaptive Card Template – `teamsTemplate.json`

Universal Adaptive Card for MS Teams build notifications.

---

#### Features:
- Dynamic build status display (`✅` or `❌`)
- Auto-color header (green/red)
- Mention summary block
- Message body with build details
- Commit history (optional)
- Duration
- Navigation buttons (Grafana, ArgoCD, Sentry, Jenkins)
- Dynamic mentions via `msteams.entities`

---

#### Variables used:

| Variable               | Description                         |
|-----------------------|-------------------------------------|
| buildStatus           | Build result (SUCCESS, FAILURE, etc) |
| messageHeader         | Card title header                   |
| messageMentionSummary | Mention text block                  |
| messageBody           | Build info (Docker image, etc)      |
| messageDurationTime   | Duration of build                   |
| messageLastCommit     | Commit history block (optional)     |
| actions               | JSON array of `Action.OpenUrl`     |
| jsonEntities          | Mention entities for MS Teams       |

---

### Location:
resources/msteams/teamsTemplate.json

Used in:

```groovy
notifyMsTeams(env.MSTEAMS_WEBHOOK, 'teamsTemplate', notificationTemplateValues)
```
