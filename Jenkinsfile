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
                    // Gradle 빌드 수행
                    sh './gradlew clean build'
                }
            }
        }

        stage('Verify Build Artifacts') {
            steps {
                script {
                    // 빌드 결과물 확인
                    sh 'ls -la build/libs/'
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def gitTag = sh(returnStdout: true, script: 'git describe --tags --always').trim()

                    // Docker 빌드를 위한 작업 디렉토리 설정
                    dir("${env.WORKSPACE}") {
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
}
