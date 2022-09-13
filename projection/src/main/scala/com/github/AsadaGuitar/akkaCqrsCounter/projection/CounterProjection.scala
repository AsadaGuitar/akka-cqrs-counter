package com.github.AsadaGuitar.akkaCqrsCounter.projection

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.projection.{ProjectionBehavior, ProjectionId}
import akka.projection.cassandra.scaladsl.CassandraProjection
import akka.projection.eventsourced.scaladsl.EventSourcedProvider
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate.{CounterEvent, CounterTags}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object CounterProjection extends App {

  def apply(): Behavior[String] = Behaviors.setup { context =>

    implicit val system: ActorSystem[_] = context.system
    implicit val ec: ExecutionContextExecutor = system.executionContext

    val sourceProvider =
      EventSourcedProvider
        .eventsByTag[CounterEvent](
          system,
          readJournalPluginId = CassandraReadJournal.Identifier,
          tag = CounterTags.Single)

    val projection =
      CassandraProjection
        .atLeastOnce(
          projectionId = ProjectionId("counters", CounterTags.Single),
          sourceProvider,
          handler = () => new CounterProjectionHandler(system))
        .withSaveOffset(afterEnvelopes = 1, afterDuration = 500.millis)

    ClusterSingleton(system).init(
      SingletonActor(
        ProjectionBehavior(projection),
        projection.projectionId.id)
        .withStopMessage(ProjectionBehavior.Stop))

    Behaviors.empty
  }

  ActorSystem(CounterProjection(), "akkaCqrsCounter_projection")
  StdIn.readLine()
}