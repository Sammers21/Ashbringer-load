# ashbringer
Load testing Framework

![image](https://user-images.githubusercontent.com/16746106/27516254-0d7556a2-59bf-11e7-8b3d-7709e5283341.png)

This framework consist of 3 modules: core, cli, bechmark and japontro server.

You should use cli in case you want to test your http server.

Usage
```
$ mvn clean install
$ java -jar ashbringer-cli/target/ashbringer-cli-1.0-SNAPSHOT.jar <time> <host> <port> <max_sessions> <nThreads> <path>
```
Default values for:
* time          - 0 mean infinite time
* host          - 0.0.0.0 or localhost
* port          - 80
* max_sessions  - 2
* nThreads      - 1
* path          - "" or nothing

# Reached maximum
![image](https://user-images.githubusercontent.com/16746106/27520310-d32e2e86-5a10-11e7-9582-50cefc6f16d1.png)
