import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.Utils

/////////////////////////////////////////////////////////////////
// This file is used for generating Jenkins jobs
// Its placement and structure is a "convention" in kiegroup
// For kogito-editors-java we currently generate just pullrequest job,
// to run sonar with each pull request, see: .ci/jenkins/Jenkinsfile
//
// For more details see:
//  - https://github.com/kiegroup/kogito-pipelines/blob/main/docs/jenkins.md
// Or the same file in other repositories like:
//  - https://github.com/kiegroup/kogito-runtimes/tree/main/.ci/jenkins/dsl
/////////////////////////////////////////////////////////////////

def getDefaultJobParams() {
    def jobParams = KogitoJobTemplate.getDefaultJobParams(this, 'kogito-editors-java')
    jobParams.pr.excluded_regions.add('docsimg/.*')
    return jobParams
}

Map getMultijobPRConfig() {
    return [
            parallel: true,
            jobs : [
                    [
                            id: 'Kogito Tooling Editors Java',
                            primary: true,
                            repository: 'kogito-editors-java'
                    ]
            ],
            extraEnv : [
                    ENABLE_SONARCLOUD: Utils.isMainBranch(this)
            ]
    ]
}

// PR checks
setupMultijobPrDefaultChecks()

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createMultijobPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}