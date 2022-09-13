package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate

import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.CborSerializable

sealed trait CounterEvent extends CborSerializable

object CounterEvent {

  final case class CountUpped(id: String, n: Int) extends CounterEvent
}
