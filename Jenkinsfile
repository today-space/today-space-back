pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    // Gradle을 사용하여 프로젝트 빌드
                    sh './gradlew build'
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def gitTag = sh(returnStdout: true, script: 'git describe --tags --always').trim()
                    // Docker 이미지를 빌드할 때 현재 디렉토리를 컨텍스트로 사용
                    def dockerImage = docker.build("today-space/today-space:${gitTag}", ".")
                    withDockerRegistry([ credentialsId: "github-token" ]) {
                        dockerImage.push("${gitTag}")
                        dockerImage.push("latest")
                    }
                    sh "docker rmi today-space/today-space:${gitTag}"
                }
            }
        }
    }
}
