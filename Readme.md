# RESTful API for money transfers between accounts.

## Requirements

You'll need Java 8 to compile and run the application. You'll also need maven to build it.

## How to I build it and run the tests?
Execute `mvn package` from the project's folder.

## How do I run it?
Execute
```
mvn exec:java
```

Alternatively you can run it following these steps

Build the application with
```
mvn package
```
Tests may take a minute or so you can build the jar without running them
```
mvn package -Dmaven.test.skip=true
```

Assuming you are still in the root folder of the repo run the jar file from the console issuing the following command:
```
java -jar target/money-transfers-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Usage examples
Official documentation id not present but you can find exaple of usages bellow
1. Initiate the creation of some test accounts.
Returns the account IDs
Subsequent calls to this endpoint will create additional accounts per call with balance > 100.000 and < 900.000
```
curl -v -X GET http://kottbullar:9989/api/accounts/create_test?amount=9
```

2. You can create a new account by calling this endpoint
```
curl -H "Content-Type:application/json" -X POST http://localhost:9989/api/create -d "{\"balance\":"200",\"currency\":\"EUR\"}"
```

3. Get the information regarding account with ID=8
```
curl -v http://kottbullar:9989/api/account/8
```

4. Transfer 200 euros from account 8 to account 7
```
curl -H "Content-Type:application/json" -X POST http://localhost:9989/api/transfer -d "{\"fromAccountId\":8,\"toAccountId\":7,\"transferAmount\":\"200\"}"
```

## Implementation notes
1. There is only one currency used for simplicity and there is no multi currency support during the transfer.
It can be expanded throught the injection of a FX Converter and expending the Currency class to include more currencies
or even use the standard java.util.Currencies
2. The account class only holds the minimum information that is balance which is defined as BigDecimal and currency
3. The storage that is used is a key-value in memory Concurrent Map and all accounts are gone once you stop the application
4. The application is designed with concurrency in mind and there is an equivalent unit test that checks this assumption
5. The Spring framework is not used. Instead the Jersey HTTP server is used which is more lightweight and also serves
as a reference implementation for JAX-RS. JAX-RS is a specification defining a set of Java APIs for the development of Web services
built according to the REST architectural style).
6. There is a helper endpoint (api/accounts/create_test?amount=X) that can create X accounts for testing purposes with a
balance between 100_000 and 900_000.
7. Port and URL that the server is running are hardcoded to `localhost:9989`

Have fun!