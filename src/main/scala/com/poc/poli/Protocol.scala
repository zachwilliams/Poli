package com.poc.poli

import spray.json.DefaultJsonProtocol

/**
  * Created by zach on 2/4/16.
  */
//classes used by HTTP server
case object NotFound
case class RSSList(id: String, fullList: List[String])
case class RSSRecord(id: String, title: String, description: String)
// for JSON (un)marshalling
object ListJsonImplicits extends DefaultJsonProtocol {
  implicit val listFormat = jsonFormat2(RSSList)
}
object RecordJsonImplicits extends DefaultJsonProtocol {
  implicit val recordFormat = jsonFormat3(RSSRecord)
}

// classes used by RSS Collector
case class RSSTarget(tagetName: String, targetUrl: String)
