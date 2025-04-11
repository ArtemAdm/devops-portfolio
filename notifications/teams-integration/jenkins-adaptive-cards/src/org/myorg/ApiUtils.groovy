package org.myorg

class ApiUtils {
    static List<Map> extractUser(def youtrack, String codeIssue, String fieldIssueName, Closure getAzureEmailByName) {
        def fieldUsers = []

        def userId = youtrack.getIssueFieldValueId(codeIssue, fieldIssueName)
        if (!userId) {
            println "[⚠️ extractUser] Field '${fieldIssueName}' in issue '${codeIssue}' is empty"
            return fieldUsers
        }

        def usersInfo = youtrack.getUsersInfo(userId)
        if (!usersInfo) {
            println "[⚠️ extractUser] Failed to fetch users by ID: ${userId}"
            return fieldUsers
        }

        usersInfo.each { user ->
            def name = user.name
            def email = getAzureEmailByName(name)

            if (!email) {
                println "[⚠️ extractUser] Email not found in Azure for user: '${name}'"
                email = user.name
            } else {
                println "✅ Email found: ${name} → ${email}"
            }
        }

        return fieldUsers << [name: name, email: email]
    }
}
