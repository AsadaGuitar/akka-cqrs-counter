package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain

final case class Counter(number: Int = 0) {

  def countUp(number: Int): Counter =
    this.copy(number = this.number + number)
}
