package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate

import akka.actor.typed.ActorRef
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.CborSerializable
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.Counter

object CounterAggregateProtocol {

  sealed trait CounterCommand extends CborSerializable

  final case class CountUp(id: String, n: Int)(val replyTo: ActorRef[CountUpReply]) extends CounterCommand

  sealed trait CountUpReply extends CborSerializable

  final case class CountUpSucceededReply(counter: Counter) extends CountUpReply

  final case class CountUpFailedReply(e: Exception) extends CountUpReply

}
