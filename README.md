# Mendel Challenge (Java Spring Boot API)

## How to run the app

In order to run the REST API server you'll need to:
1. Build a Docker image based on the Dockerfile by stepping at the root of this repository with `docker build -t mendel-challenfe:latest .`
2. Run the container with the built image with `docker run -p 8080:8080 mendel-challenge:latest`

The server should be ready at `http://localhost:8080`.

## Endpoints

- **PUT** `/transactions/{transactionId}` with the following body (`parent_id` being optional) to insert a transaction:
```json
{
  "amount": double,
  "type": string,
  "parent_id": long
}
```
- **GET** `/transactions/types/{transactionType}` to get all the transaction ids for transactions of that type.
- **GET** `/transactions/sum/{transactionId}` to get the sum of all the descendants of this transaction.