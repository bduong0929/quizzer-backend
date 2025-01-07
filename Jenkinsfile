pipeline {
    agent any
    
    environment {
        docker_image = 'quizzer-app'
        docker_tag = "${build_number}"
        // add database credentials from jenkins credentials
        db_creds = credentials('db_credentials')
    }
    
    stages {
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('build') {
            steps {
                // build with maven
                sh 'mvn clean package -dskiptests'
            }
        }
        
        // stage('test') {
        //     steps {
        //         sh 'mvn test'
        //     }
        // }
        
        stage('docker build') {
            when { branch 'main' }  // only on main branch
            steps {
                script {
                    // build new image
                    sh "docker build -t ${docker_image}:${docker_tag} ."
                }
            }
        }
        
        stage('deploy') {
            when { branch 'main' }  // only on main branch
            steps {
                script {
                    // stop existing container
                    sh "docker stop ${docker_image} || true"
                    sh "docker rm ${docker_image} || true"
                    
                    // run new container with environment variables
                    sh """
                        docker run -d \
                        --name ${docker_image} \
                        -p 8080:8080 \
                        -e spring_datasource_username=${db_creds_usr} \
                        -e spring_datasource_password=${db_creds_psw} \
                        --restart unless-stopped \
                        ${docker_image}:${docker_tag}
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo 'pipeline succeeded!'
        }
        failure {
            echo 'pipeline failed!'
        }
    }
}