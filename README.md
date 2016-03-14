# Swarmfire Example

This project demonstrates how to execute junit tests in a distributed way using [swarmfire](https://github.com/Jotschi/swarmfire).

## Setup

* Requirements:

At least wwo hosts that run docker:

* HostA
* HostB

1. Start up a registry on *HostA*

Setup your [Docker Registry](https://github.com/docker/distribution/blob/master/docs/deploying.md)

```
$ docker run -d -p 5000:5000 --restart=always --name registry registry:2
```

2. Create your swarm cluster id on *HostA*

 More details can be found within the [Docker Swarm](https://docs.docker.com/swarm/install-w-machine/) documentation.

```
$ docker run --rm swarm create
```

3. Create swarm on *HostA*

The token hash must match the previously generated container id. We choose the random strategy in order to randomly distribute test containers.

```
$ docker run -d -p 3376:3376 -t swarm manage -H 0.0.0.0:3376 --strategy random token://0ac50ef75c9739f5bfeeaf00503d4e6e
```

4.  Join the cluster for each docker host:

```
$ docker run -d swarm join --addr=HostA:2375 token://0ac50ef75c9739f5bfeeaf00503d4e6e
$ docker run -d swarm join --addr=HostB:2375 token://0ac50ef75c9739f5bfeeaf00503d4e6e
```

Please note that these steps just briefly scratch the surface of docker swarm. The [docker swarm docs](https://docs.docker.com/swarm/install-w-machine/) contain much more information on how to setup a swarm and how to secure it properly.

5. Install and configure swarmfire

Download and install [swarmfire](https://github.com/Jotschi/swarmfire)

This example assumes that you copy your swarmfire file to /usr/local/bin/swarmfire

Create test specific *config.json* file next to you *pom.xml*

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

Lastly it is mandatory to run maven via:

```
mvn package -Dmaven.repo.local=target/.m2
```

This way the maven dependencies will be freshly downloaded into the target folder. This is important since the swarmfire tool will create a dedicated docker image which will include such dependencies.
