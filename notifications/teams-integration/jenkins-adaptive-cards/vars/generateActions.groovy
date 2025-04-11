def call(Map urls) {
    def actions = []

    def configs = [
        [id: 'jenkins-build-tab', title: 'Build',  icon: 'https://raw.githubusercontent.com/ArtemAdm/devops-portfolio/main/assets/icons/Jenkins.png', urlKey: 'buildUrl'],
        [id: 'jenkins-test-tab',  title: 'Test',   icon: 'https://raw.githubusercontent.com/ArtemAdm/devops-portfolio/main/assets/icons/Jenkins.png', urlKey: 'testsUrl'],
        [id: 'argocd-tab',        title: 'ArgoCD', icon: 'https://raw.githubusercontent.com/ArtemAdm/devops-portfolio/main/assets/icons/ArgoCD.png',  urlKey: 'argocdUrl'],
        [id: 'grafana-tab',       title: 'Logs',   icon: 'https://raw.githubusercontent.com/ArtemAdm/devops-portfolio/main/assets/icons/Grafana.png', urlKey: 'grafanaUrl'],
        [id: 'sentry-tab',        title: 'Sentry', icon: 'https://raw.githubusercontent.com/ArtemAdm/devops-portfolio/main/assets/icons/Sentry.png',  urlKey: 'sentryUrl']
    ]

    configs.each { cfg ->
        def url = urls[cfg.urlKey]
        if (url && url != '') {
            actions << [
                type    : 'Action.OpenUrl',
                id      : cfg.id,
                title   : cfg.title,
                iconUrl : cfg.icon,
                url     : url,
                style   : 'positive'
            ]
        }
    }

    return actions
}