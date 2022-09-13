package com.github.AsadaGuitar.akkaCqrsCounter.writeApi.controller

import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate.{CounterAggregateProtocol, CounterClusterSharding}

import scala.util.{Failure, Success}

final class CounterController(clusterSharding: ClusterSharding)(implicit askTimeout: Timeout) {

  val router: Route =
    pathPrefix("count-up" / Segment) { id =>
      post {
        val ref = clusterSharding.entityRefFor(CounterClusterSharding.TypeKey, id)
        pathPrefix(IntNumber) { number =>
          val result = ref.ask(CounterAggregateProtocol.CountUp(id, number))
          onComplete(result) {
            case Failure(_) =>
              complete(InternalServerError)
            case Success(reply) =>
              reply match {
                case CounterAggregateProtocol.CountUpSucceededReply(counter) =>
                  complete(s"Added number $number, counter=${counter.number}")
                case CounterAggregateProtocol.CountUpFailedReply(_) =>
                  complete(InternalServerError)
              }
          }
        }
      }
    }
}
