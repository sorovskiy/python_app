pipeline {
    agent any

	stages {
		stage("git clone") {
			steps{
				git changelog: false, poll: false, url: 'https://github.com/sorovskiy/python_app.git'

			}
		}
        stage("build docker image") {
			steps{
				sh "docker login -u marseek -p marseek6984"
                sh "docker build -t marseek/python_image ."
                sh "docker push marseek/python_image"
			}
		}
        stage("ssh") {
			steps{
                sshPublisher(publishers: [sshPublisherDesc(configName: 'my_1st', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand:
                    '''sudo docker stop hello
                    sudo docker pull marseek/python_image
                    sudo docker run --name hello -d --rm -p 80:5000 marseek/python_image''',
                    execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')],
                    usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
			}
		}
	}
 }
