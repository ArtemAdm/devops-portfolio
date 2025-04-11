import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def doHttpRequest(String url, String accessToken=null, String httpMethod = 'GET', String body=null) {
    def connection = new URL(url).openConnection()
    connection.addRequestProperty("Content-Type", "application/vnd.microsoft.card.adaptive")
    if (accessToken) {
        connection.addRequestProperty("Authorization", "Bearer " + accessToken)
    }
    connection.setRequestMethod(httpMethod)
    connection.setDoOutput(true)

    if (body) {
        OutputStream pos = connection.getOutputStream()
        pos.write(body.getBytes())
        pos.flush()
        pos.close()
    }

    def responseCode = connection.getResponseCode()
    println("Response code: " + connection.getResponseCode())
    if (responseCode.equals(200)) {
        return new JsonSlurper().parseText(connection.getInputStream().getText().trim())
    } else {
        return null
    }
}

def call(String teamsWebHook, String templateName, def templateValues) {
    def teamsMessageTemplate = libraryResource "msteams/${templateName}.json"

    if (templateValues?.jsonEntities) {
        templateValues['jsonEntities'] = JsonOutput.toJson(templateValues.jsonEntities)
    }

    def engine = new groovy.text.SimpleTemplateEngine()
    String template = engine.createTemplate(teamsMessageTemplate).make(templateValues)
    println("Template:\n" + template)

    return doHttpRequest(teamsWebHook, null, 'POST', template)
}
