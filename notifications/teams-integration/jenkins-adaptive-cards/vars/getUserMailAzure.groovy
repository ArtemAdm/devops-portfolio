import org.myorg.MicrosoftGraphClient

def call(String userFullName) {
    def userInfo

    withCredentials([
        string(credentialsId: 'MS_JENKINS_CLIENT_ID', variable: 'AZURE_CLIENT_ID'),
        string(credentialsId: 'MS_JENKINS_CLIENT_SECRET', variable: 'AZURE_CLIENT_SECRET'),
        string(credentialsId: 'MS_JENKINS_TENANT_ID', variable: 'AZURE_TENANT_ID')
    ]) {
        def client = new MicrosoftGraphClient(AZURE_CLIENT_ID, AZURE_CLIENT_SECRET, AZURE_TENANT_ID)

        userInfo = client.findUserByDisplayName(userFullName)
    }

    return userInfo?.mail
}
