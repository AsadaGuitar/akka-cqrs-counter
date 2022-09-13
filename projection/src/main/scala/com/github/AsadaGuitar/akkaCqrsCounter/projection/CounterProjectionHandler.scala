package com.github.AsadaGuitar.akkaCqrsCounter.projection

import akka.Done
import akka.actor.typed.ActorSystem
import akka.projection.eventsourced.EventEnvelope
import akka.projection.scaladsl.Handler
import com.github.AsadaGuitar.akkaCqrsCounter.readApi.{CounterRepository, CounterRow}
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate.CounterEvent

import scala.concurrent.{ExecutionContext, Future}

final class CounterProjectionHandler(system: ActorSystem[_])
  extends Handler[EventEnvelope[CounterEvent]] {

  private implicit val ec: ExecutionContext = system.executionContext

  override def process(envelope: EventEnvelope[CounterEvent]): Future[Done] = {
    envelope.event match {
      case CounterEvent.CountUpped(id, number) =>
        val row = CounterRow(id, number)
        CounterRepository.insert(row).map(_ => Done)
    }
  }
}
