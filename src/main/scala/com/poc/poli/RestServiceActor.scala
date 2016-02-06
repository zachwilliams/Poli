package com.poc.poli

import akka.actor._
import spray.http.StatusCodes
import spray.routing._
import scala.collection.mutable.ListBuffer
import scala.xml.XML

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
          responder ! rssList
        }
      } ~
        pathPrefix(Segment) { rssname =>
          get { requestContext =>
            val responder = context.actorOf(Props(new Responder(requestContext)))
            isValid(rssList,rssname) match {
              case true =>
                val xml = getAllRSS(rssname)
                responder ! buildFirstRSS(rssname,xml)
              case _ => responder ! NotFound
            }
          }
        }
    }
  }

  /**
    * take full xml and return a RSSRecord which contains the fields
    * from the most recent push from the RSS file
    * This is probably not the most efficient way to do this
    * (too handcodedy)
    *
    * @param fullxml entire RSS xml
    * @return RSSRecord containing most recent values from RSS
    */
  private def buildFirstRSS(target: String, fullxml: xml.Elem): RSSRecord = {
    //TODO serious potential errors if tag names aren't correct
    //TODO this is bad to have all these hard coded
    val titles = fullxml \ "channel" \ "item" \ "title"
    val links = fullxml \ "channel" \ "item" \ "link"
//    val descriptions = fullxml \ "channel" \ "item" \ "description"
    val pubDate = fullxml \ "channel" \ "item" \ "pubDate"

    log.info("returning record with title: "+ titles.head.text)

    RSSRecord(
      target,
      titles.head.text,
      links.head.text,
      "fixing the description is on the todo list",
//      descriptions.head.text,
      pubDate.head.text
    )
  }
  /**
    * private function to get full xml text from the file
    *
    * @param target name of target xml  (rss) file
    * @return full xml from target rss file
    */
  private def getAllRSS(target: String): xml.Elem = {

    //get RSS file to the file system
    //TODO get rid of this hardcoded value!
    val filename = "data/RSSXML/" + target + ".xml"
    val xml = XML.loadFile(filename)

    log.info("read RSS file: "+filename)
    xml
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
  */
class Responder(requestContext:RequestContext) extends Actor with ActorLogging {
  import com.poc.poli.Protocols._
  import com.poc.poli.Protocols.JsonImplicits._
  def receive = {
    case record : RSSRecord =>
      requestContext.complete(StatusCodes.OK, record)
      self ! PoisonPill

    case recordlist : List[String] =>
      requestContext.complete(StatusCodes.OK, recordlist)
      self ! PoisonPill

    case NotFound =>
      requestContext.complete(StatusCodes.NotFound)
      self ! PoisonPill
  }

}
