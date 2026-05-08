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
                // install 确保多模块依赖(docs-core等)被存入本地库
                sh 'mvn install -DskipTests'
            }
        }
        stage('Test & JaCoCo') {
            steps {
                // 运行测试并自动触发 JaCoCo 代理
                // -Dmaven.test.failure.ignore=true 确保即使测试失败也能生成覆盖率报告
                sh 'mvn test jacoco:report -Dmaven.test.failure.ignore=true'
            }
        }
        stage('PMD') {
            steps {
                sh 'mvn pmd:pmd || true'
            }
        }
        stage('Site Documentation') {
            steps {
                // 生成包含所有插件报告的站点(包括测试报告和覆盖率报告)
                sh 'mvn site -DskipTests -Ddoclint=none || true'
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
            // 归档生成的 HTML 报告和二进制文件
            archiveArtifacts artifacts: '**/target/site/**/*.*, **/target/*.jar, **/target/*.war', fingerprint: true
            // 发布 JUnit 测试结果记录
            junit '**/target/surefire-reports/*.xml'
        }
    }
}