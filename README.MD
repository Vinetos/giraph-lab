# Giraph-Lab
## Pre-requisites
You must have docker running on your machine. If you don't have it, you can install it from [here](https://docs.docker.com/install/).

## How to run
1. Clone the repository
2. Update the code as you wish
3. Update the `FILE` environment variable in `docker-compse.yml` to point to the file you want to run
```yml
    environment:
      FILE: "example.DummyComputation"
```
4. Run the container to build and execute the code
```
docker compose up --build
```
