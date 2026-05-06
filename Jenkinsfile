pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }
        stage('Compile & Install') {
            steps {
                // install 是为了解决多模块依赖报错
                sh 'mvn install -DskipTests'
            }
        }
        stage('Test') {
            steps {
                // ignore=true 是为了防止测试用例失败中断流水线
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd || true'
            }
        }
        stage('JaCoCo') {
            steps {
                sh 'mvn jacoco:report || true'
            }
        }
        stage('Javadoc') {
            steps {
                // -Ddoclint=none 关掉严格检查，|| true 保证万一报错也不中断
                sh 'mvn javadoc:javadoc -Ddoclint=none || true'
            }
        }
        stage('Site') {
            steps {
                sh 'mvn site -Ddoclint=none || true'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    post {
        always {
            // 归档所有产物
            archiveArtifacts artifacts: '**/target/site/**/*.*', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
            archiveArtifacts artifacts: '**/target/**/*.war', fingerprint: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
