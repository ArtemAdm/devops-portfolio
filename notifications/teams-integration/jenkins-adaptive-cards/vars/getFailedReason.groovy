def call(Exception e = null, currentBuild, countGetLog = 100) {
    def logLines = currentBuild.rawBuild.getLog(countGetLog)

    def failReasonFromException = e?.message

    def failReasonFromLog = logLines.find { it =~ /(ERROR|FAILURE|Exception|BUILD FAILED)/ }

    def reason = failReasonFromLog ?: failReasonFromException ?: "Unknown failure reason"

    return "- ⚠️ **Build failed**: ${reason}"
}
