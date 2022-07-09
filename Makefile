.PHONY: compile build-job-image build-api-async-image build run configure clean

java_home ?= ~/.sdkman/candidates/java/17.0.2-zulu/

compile:
	JAVA_HOME=$(java_home) ./gradlew clean build

build-job-image:
	cd job && docker build -t francesco/job .

build-api-async-image:
	cd api-async && docker build -t francesco/api-async .

build: compile build-job-image build-api-async-image

init:
	docker-compose up -d --remove-orphans

configure:
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/conservazione-request -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/conservazione-response -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/firma-request -d '{"auto_delete":false,"durable":true,"arguments":{}}'
	curl -i -u guest:guest -H "content-type:application/json" -X PUT http://localhost:15672/api/queues/%2f/firma-response -d '{"auto_delete":false,"durable":true,"arguments":{}}'

run:
	docker run --network=host -e SPRING_PROFILES_ACTIVE=firma francesco/job \
& docker run --network=host -e SPRING_PROFILES_ACTIVE=conserva francesco/job \
& docker run --network=host -e SPRING_PROFILES_ACTIVE=firma francesco/api-async \
& docker run --network=host -e SPRING_PROFILES_ACTIVE=conserva francesco/api-async

clean:
	docker-compose down --remove-orphans
