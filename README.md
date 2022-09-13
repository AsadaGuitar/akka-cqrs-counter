# akka-cqrs-counter
Akka Projection Version 1.2.5 sample project.<br>
The system configuration assumes [CQRS](https://docs.microsoft.com/ja-jp/azure/architecture/patterns/cqrs), [EventSourcing](https://docs.microsoft.com/ja-jp/azure/architecture/patterns/event-sourcing).<br>
**This project is not intended for a production environment. Please use this information to get an overall picture.**

## Table of Contents
1. [Features](#anchor1)
2. [System Architecture](#anchor2)
3. [Getting Started Guide](#anchor3)
4. [Services Provided](#anchor4)

<a id="anchor1"></a>
## Features
+ Scala version 2.13.8
+ Akka
+ http4s
+ Event Sourcing
+ DDD
+ CQRS

<a id="anchor2"></a>
## System Architecture
### Diagrams
![akka-cqrs-counter](https://user-images.githubusercontent.com/79627592/190022508-cdf60166-caee-40fa-b858-83c24978518b.png)

### Description & Dependency Libraries
- Write API
  - This project builds *ClusterSharding* and provides event writing.
  - The project is structured around [Akka](https://akka.io/docs/).
  - Dependency Libraries
    - "com.typesafe.akka" %% "akka-actor-typed"            % akkaVersion
    - "com.typesafe.akka" %% "akka-persistence-typed"      % akkaVersion
    - "com.typesafe.akka" %% "akka-persistence-cassandra"  % cassandraPersistenceVersion
    - "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion
    - "com.typesafe.akka" %% "akka-http"                   % akkaHttpVersion
- Read API
  - read-api provides an Api to get a simple *Counter*. **Also, due to Akka license changes, this project uses http4s.**
  - Dependency Libraries
    - "org.http4s" %% "http4s-server" % http4sVersion
    - "org.http4s" %% "http4s-blaze-server" % http4sVersion
    - "org.http4s" %% "http4s-dsl" % http4sVersion
    - "com.typesafe.slick" %% "slick" % slickVersion
    - "org.postgresql" % "postgresql" % postgresVersion
- Projection

### Databases
- KVS for write-api.
  - [Apache Cassandra](https://aws.amazon.com/jp/keyspaces/)
- RDMS for read-api
  - [Amazon Aurora Postgresql](https://aws.amazon.com/jp/rds/aurora/)


<a id="anchor3"></a>
## Getting Started Guide

<a id="anchor4"></a>
## Services Provided
