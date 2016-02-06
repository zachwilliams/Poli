package com.poc.poli

import akka.actor._
import scala.concurrent.ExecutionContext
import scala.xml.XML
import scala.concurrent.duration._




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

      println("writting file")

      //save the RSS file to the file system
      val filename = "data/RSSXML/" + name + ".xml"
      try{

        log.info("writting to file " + filename)
        XML.save(filename, xmlResponse,"UTF-8")

      } catch { case e : Exception => log.error(e.toString) }

      //clean up these actors as they are dynamically created
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
  implicit val context = ActorSystem("poli-collector")


  def start(filename: String) = {


//  context.scheduler.schedule(0 seconds,10 minutes)(go(filename))
  // for debugging use below
  go(filename)
  context.terminate()
}

  /**
    * function to read target rss feed list and
    * start an actor to process each one
    */
  private def go(filename: String) = {
    // get list of target rss feeds from file and send to actors for processing
    for (line <- io.Source.fromFile(filename).getLines()) {
      val lineList = line.split(",")
      val act = context.actorOf(Props(new RSSGetter), lineList(0))
      //send name,url to actor for processing
      act ! RSSTarget(lineList(0),lineList(1))
    }
  }
}
