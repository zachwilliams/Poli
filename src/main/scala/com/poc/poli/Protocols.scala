package com.poc.poli

/**
  * spray http needed objects and such
  */
object Protocols {
  import spray.json._
  import spray.httpx.SprayJsonSupport

  //classes used by HTTP server
  case object NotFound
  case object NothingNew
  case class RSSRecord(id: String, title: String, link: String, description: String, pubDate: String)

  // for JSON (un)marshalling
  object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport{
    implicit val recordFormat = jsonFormat5(RSSRecord)
  }

}
