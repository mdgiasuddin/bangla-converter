# Phonetic Bengali editor

## Run with Docker (one command)

    docker compose up -d --build

Open http://localhost:8080

Useful commands:

    docker compose logs -f          # follow the logs
    docker compose ps               # status (shows "healthy" once started)
    docker compose down             # stop and remove
    APP_PORT=9090 docker compose up -d --build   # use another public port
    docker compose build --build-arg MVN_ARGS="" # build and run the unit tests

## Run without Docker

    mvn spring-boot:run
