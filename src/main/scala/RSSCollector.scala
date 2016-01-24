/**
  * RSS data getter
  * Created by Zach on 1/24/16.
  */


import scala.concurrent.ExecutionContext
import scala.xml._
import akka.actor._

// object sent to RSSgetter actor
case class RSSTarget(targetUrl: String)

/**
  * actor to make call to target rss feed
  * receives a url to call
  * prints debug info
  */
class RSSGetter extends Actor with ActorLogging{
  def receive = {
    case RSSTarget(url) =>
      val response = io.Source.fromURL(url).mkString
      val xmlResponse = XML.loadString(response)

      //debugging print the first title in the list of titles
      val titles = xmlResponse \ "channel" \ "item" \ "title"
      log.info(titles.head.text)

      //TODO add data to db
      //TODO check if there is any new content
      //TODO update db with new content
      //clean up these actors as they are dynamically created
      context.stop(self)

  }
}

/**
  * mainline app
  * every 10 minutes get list of subscribed feeds
  * create one actor per feed to call and get xml data
  *
  */
object RSSCollector extends App{

  // set implicit context for scheduler
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val system = ActorSystem("RSSRetrievalSystem")

  /**
    * function to read target rss feed list and
    * start an actor to process each one
    */
  def RSSGo() = {
    // get list of target rss feeds from file and send to actors for processing
    val fileName = "data/targetRSS.txt"
    var num = 0
    for (line <- io.Source.fromFile(fileName).getLines()) {
      num += 1
      val act = system.actorOf(Props(new RSSGetter), "RSSGetter"+ num)
      act ! RSSTarget(line)
    }
  }

  //for debugging call once
  RSSGo()
  system.terminate()
//  system.scheduler.schedule(0 seconds,10 minutes)(RSSGo)


}

