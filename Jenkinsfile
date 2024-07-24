pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    def gitTag = sh(returnStdout: true, script: 'git describe --tags --always').trim()
                    def dockerImage = docker.build("today-space/today-space:${gitTag}", "./")
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