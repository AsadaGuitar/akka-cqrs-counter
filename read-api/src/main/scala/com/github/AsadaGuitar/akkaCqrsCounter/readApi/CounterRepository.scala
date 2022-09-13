package com.github.AsadaGuitar.akkaCqrsCounter.readApi

import slick.basic.DatabaseConfig
import slick.jdbc.PostgresProfile

import scala.concurrent.Future

object CounterRepository {
  import slick.jdbc.PostgresProfile.api._

  private lazy val dbConfig: DatabaseConfig[PostgresProfile] = DatabaseConfig.forConfig("slick")
  private lazy val table = TableQuery[CounterTable]

  def findAll: Future[Seq[CounterRow]] = dbConfig.db.run(table.result)

  def findById(id: String): Future[Option[CounterRow]] = dbConfig.db.run(table.filter(_.id === id).result.headOption)

  def insert(row: CounterRow): Future[Int] = dbConfig.db.run(table.insertOrUpdate(row))

  private class CounterTable(tag: Tag) extends Table[CounterRow](tag, "counter") {

    def id = column[String]("id", O.PrimaryKey)
    def number = column[Int]("number")

    override def * = (id, number) <> ((CounterRow.apply _).tupled, CounterRow.unapply)
  }
}

final case class CounterRow(id: String, number: Int)

