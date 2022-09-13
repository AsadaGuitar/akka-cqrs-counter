package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate

import akka.actor.typed.ActorSystem
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import akka.persistence.typed.PersistenceId

object CounterClusterSharding {

  val TypeKey: EntityTypeKey[CounterAggregateProtocol.CounterCommand] =
    EntityTypeKey[CounterAggregateProtocol.CounterCommand]("CounterShard")
}

final class CounterClusterSharding(system: ActorSystem[_]) {
  import CounterClusterSharding._

  val sharding: ClusterSharding = ClusterSharding(system)

  sharding.init(
    Entity(typeKey = TypeKey) { entityContext =>
      CounterAggregate(
        PersistenceId(
          entityTypeHint = entityContext.entityTypeKey.name,
          entityId = entityContext.entityId))
    }
  )
}