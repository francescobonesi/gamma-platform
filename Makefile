.PHONY: compile build-job-image build run run-background configure clean

java_home ?= ~/.sdkman/candidates/java/17.0.2-zulu/

compile:
	JAVA_HOME=$(java_home) ./gradlew clean build

build-job-image:
	cd job && docker build -t francesco/job .

build: compile build-job-image

init:
	docker-compose up -d --remove-orphans

configure:
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/conservazione-request -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/conservazione-response -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/firma-request -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/firma-response -d '{"auto_delete":false,"durable":true,"arguments":{}}'

run:
	docker run --network=host -e SPRING_PROFILES_ACTIVE=firma francesco/job & docker run --network=host -e SPRING_PROFILES_ACTIVE=conserva francesco/job

run-background:
	docker run -d --network=host francesco/job

clean:
	docker-compose down --remove-orphans
