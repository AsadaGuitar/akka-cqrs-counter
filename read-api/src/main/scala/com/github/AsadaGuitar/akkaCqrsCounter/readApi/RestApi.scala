package com.github.AsadaGuitar.akkaCqrsCounter.readApi

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.io._
import org.http4s.server.Router

object RestApi extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      params <- IO{
        val config = ConfigFactory.load()
        (config.getString("http.host"), config.getInt("http.port"))
      }.handleError(_ => ("localhost", 8080))
      (host, port) = params
      server <- BlazeServerBuilder[IO]
        .bindHttp(port, host)
        .withHttpApp(app)
        .resource
        .useForever
        .as(ExitCode.Success)
    } yield server
  }

  val router: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "counter" / str =>
      val counter = IO.fromFuture(IO(CounterRepository.findById(str)))
      counter.flatMap {
        case Some(value) => Ok(s"Exists Counter id=$str number=${value.number}")
        case None => NotFound(s"Not Found counter id=$str.")
      }
    case GET -> Root / "counters" =>
      val counters = IO.fromFuture(IO(CounterRepository.findAll))
      counters.flatMap { list =>
        val msg = "Exists Counter : " ++ list.map{ row =>
          s"id=${row.id} number=${row.number}"
        }.mkString(", ")
        Ok(msg)
      }
  }

  val app: Kleisli[IO, Request[IO], Response[IO]] = Router(
    "/" -> router
  ).orNotFound
}
