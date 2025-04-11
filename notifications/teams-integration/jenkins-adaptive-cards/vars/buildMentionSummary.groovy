def call(personPipe, personsQA = [], personsNotify = [], personLead = null, personsDevOps = []) {
    def summary = ''

    // Используем Set для отслеживания уже добавленных людей (по name|email)
    def seen = [] as Set


    // 🔧 Pipe Starter
    if (personPipe?.name && personPipe?.email) {
        summary += "- 🔧 Pipeline started by: <at>${personPipe.name}</at>\n"
        seen << "${personPipe.name}|${personPipe.email}"
    } else {
        summary += "- 🔧 Pipeline started by: not specified\n"
    }

    // 🔧 Lead
    if (personLead?.name && personLead?.email) {
        summary += "- 👨‍🏫 Project Lead: <at>${personLead.name}</at>\n"
        seen << "${personLead.name}|${personLead.email}"
    }

    // 🧪 QA
    def qaMentions = []
    personsQA.findAll { it.name && it.email }.each { person ->
        def key = "${person.name}|${person.email}"
        if (!seen.contains(key)) {
            seen << key
            qaMentions << "<at>${person.name}</at>"
        }
    }
    if (qaMentions) {
        summary += "- 🧪 QA: ${qaMentions.join(', ')}\n"
    }

    // 📣 Notify
    def notifyMentions = []
    personsNotify.findAll { it.name && it.email }.each { person ->
        def key = "${person.name}|${person.email}"
        if (!seen.contains(key)) {
            seen << key
            notifyMentions << "<at>${person.name}</at>"
        }
    }
    if (notifyMentions) {
        summary += "- 📣 Notify: ${notifyMentions.join(', ')}\n"
    }

    // 🔧 DevOps
    def devOpsMentions = []
    personsDevOps.findAll { it.name && it.email }.each { person ->
        def key = "${person.name}|${person.email}"
        if (!seen.contains(key)) {
            seen << key
            devOpsMentions << "<at>${person.name}</at>"
        }
    }
    if (devOpsMentions) {
        summary += "- 🛠 DevOps: ${devOpsMentions.join(' ')}"
    }

    return summary.trim()
}
