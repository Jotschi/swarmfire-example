# Swarmfire Example

This project demonstrates how to execute junit tests in a distributed way using [swarmfire](https://github.com/Jotschi/swarmfire).

## Setup 

This example shows how to setup the docker environment using docker-machine

### Start up a registry

Setup your [Docker Registry](https://github.com/docker/distribution/blob/master/docs/deploying.md)

```
$ docker run -d -p 5000:5000 --restart=always --name registry registry:2
```

### Create your swarm cluster id on *HostA*

More details can be found within the [Docker Swarm](https://docs.docker.com/swarm/install-w-machine/) documentation.

```
docker-machine create -d virtualbox --engine-env DOCKER_TLS=no --engine-insecure-registry YOUR_REGISTRY_HOST:5000 manager
docker-machine create -d virtualbox --engine-env DOCKER_TLS=no --engine-insecure-registry YOUR_REGISTRY_HOST:5000 agent1
docker-machine create -d virtualbox --engine-env DOCKER_TLS=no --engine-insecure-registry YOUR_REGISTRY_HOST:5000 agent2

docker -H tcp://$(docker-machine ip manager):2376 run --rm swarm create

# The token hash must match the previously generated container id. We choose the random strategy in order to randomly distribute test containers.

docker -H tcp://$(docker-machine ip manager):2376 run -d -p 3376:3376 -t swarm manage -H 0.0.0.0:3376  --strategy random  token://830700a5f0a5e5537077a2a61851652d

#  Join the cluster for each docker host:
docker -H tcp://$(docker-machine ip agent1):2376 run -d swarm join --addr=$(docker-machine ip agent1):2376 token://830700a5f0a5e5537077a2a61851652d
docker -H tcp://$(docker-machine ip agent2):2376 run -d swarm join --addr=$(docker-machine ip agent2):2376 token://830700a5f0a5e5537077a2a61851652d

docker -H tcp://$(docker-machine ip manager):3376 ps
```

Please note that these steps just briefly scratch the surface of docker swarm. The [docker swarm docs](https://docs.docker.com/swarm/install-w-machine/) contain much more information on how to setup a swarm and how to secure it properly.

### Install and configure swarmfire

Download and install [swarmfire](https://github.com/Jotschi/swarmfire)

This example assumes that you copy your swarmfire file to /usr/local/bin/swarmfire

Create test specific *swarmfire-config.json* file next to you *pom.xml*

```
{
 "baseImageName": "jotschi/swarmfire",
 "contextImageName": "HostA:5000/test_context_image_1",
 "dockerswarm": "tcp://HostA.sky:3376",
 "command": ["java", "-jar"]
}
```

Adapt *maven-surefire-plugin* configuration. The *jvm* parameter needs to point to the swarmfire tool. The *argLine* command sets the swarmfire command argument and the *useManifestOnlyJar* and *useSystemClassLoader* settings are needed in order to create the correct maven surefire output files.

```
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.19.1</version>
      <configuration>
        <argLine>-c run</argLine>
        <jvm>/usr/local/bin/swarmfire</jvm>
        <!-- 10 parallel tests -->
        <forkCount>10</forkCount>
        <useManifestOnlyJar>true</useManifestOnlyJar>
        <useSystemClassLoader>true</useSystemClassLoader>
      </configuration>
    </plugin>
  </plugins>
</build>
```

### Run Test

Lastly it is mandatory to run maven via:

```
$ mvn package -Dmaven.repo.local=target/.m2
```

This way the maven dependencies will be freshly downloaded into the target folder. This is important since the swarmfire tool will create a dedicated docker image which will include such dependencies.
