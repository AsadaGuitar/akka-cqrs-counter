package com.github.AsadaGuitar.akkaCqrsCounter.writeApi

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import akka.util.Timeout
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.controller.CounterController
import com.github.AsadaGuitar.akkaCqrsCounter.writeApi.domain.aggregate.CounterClusterSharding

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App {

  sealed trait Message

  final case class StartAppSucceeded(message: String) extends Message

  final case class StartAppFailed(message: String) extends Message

  def apply(): Behavior[Message] = Behaviors.setup { context =>

    implicit val system: ActorSystem[_] = context.system
    implicit val ec: ExecutionContextExecutor = system.executionContext
    val config = system.settings.config

    val host = config.getString("akka.http.host")
    val port = config.getInt("akka.http.port")

    implicit val askTimeout: Timeout = Timeout(2.seconds)

    val clusterSharding = new CounterClusterSharding(system)
    val counterController = new CounterController(clusterSharding.sharding)

    val bindingFuture =
      Http()
        .newServerAt(host, port)
        .bind(counterController.router)
        .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))

    context.pipeToSelf(bindingFuture) {
      case Failure(exception) =>
        StartAppFailed(s"Failed to bind endpoint, terminating system: ${exception.getMessage}")
      case Success(serverBinding) =>
        val address = serverBinding.localAddress
        StartAppSucceeded(s"Starting server at http://${address.getHostName}/${address.getPort}")
    }

    Behaviors.receiveMessage {
      case StartAppSucceeded(message) =>
        context.log.info(message)
        StdIn.readLine()
        system.terminate()
        Behaviors.same
      case StartAppFailed(message) =>
        context.log.error(message)
        Behaviors.stopped
    }
  }

  ActorSystem(Main(), "akkaCqrsCounter_writeApi")
  StdIn.readLine()
}
