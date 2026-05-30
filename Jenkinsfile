// Happy Jump — Jenkins CI/CD
// Modo rapido (default): solo API — evita colgar Docker con Gradle/Android.
// Modo completo: Android + Sonar + build + deploy (requiere Docker 8 GB+ RAM).
// Guia: docs/GUIA_JENKINS_CI_CD.md

pipeline {
    agent any

    parameters {
        choice(
            name: 'PIPELINE_MODE',
            choices: ['rapido', 'completo'],
            description: 'rapido = tests API (recomendado). completo = + Android, Sonar, build APK, deploy.'
        )
    }

    environment {
        ANDROID_IMAGE = 'mingc/android-build-box:latest'
        PROJECT_DIR = "${env.WORKSPACE}"
        BUILD_ID = "${env.BUILD_NUMBER ?: 'local'}-${env.GIT_COMMIT?.take(7) ?: 'nosha'}"
        HAPPYJUMP_DEPLOY_BASE = "${env.HAPPYJUMP_DEPLOY_BASE ?: '/var/jenkins_home/servers/happyjump'}"
        DOCKER_NETWORK = "${env.DOCKER_NETWORK ?: 'happyjump-ci_default'}"
        JENKINS_HOST_WORKSPACE_ROOT = "${env.JENKINS_HOST_WORKSPACE_ROOT ?: 'E:/happyjump-ci/data/jenkins_home/workspace'}"
    }

    options {
        timestamps()
        timeout(time: 120, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                dir('server') {
                    sh 'npm ci --ignore-scripts 2>/dev/null || npm install'
                }
            }
        }

        stage('Test API') {
            steps {
                dir('server') {
                    sh 'npm run test:ci'
                }
            }
        }

        stage('Test Android') {
            when { expression { params.PIPELINE_MODE == 'completo' } }
            steps {
                sh 'chmod +x ci/jenkins-run-gradle.sh && ./ci/jenkins-run-gradle.sh :app:testDebugUnitTest :app:jacocoTestReport'
            }
        }

        stage('Sonar') {
            when { expression { params.PIPELINE_MODE == 'completo' } }
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh 'chmod +x ci/jenkins-run-sonar.sh && ./ci/jenkins-run-sonar.sh'
                    }
                }
            }
        }

        stage('Build') {
            when { expression { params.PIPELINE_MODE == 'completo' } }
            steps {
                sh 'chmod +x ci/build-production.sh ci/deploy-server.sh && ./ci/build-production.sh'
            }
        }

        stage('Deploy') {
            when { expression { params.PIPELINE_MODE == 'completo' } }
            steps {
                sh './ci/deploy-server.sh'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'app/build/test-results/testDebugUnitTest/*.xml'
            archiveArtifacts artifacts: 'dist/**,app/build/reports/jacoco/**', allowEmptyArchive: true
        }
        success {
            script {
                if (params.PIPELINE_MODE == 'rapido') {
                    echo 'Pipeline OK (modo rapido — API). Android/Sonar: usar GitHub Actions o modo completo con mas RAM.'
                } else {
                    echo "Pipeline OK (completo) — artefactos en ${HAPPYJUMP_DEPLOY_BASE}"
                }
            }
        }
        failure {
            echo 'Pipeline fallo — revisa consola. Si Docker se cuelga, usa PIPELINE_MODE=rapido.'
        }
    }
}
