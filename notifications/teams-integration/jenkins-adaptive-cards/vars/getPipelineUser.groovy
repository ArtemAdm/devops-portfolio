import groovy.json.JsonSlurper

def call(params, env, currentBuild) {
    def buildUserName = "Unknown User"
    def buildUserEmail = "Unknown Email"

    if (params.containsKey('buildUser') && params.buildUser?.trim()) {
        def json = new JsonSlurper().parseText(params.buildUser)
        buildUserName = json.name ?: buildUserName
        buildUserEmail = json.email ?: buildUserEmail

    } else if (env.gitlabUserName?.trim()) {
        buildUserName = env.gitlabUserName
        buildUserEmail = env.gitlabUserEmail ?: buildUserEmail

    } else {
        def userIdCause = currentBuild.rawBuild.getCause(hudson.model.Cause.UserIdCause)
        if (userIdCause) {
            def user = jenkins.model.Jenkins.instanceOrNull.getUser(userIdCause.getUserId())
            if (user != null) {
                buildUserName = user.getFullName()
                buildUserEmail = user.getProperty(hudson.tasks.Mailer.UserProperty)?.getAddress() ?: buildUserEmail
            }
        }
    }

    echo "🔧 Pipeline started by: name: ${buildUserName}, email: ${buildUserEmail}"
    return [ name: buildUserName, email: buildUserEmail ]
}
