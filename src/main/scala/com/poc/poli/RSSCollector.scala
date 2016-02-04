package com.poc.poli

import akka.actor._
import scala.concurrent.ExecutionContext
import scala.xml.XML




// classes used by RSS Collector
case class RSSTarget(tagetName: String, targetUrl: String)

/**
  * actor to make call to target rss feed
  * receives a url to call
  * prints debug info
  */

class RSSGetter extends Actor with ActorLogging{
  def receive = {
    case RSSTarget(name, url) =>
      val response = io.Source.fromURL(url).mkString
      val xmlResponse = XML.loadString(response)


      //save the RSS file to the file system
      val filename = "data/RSSXML/" + name + ".xml"
      try{

        log.info("writting to file " + filename)
        XML.save(filename, xmlResponse,"UTF-8")

      } catch { case e : Exception => log.error(e.toString) }

      //debugging print the first title in the list of titles
      //val titles = xmlResponse \ "channel" \ "item" \ "title"
      //log.info(titles.head.text)

      //clean up these actors as they are dynamically created
      //killYourself
      self ! PoisonPill
  }
}
/**
  * mainline for RSS collection
  * called every 10 minutes, gets list of subscribed feeds
  * create one actor per feed to call and get xml data
  */
object RSSCollector{

  // set implicit context for scheduler
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
//  val system = ActorSystem("RSSRetrievalSystem")

  /**
    * function to read target rss feed list and
    * start an actor to process each one
    */
  def Go(system: ActorSystem, filename: String) = {
    // get list of target rss feeds from file and send to actors for processing
    for (line <- io.Source.fromFile(filename).getLines()) {
      val lineList = line.split(",")
      val act = system.actorOf(Props(new RSSGetter), lineList(0))
      //send name,url to actor for processing
      act ! RSSTarget(lineList(0),lineList(1))
    }
  }
}
