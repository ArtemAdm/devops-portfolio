import org.myorg.Youtrack

def call(String youtrackUrl, String credentialsId = 'YOUTRACK_ACCESS_TOKEN') {
    def youtrackToken = null

    withCredentials([string(credentialsId: credentialsId, variable: 'YOUTRACK_TOKEN')]) {
        youtrackToken = env.YOUTRACK_TOKEN
    }

    if (!youtrackToken) {
        error "❌ Youtrack token could not be loaded from credentials: ${credentialsId}"
    }

    return new Youtrack(youtrackUrl, youtrackToken)
}
