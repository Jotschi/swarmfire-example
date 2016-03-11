# Swarmfire Example

This project demonstrates how to execute junit tests in a distributed way using *swarmfireJVM* by using docker swarm and maven.

* TL,DR
  * A new docker container is started which runs the JVM instead of forking a JVM on the host. This way junit tests can be distributed across multiple hosts.

It is possible to execute junit tests in a multithreaded fashion using the forkcount parameter. Additionally the JVM can also be specified.
Normally a new JVM will be forked when the reuseFork parameter is set to false and threadCount is set to 1.
Instead of the host JVM a docker container will be spawned which will execute the test. The dockerJVM tool will spawn a new docker container.

## Workflow

* Run mvn with -Dmaven.repo.local=target/.m2
* Invocation of dockerJVM -c build using the exec plugin in order to create a new test context image which contains .m2 files and classes
* Invocation of surfire:test using dockerJVM for JVM parameter. This way a new docker container will be created and started

## swarmfireJVM

[SwarmfireJVM](https://github.com/Jotschi/swarmfire/tree/swarmfireJVM)
