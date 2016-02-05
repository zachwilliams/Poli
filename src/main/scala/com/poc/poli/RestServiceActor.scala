package com.poc.poli

import akka.actor._
import spray.http.StatusCodes
import spray.routing._
import scala.collection.mutable.ListBuffer


//import com.poc.poli.Protocols._
//object RecordProtocol extends DefaultJsonProtocol{
//  case class RSSRecord(id: String, title: String, description: String)
//  implicit val recordformat = jsonFormat3(RSSRecord)
//}
//import RecordProtocol._

//object JsonImplicits extends DefaultJsonProtocol with SprayJsonSupport{
//  implicit val recordFormat = jsonFormat3(RSSRecord)
//}
//case class RSSRecord(id: String, title: String, description: String)

//case class RSSRecord(id: String, title: String, description: String)
//object RSSRecord extends DefaultJsonProtocol {
//  implicit val format = jsonFormat3(RSSRecord.apply)
//}


/**
  * REST Service actor.
  */
class RestServiceActor extends Actor with RestService {
  implicit def actorRefFactory = context
  def receive = runRoute(routes)
}

/**
  * REST service trait
  * spray code to process rest quests is here
  */
trait RestService extends HttpService with ActorLogging { actor: Actor =>
  import com.poc.poli.Protocols._

  def routes : Route = {

    //TODO get rid of this hardcoded value!
    val rssList = listAllRSS("data/targetRSS.csv")

    pathPrefix("rss"){
      pathPrefix("list") {
        get { requestContext =>
          val responder = context.actorOf(Props(new Responder(requestContext)))
//          responder ! RSSList("everything", rssList)
          responder ! RSSRecord("test", "TEST", "TTTTEEEESSSSTTTT")
        }
      } ~
        pathPrefix(Segment) { rssname =>
          get { requestContext =>
            val responder = context.actorOf(Props(new Responder(requestContext)))
            isValid(rssList,rssname) match {
              case true => responder ! RSSRecord(rssname,"test","test")
              case _ => responder ! NotFound
            }
          }
        }
    }
  }

  /**
    * read through all files in targetRSS.csv and create list
    *
    * @return list of all potential rss files one could go after
    */
  private def listAllRSS(filename: String): List[String] = {

    //TODO make this not shitty efficiency
    var rsslist = new ListBuffer[String]()
    for (line <- io.Source.fromFile(filename).getLines()) {
      rsslist += line.split(",")(0)
    }
    rsslist.toList
  }
  /**
    * @param rsslist list of potential rss files
    * @param rssname target rss source name, from URL
    * @return true if name is a valid target in  data/targetRSS.csv
    */
  private def isValid(rsslist: List[String], rssname: String): Boolean ={
    rsslist.find(x=>x == rssname) match {
      case Some(_) => true
      case None =>  false
    }
  }
}

/**
  * Responder actor receives a message it maps it to a meaningful HTTP
  * response and send it back
  *
  */
class Responder(requestContext:RequestContext) extends Actor with ActorLogging {
  import com.poc.poli.Protocols._
  import com.poc.poli.Protocols.JsonImplicits._
  def receive = {
    case record : RSSRecord =>
      requestContext.complete(StatusCodes.OK, record)
      self ! PoisonPill
//
//    case rsslist : RSSList =>
//      requestContext.complete(StatusCodes.OK, rsslist)
//      self ! PoisonPill
//
    case NotFound =>
      requestContext.complete(StatusCodes.NotFound)
      self ! PoisonPill
  }

}
