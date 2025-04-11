package org.myorg

import groovy.json.JsonSlurper

class Youtrack implements Serializable {

    String url
    String accessToken

    Youtrack(url, accessToken) {
        this.url = url
        this.accessToken = accessToken
    }

    def debugMess = 'DEBUG youtrack'

    def doHttpRequest(String urlPath, String httpMethod = 'GET', String body=null) {
        def connection = new URL(url + urlPath).openConnection()
        connection.addRequestProperty('Accept', 'application/json')
        connection.setRequestMethod(httpMethod)
        connection.setDoOutput(true)

        if (accessToken) {
            connection.addRequestProperty('Authorization', 'Bearer ' + accessToken)
        }

        if (body) {
            connection.addRequestProperty('Content-Type', 'application/json')

            OutputStream pos = connection.getOutputStream()
            pos.write(body.getBytes())
            pos.flush()
            pos.close()
        }

        def responseCode = connection.getResponseCode()
        println("[${debugMess} HttpRequest] " + 'URL: ' + urlPath + ' Response code: ' + connection.getResponseCode())

        if (responseCode.equals(200)) {
            return new JsonSlurper().parseText(connection.getInputStream().getText())
        } else {
            println "[${debugMess} HttpRequest] ⚠️ HTTP Error: ${responseCode}"
            return null
        }
    }

    // Getters
    def getParentIssue(String issueId) {
        println("[${debugMess} getParentIssue] Get parent task name for ${issueId}")

        def parentIssueName;

        try {
            def parentIssueId = doHttpRequest(
                "/api/issues/${issueId}?fields=parent(issues(id,name))",
                'GET'
            )?.parent?.issues?.id?.get(0)

            parentIssueName = doHttpRequest(
                "/api/issues/${parentIssueId}?fields=id,summary,name,idReadable",
                'GET'
            )
        } catch (Exception e) {
            parentIssueName = null
        }

        return parentIssueName?.idReadable
    }

    def getIssueFieldValueId(String issueId, String issueFieldName) {
        println "[${debugMess} getIssueFieldValueId] Get issueId value <${issueId}> issueName <${issueFieldName}>"

        // https://www.jetbrains.com/help/youtrack/devportal/resource-api-admin-customFieldSettings-bundles-build-bundleID-values.html#create-BuildBundleElement-method-sample
        // update build list for the issue with a new appended build id
        def fieldData = doHttpRequest(
            "/api/issues/${issueId}/fields/${issueFieldName}?fields=value(name,id)",
            'GET'
        )

        if (!fieldData?.value) {
            println "[${debugMess} getIssueFieldValueId] ⚠️ Field '${issueFieldName}' has no value or is not found"
            return null
        }

        def value = fieldData.value

        // If the field is multi-value (array), e.g., tags or users
        if (value instanceof List && !value.isEmpty()) {
            return value.collect { it?.id ?: it?.name }.findAll { it }.join(', ')
        }

        // If the field is single value (Map)
        if (value instanceof Map) {
            return value.id ?: value.name
        }

        // Unexpected field value format
        println "[${debugMess} getIssueFieldValueId] ⚠️ Unexpected format for field value: ${value}"
    }

    def getUserInfo(String userId) {
        println("[${debugMess} getUserInfo] Get user fullName, email from user ID <${userId}>")

        def userData = doHttpRequest(
            "/api/users/${userId}?fields=fullName,email",
            'GET'
        )

        if (userData) {
            def name = userData?.fullName ?: "unknown"
            def email = userData?.email ?: "no-email"
            return [ name: name, email: email ]
        } else {
            return [ name: "not found", email: "not found" ]
        }
    }

    def getUsersInfo(String usersIdString) {
        println("[${debugMess} getUsersInfo] Get users fullName, email in Arraylist from users ID <${usersIdString}>")
        def usersId = usersIdString
            .split(',')
            .collect { it.trim() }
            .findAll { it }

        return usersId.collect { userId -> getUserInfo(userId) }
    }
}
