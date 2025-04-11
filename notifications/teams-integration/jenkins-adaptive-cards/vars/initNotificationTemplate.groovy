def call(Map context = [:]) {
    def defaultMap = [
        'buildUrl'              : context.buildUrl ?: '',
        'grafanaUrl'            : context.grafanaUrl ?: '',
        'argocdUrl'             : context.argocdUrl ?: '',
        'sentryUrl'             : context.sentryUrl ?: '',
        'testsUrl'              : '',
        'messageDurationTime'   : '',
        'messageHeader'         : context.messageHeader ?: '',
        'messageMentionSummary' : '',
        'messageBody'           : '',
        'messageLastCommit'     : null,
        'personPipe'            : null,
        'personsCommit'         : [],
        'personsNotify'         : [],
        'personsQa'             : [],
        'personLead'            : [],
        'personsDevOps'         : []
    ]

    return defaultMap + (context.extraFields ?: [:])
}
