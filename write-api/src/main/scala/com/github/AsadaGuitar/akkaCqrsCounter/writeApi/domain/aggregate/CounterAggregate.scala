package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate

import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, RetentionCriteria}
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.Counter

import scala.concurrent.duration.DurationInt

object CounterAggregate {

  private def commandHandler(ctx: ActorContext[CounterAggregateProtocol.CounterCommand]):
  (Counter, CounterAggregateProtocol.CounterCommand) => Effect[CounterEvent, Counter] =
    (_, command) =>
      command match {
        case cmd@CounterAggregateProtocol.CountUp(id, n) =>
          Effect.persist(CounterEvent.CountUpped(id, n)).thenReply(cmd.replyTo) { state =>
            ctx.log.info(s"counter added: $state")
            CounterAggregateProtocol.CountUpSucceededReply(state)
          }
      }

  private def eventHandler: (Counter, CounterEvent) => Counter =
    (state, event) =>
      event match {
        case CounterEvent.CountUpped(_, n) => state.countUp(n)
      }

  def apply(persistenceId: PersistenceId): Behavior[CounterAggregateProtocol.CounterCommand] =
    Behaviors.setup { ctx =>
      ctx.log.info(s"Generate actor `Counter`. id=${persistenceId.id}")
      EventSourcedBehavior(
        persistenceId = persistenceId,
        emptyState = Counter(),
        commandHandler = this.commandHandler(ctx),
        eventHandler = this.eventHandler)
        .withTagger(_ => Set(CounterTags.Single))
        .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 10, keepNSnapshots = 3))
        .onPersistFailure(SupervisorStrategy.restartWithBackoff(200.millis, 5.seconds, 0.1))
    }
}
