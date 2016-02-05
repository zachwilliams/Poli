package com.poc.poli

/**
  * spray http needed objects and such
  */
object Protocols {
  import spray.json._
  import spray.httpx.SprayJsonSupport

  //classes used by HTTP server
  case object NotFound
//  case class RSSList(id: String, fullList: List[String])
  case class RSSList(id: String, fullList: String)
  //TODO could just marshal a list of rssTitle(name: String)
  case class RSSRecord(id: String, title: String, description: String)

  // for JSON (un)marshalling
  object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport{
    implicit val listFormat = jsonFormat2(RSSList)
    implicit val recordFormat = jsonFormat3(RSSRecord)
  }

}
