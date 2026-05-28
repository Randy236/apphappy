// Happy Jump — Jenkins: API Node + Android + SonarQube + build/deploy por scripts
// Guia: docs/GUIA_JENKINS_CI_CD.md

pipeline {
    agent any

    tools {
        nodejs 'NodeJS 22'
    }

    environment {
        ANDROID_IMAGE = 'mingc/android-build-box:latest'
        PROJECT_DIR = "${env.WORKSPACE}"
        BUILD_ID = "${env.BUILD_NUMBER ?: 'local'}-${env.GIT_COMMIT?.take(7) ?: 'nosha'}"
        HAPPYJUMP_DEPLOY_BASE = "${env.HAPPYJUMP_DEPLOY_BASE ?: "${env.HOME}/servers/happyjump"}"
        DOCKER_NETWORK = "${env.DOCKER_NETWORK ?: 'happyjump-ci_default'}"
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

        stage('Test') {
            parallel {
                stage('API (Node)') {
                    steps {
                        dir('server') {
                            sh 'npm run test:ci'
                        }
                    }
                }
                stage('Android (unitarias + JaCoCo)') {
                    steps {
                        sh '''
docker --version
docker pull "${ANDROID_IMAGE}" || true
docker run --rm \
  -v "${PROJECT_DIR}:/project" \
  -w /project \
  -e GRADLE_USER_HOME=/project/.gradle-jenkins \
  "${ANDROID_IMAGE}" \
  bash -lc "chmod +x gradlew && ./gradlew :app:testDebugUnitTest :app:jacocoTestReport --no-daemon -Dorg.gradle.jvmargs='-Xmx2560m'"
'''
                    }
                }
            }
        }

        stage('Sonar') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
docker run --rm \
  --network "${DOCKER_NETWORK}" \
  -v "${PROJECT_DIR}:/project" \
  -w /project \
  -e SONAR_TOKEN \
  -e SONAR_HOST_URL="${SONAR_HOST_URL}" \
  "${ANDROID_IMAGE}" \
  bash -lc "chmod +x gradlew && ./gradlew sonar \
    -Dproject.settings=sonar-project.local.properties \
    -Dsonar.host.url=${SONAR_HOST_URL} \
    -Dsonar.token=${SONAR_TOKEN} \
    --no-daemon -Dorg.gradle.jvmargs='-Xmx2560m'"
'''
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ci/build-production.sh ci/deploy-server.sh && ./ci/build-production.sh'
            }
        }

        stage('Deploy') {
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
            echo "Pipeline OK — API y APK en ${HAPPYJUMP_DEPLOY_BASE}"
        }
        failure {
            echo 'Pipeline fallo — revisa consola (Node, Gradle, Sonar, ci/*.sh)'
        }
    }
}
