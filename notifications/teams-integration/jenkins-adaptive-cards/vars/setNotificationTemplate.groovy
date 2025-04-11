import groovy.json.JsonOutput

def call(env, notificationTemplateValues, currentBuild, options = [:]) {
    def personLead       = options.personLead ?: []
    def personPipe       = options.personPipe
    def personsQa        = options.personsQa ?: []
    def personsDevOps    = options.personsDevOps ?: []
    def personsCommit    = options.personsCommit ?: []
    def personsNotify    = options.personsNotify ?: []
    def messageBody      = options.messageBody ?: ''
    def changeLog        = options.changeLog ?: []

    def actions = generateActions([
        buildUrl   : currentBuild.absoluteUrl,
        testsUrl   : notificationTemplateValues.testsUrl,
        argocdUrl  : notificationTemplateValues.argocdUrl,
        grafanaUrl : notificationTemplateValues.grafanaUrl,
        sentryUrl  : notificationTemplateValues.sentryUrl
    ])

    notificationTemplateValues['messageMentionSummary'] = buildMentionSummary(
        personPipe, personsQa, personsNotify, personLead, personsDevOps
    )

    def mentionEntities = buildMentionEntities(
        personPipe, personsQa, personsNotify, personLead, personsCommit, personsDevOps
    )

    notificationTemplateValues['messageHeader']       += " ${currentBuild.result}"
    notificationTemplateValues['jsonEntities']        = mentionEntities
    notificationTemplateValues['buildStatus']         = currentBuild.result
    notificationTemplateValues['messageDurationTime'] = "- 🕒 **Duration**: ${buildDuration(currentBuild)}"
    notificationTemplateValues['messageBody']         = messageBody
    notificationTemplateValues['messageLastCommit']   = changeLog
    notificationTemplateValues['personLead']          = personLead
    notificationTemplateValues['actions']             = JsonOutput.toJson(actions)

    def channelId = options.channelId ?: 'teamsTemplate'
    def webhookId = env.MSTEAMS_WEBHOOK ?: options.webhookId

    if (!webhookId) {
        error "❌ Missing MSTEAMS_WEBHOOK environment variable or webhookId in options."
    }

    withCredentials([string(credentialsId: "${MSTEAMS_WEBHOOK}", variable: 'TEAMS_DEPLOY_HOOK')]) {
        notifyMsTeams("${TEAMS_DEPLOY_HOOK}", channelId, notificationTemplateValues)
    }
}
