pipeline {
    agent none

    options {
        timestamps()
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds(abortPrevious: true)
    }

    parameters {
        booleanParam(name: 'RUN_DEVICE_TESTS', defaultValue: false, description: 'Run the @device scenario after verification')
        string(name: 'DEVICE_AGENT_LABEL', defaultValue: 'mobile-android', description: 'Jenkins label for the allocated device agent')
        choice(name: 'FRAMEWORK_PLATFORM', choices: ['android', 'ios'], description: 'Target platform')
        choice(name: 'IOS_TARGET', choices: ['simulator', 'device'], description: 'Used when FRAMEWORK_PLATFORM is ios')
        string(name: 'DEVICE_NAME', defaultValue: 'Mobile Device', description: 'Appium device name')
        string(name: 'DEVICE_UDID', defaultValue: '', description: 'Connected device or simulator UDID')
    }

    stages {
        stage('Verify') {
            agent any
            steps {
                sh 'mvn --batch-mode --no-transfer-progress clean test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'reports/**/*,logs/**/*,target/cucumber-reports/**/*'
                }
            }
        }

        stage('Device') {
            when {
                expression { params.RUN_DEVICE_TESTS }
            }
            agent {
                label "${params.DEVICE_AGENT_LABEL}"
            }
            environment {
                FRAMEWORK_PLATFORM = "${params.FRAMEWORK_PLATFORM}"
                IOS_TARGET = "${params.IOS_TARGET}"
                DEVICE_NAME = "${params.DEVICE_NAME}"
                DEVICE_UDID = "${params.DEVICE_UDID}"
                START_APPIUM = 'true'
            }
            steps {
                sh './scripts/run-device-tests.sh'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'reports/**/*,logs/**/*,target/cucumber-reports/**/*'
                }
            }
        }
    }
}
