# Swarmfire Example

This project demonstrates how to execute junit tests in a distributed way using [swarmfire](https://github.com/Jotschi/swarmfire).

## Setup

* Download and install [swarmfire](https://github.com/Jotschi/swarmfire)
* Setup your [Docker Swarm](https://docs.docker.com/swarm/install-w-machine/)
* Setup your [Docker Registry](https://github.com/docker/distribution/blob/master/docs/deploying.md)

```
  docker run -d -p 5000:5000 --restart=always --name registry registry:2
```
