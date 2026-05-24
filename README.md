# CREDO API Tests

## Run

WireMock-i unda iyos gashvebuli localhost:8080-ze.

martivad gasashvebad:
```
./run-tests.ps1
```

an xelit:
```
docker run --rm -d --name wiremock -p 8080:8080 wiremock/wiremock:2.35.0
mvn test
docker stop wiremock
```

testebis shedegebi inakheba `test-results.db`-shi.

