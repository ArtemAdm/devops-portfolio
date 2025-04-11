### 🧩 `ApiUtils.groovy`

Helper class for YouTrack + Azure Active Directory integration.

Located at:  
`src/org/myorg/ApiUtils.groovy`

---

#### Method: `extractUser(...)`

Fetches a list of users from a YouTrack issue field and resolves their emails via Azure.

| Param               | Type     | Description                                       |
|--------------------|----------|--------------------------------------------------|
| youtrack           | Object   | YouTrack API client instance                      |
| codeIssue          | String   | Issue ID (e.g., `PROJECT-123`)                    |
| fieldIssueName     | String   | Issue field name (e.g., `QA Engineer`)            |
| getAzureEmailByName| Closure  | Closure that returns email for given user name    |

---

#### Returns:
`List<Map>`
Each map contains:
`[name: "John Doe", email: "john@company.com"]`

---

#### Example:

```groovy
def qaUsers = org.myorg.ApiUtils.extractUser(
    youtrackClient,
    "PROJECT-123",
    "QA Engineer",
    { username -> getUserMailAzure(username) }
)
```

### 🧩 `MicrosoftGraphClient.groovy`

Microsoft Graph API client for Azure Active Directory.

Location:  
`src/com/advcash/MicrosoftGraphClient.groovy`

---

#### Purpose:
- Fetch users from Azure AD
- Get Access Token via Client Credentials
- Perform Graph API requests
- Find user email by display name

---

#### Constructor:
```groovy
def client = new com.advcash.MicrosoftGraphClient(
  env.AZURE_CLIENT_ID,
  env.AZURE_CLIENT_SECRET,
  env.AZURE_TENANT_ID
)
```

#### Methods:

#### Example Usage:
```groovy
  def client = new com.advcash.MicrosoftGraphClient(...)

  def userInfo = client.findUserByDisplayName("John Doe")

  if (userInfo) {
    echo "User email: ${userInfo.mail}"
  }
```

#### Requirements
Jenkins credentials:
  AZURE_CLIENT_ID
  AZURE_CLIENT_SECRET
  AZURE_TENANT_ID
