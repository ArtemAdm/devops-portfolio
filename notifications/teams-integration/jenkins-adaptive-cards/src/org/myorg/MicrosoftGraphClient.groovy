package org.myorg

import groovy.json.JsonSlurper
import java.net.URLEncoder

class MicrosoftGraphClient implements Serializable {

    private String clientId
    private String clientSecret
    private String tenantId
    private String accessToken
    private long tokenExpirationTime = 0L

    MicrosoftGraphClient(String clientId, String clientSecret, String tenantId) {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.tenantId = tenantId
    }

    String getAccessToken() {
        def currentTime = System.currentTimeMillis()
        if (accessToken && currentTime < tokenExpirationTime - 60_000) {
            return accessToken
        }

        def tokenUrl = "https://login.microsoftonline.com/${tenantId}/oauth2/v2.0/token"
        def params = [
            'client_id'     : clientId,
            'scope'         : 'https://graph.microsoft.com/.default',
            'client_secret' : clientSecret,
            'grant_type'    : 'client_credentials'
        ]

        def body = params.collect { k, v -> "${k}=${URLEncoder.encode(v, 'UTF-8')}" }.join('&')

        def connection = new URL(tokenUrl).openConnection()
        connection.setRequestMethod('POST')
        connection.setDoOutput(true)
        connection.setRequestProperty('Content-Type', 'application/x-www-form-urlencoded')

        OutputStream pos = connection.getOutputStream()
        pos.write(body.getBytes())
        pos.flush()
        pos.close()

        def responseCode = connection.responseCode
        if (responseCode != 200) {
            def error = connection.errorStream?.text
            throw new RuntimeException("❌ Token request failed: ${error}")
        }

        def response = new JsonSlurper().parse(connection.inputStream)
        this.accessToken = response.access_token
        this.tokenExpirationTime = currentTime + (response.expires_in.toLong() * 1000)
        return accessToken
    }

    // def userInfo = client.doGraphGet('users')
    def doGraphGet(String endpoint) {
        def url = "https://graph.microsoft.com/v1.0/${endpoint}"
        def conn = new URL(url).openConnection()
        conn.setRequestProperty("Authorization", "Bearer " + getAccessToken())
        conn.setRequestProperty("Accept", "application/json")
        conn.connect()

        if (conn.responseCode != 200) {
            def error = conn.errorStream?.text
            throw new RuntimeException("❌ Graph GET failed: ${error}")
        }

        return new JsonSlurper().parse(conn.inputStream)
    }

    // Use def findUserByDisplayName = client.findUserByDisplayName(name)
    def findUserByDisplayName(name) {
        def filter = URLEncoder.encode("displayName eq '${name}'", "UTF-8")
        def result = doGraphGet("users?\$filter=${filter}")
        return result?.value?.getAt(0)
    }
}
