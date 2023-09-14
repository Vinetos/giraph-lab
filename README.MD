# Giraph-Lab
This project is a simple lab to test Giraph graph computation. It is based on the [Giraph Quick Start](http://giraph.apache.org/quick_start.html) tutorial.
## Pre-requisites
You must have docker running on your machine. If you don't have it, you can install it from [here](https://docs.docker.com/install/).

## How to run
1. Clone the repository
2. Update the code as you wish
3. Update the input graph with `graph.txt`
4. Update the `FILE` environment variable in `docker-compse.yml` to point to the file you want to run
```yml
    environment:
      FILE: "example.DummyComputation"
```
5. Run the container to build and execute the code
```
docker compose up --build
```
