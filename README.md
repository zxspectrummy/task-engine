# RabbitMQ-based Task Engine Example

## Decision-Making
### Problem
A customer requires MVP of a task execution engine that should provide high scalability and availability

### Solution Proposal
SpringBoot AMQP is a popular and well-known library that is widely used for implementing asynchronous communication.

## Usage

### Build
    mvn clean package

### Run as service
    docker-compose up -d

### Run publisher and subscribers locally

    mvn spring-boot:run -Dspring-boot.run.profiles=sub
    mvn spring-boot:run -Dspring-boot.run.profiles=pub

### Running tests
    mvn test

## REST API


### Create a new task

#### Request

`POST /tasks/`

    curl --location --request POST 'http://localhost:8080/api/v1/tasks' \
        --header 'Content-Type: application/json' \
        --data-raw '{ "command": "sleep 1 && echo '\''hello its me'\''"}'

#### Response

    {
        "id": "15913ec1-1a6b-4d5d-a3ab-23cf253fe2a7",
        "command": "sleep 1 && echo 'hello its me'",
        "stdout": null,
        "stderr": null,
        "state": "IN_PROGRESS",
        "exitCode": 0
    }

### Get a task by id

#### Request

`GET /task/<id>`

    curl -i -H 'Accept: application/json' http://localhost:8080/tasks/15913ec1-1a6b-4d5d-a3ab-23cf253fe2a7

#### Response
    {
        "id": "15913ec1-1a6b-4d5d-a3ab-23cf253fe2a7",
        "command": "sleep 1 && echo bla10",
        "stdout": null,
        "stderr": null,
        "state": "IN_PROGRESS",
        "exitCode": 0
    }

### Get a tasks list
#### 
`GET /task/<id>`

    curl -i -H 'Accept: application/json' http://localhost:8080/tasks
#### Response
    {
        [
            {
                "id": "15913ec1-1a6b-4d5d-a3ab-23cf253fe2a7",
                "command": "sleep 1 && echo bla10",
                "stdout": null,
                "stderr": null,
                "state": "IN_PROGRESS",
                "exitCode": 0
            },
            {
                "id": "8e452067-0fd9-482d-abb6-eefc075cdae0",
                "command": "ping -n3 8.8.8.8",
                "stdout": null,
                "stderr": null,
                "state": "FINISHED",
                "exitCode": 0
            }
        ]
    }

## Future Considerations
### Monitoring
Prometheus can be used to monitor status of all the components including the database and RabbitMQ

### Security
AMQP supports transport-level security using TLS. We might want to use in production.
