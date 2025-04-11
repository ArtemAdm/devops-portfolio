import groovy.json.JsonOutput

def call(personPipe, personsQA = [], personsNotify = [], personLead = null, personsCommit = [], personsDevOps = []) {
    def mentionEntities = []

    def allPersons = (personsQA ?: []) + (personsNotify ?: []) + (personsCommit ?: []) + (personsDevOps ?: [])
    def seen = [] as Set

    [personPipe, personLead].findAll { it?.name && it?.email }.each { person ->
        mentionEntities << [
            type: 'mention',
            text: "<at>${person.name}</at>",
            mentioned: [
                id  : person.email,
                name: person.name
            ]
        ]
        seen << "${person.name}|${person.email}"
    }

    allPersons.findAll { it?.name && it?.email }.each { person ->
        def key = "${person.name}|${person.email}"
        if (!seen.contains(key)) {
            mentionEntities << [
                type: 'mention',
                text: "<at>${person.name}</at>",
                mentioned: [
                    id  : person.email,
                    name: person.name
                ]
            ]
            seen << key
        }
    }

    return mentionEntities
}
