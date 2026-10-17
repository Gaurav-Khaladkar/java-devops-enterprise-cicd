pipeline {
    agent any

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'production'], description: 'Deployment Target Environment')
        booleanParam(name: 'ENABLE_SECURITY_SCAN', defaultValue: true, description: 'Run Trivy Vulnerability & Security Scanner')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip Maven unit and integration tests (Not recommended for prod)')
    }

    environment {
        // Registry & Image Configuration
        DOCKER_REGISTRY       = 'docker.io'
        IMAGE_NAME            = 'enterprise-devops/java-devops-application'
        IMAGE_TAG             = "${env.BUILD_NUMBER}-${env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'latest'}"
        
        // AWS & EKS Configuration
        AWS_REGION            = 'ap-south-1'
        CLUSTER_NAME          = 'enterprise-eks-cluster'
        K8S_NAMESPACE         = 'devops-production'
        HELM_RELEASE_NAME     = 'java-devops-app'
        
        // Quality & Security
        SONAR_PROJECT_KEY     = 'enterprise-java-devops-app'
        SCANNER_HOME          = tool 'SonarQubeScanner'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '15'))
        disableConcurrentBuilds()
        timeout(time: 45, unit: 'MINUTES')
        ansiColor('xterm')
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo "==> Checking out repository source code on branch: ${env.BRANCH_NAME ?: 'main'}"
                checkout scm
            }
        }

        stage('Maven Compile & Test') {
            steps {
                script {
                    if (params.SKIP_TESTS) {
                        echo "==> Skipping tests per pipeline parameter"
                        sh 'mvn clean compile'
                    } else {
                        echo "==> Running Maven compile, unit tests, and JaCoCo coverage analysis"
                        sh 'mvn clean test jacoco:report'
                    }
                }
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('SonarQube Code Quality Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQubeServer') {
                        sh """
                        ${SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.sources=src/main \
                            -Dsonar.tests=src/test \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Quality Gate Verification') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to SonarQube Quality Gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('Trivy Security & Dependency Scan') {
            when {
                expression { return params.ENABLE_SECURITY_SCAN }
            }
            steps {
                echo "==> Scanning filesystem and dependencies for CVEs"
                sh 'trivy fs --severity HIGH,CRITICAL --no-progress . || true'
            }
        }

        stage('Package Application') {
            steps {
                echo "==> Packaging Java WAR Archive"
                sh 'mvn package -DskipTests=true'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "==> Building Multi-Stage Docker Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                sh """
                docker build \
                    -t ${IMAGE_NAME}:${IMAGE_TAG} \
                    -t ${IMAGE_NAME}:latest \
                    -f Dockerfile .
                """
            }
        }

        stage('Trivy Container Scan') {
            when {
                expression { return params.ENABLE_SECURITY_SCAN }
            }
            steps {
                echo "==> Scanning Container Image for Vulnerabilities"
                sh "trivy image --severity HIGH,CRITICAL --exit-code 0 ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Registry Authentication & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-registry-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                    echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin ${DOCKER_REGISTRY}
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    docker push ${IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Helm Chart Validation') {
            steps {
                echo "==> Validating Helm Chart syntax"
                sh 'helm lint ./helm/java-app-chart'
            }
        }

        stage('Deploy to Kubernetes via Helm') {
            steps {
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                    sh """
                    echo "==> Updating AWS EKS kubeconfig for cluster ${CLUSTER_NAME}"
                    aws eks update-kubeconfig --region ${AWS_REGION} --name ${CLUSTER_NAME}

                    echo "==> Ensuring namespace ${K8S_NAMESPACE} exists"
                    kubectl create namespace ${K8S_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

                    echo "==> Deploying application via Helm 3"
                    helm upgrade --install ${HELM_RELEASE_NAME} ./helm/java-app-chart \
                        --namespace ${K8S_NAMESPACE} \
                        --values ./helm/java-app-chart/values-${params.ENVIRONMENT}.yaml \
                        --set image.repository=${IMAGE_NAME} \
                        --set image.tag=${IMAGE_TAG} \
                        --atomic \
                        --cleanup-on-fail \
                        --wait \
                        --timeout 300s
                    """
                }
            }
        }

        stage('Post-Deployment Smoke Verification') {
            steps {
                script {
                    echo "==> Verifying Deployment rollout status"
                    sh "kubectl rollout status deployment/${HELM_RELEASE_NAME}-java-devops-application -n ${K8S_NAMESPACE} --timeout=120s"
                    echo "==> Querying pods in namespace ${K8S_NAMESPACE}"
                    sh "kubectl get pods -n ${K8S_NAMESPACE} -l app.kubernetes.io/instance=${HELM_RELEASE_NAME}"
                }
            }
        }
    }

    post {
        always {
            echo "==> Cleaning up pipeline workspace artifacts"
            cleanWs(cleanWhenAborted: true, cleanWhenFailure: true, cleanWhenNotBuilt: true, cleanWhenSuccess: true, cleanWhenUnstable: true)
        }
        success {
            echo "SUCCESS: Enterprise Java DevOps CI/CD Pipeline Completed Successfully! 🚀"
        }
        failure {
            echo "FAILURE: Pipeline Failed! Please inspect console logs and telemetry. ❌"
        }
    }
}
