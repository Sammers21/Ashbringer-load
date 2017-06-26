# ashbringer
Load testing Framework

![image](https://user-images.githubusercontent.com/16746106/27516254-0d7556a2-59bf-11e7-8b3d-7709e5283341.png)

This framework consist of 4 modules: core, cli, bechmark and japontro server.

You should use cli in case you want to test your http server.

Usage
```
$ mvn clean install
$ java -jar ashbringer-cli/target/ashbringer-cli-1.0-SNAPSHOT.jar \
  <time> <host> <port> <max_sessions> <nThreads> <schme> <path>
```
Default values for:
* time          - 0 mean infinite time
* host          - 0.0.0.0 or localhost
* port          - 80
* max_sessions  - 2
* nThreads      - 1
* scheme        - http (it also support https)
* path          - "" or nothing

# Reached maximum
*hardware*: 2xCPU,7 gb RAM

*os*: Ubuntu 16.04.2 LTS (GNU/Linux 4.4.0-81-generic x86_64)

*java*: openJDK 1.8.0_131 x64 server mode

![image](https://user-images.githubusercontent.com/16746106/27547155-b6cf157e-5a9d-11e7-905c-3bebab56f636.png)
