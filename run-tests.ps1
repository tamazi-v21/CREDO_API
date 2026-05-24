# start wiremock, run tests, stop wiremock
docker run --rm -d --name wiremock -p 8080:8080 wiremock/wiremock:2.35.0
Start-Sleep -Seconds 3

try {
    mvn test
} finally {
    docker stop wiremock
}
