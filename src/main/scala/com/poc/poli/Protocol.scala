package com.poc.poli


/**
  * Created by zach on 2/4/16.
  */
object Protocol {
  import spray.json._

  //classes used by HTTP server
  case object NotFound
  case class RSSList(id: String, fullList: List[String])
  case class RSSRecord(id: String, title: String, description: String)

  // for JSON (un)marshalling
  object ListJsonImplicits extends DefaultJsonProtocol {
    implicit val listFormat = jsonFormat2(RSSList.apply)
  }
  object RecordJsonImplicits extends DefaultJsonProtocol {
    implicit val recordFormat = jsonFormat3(RSSRecord.apply)
  }

}
