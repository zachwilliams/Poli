package com.poc.poli

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._

/**
  * Created by zach on 2/4/16.
  */
object Boot extends App {

  //todo get from config file
  val host = "localhost"
  val port = 8080
  val filename = "data/targetRSS.csv"

  // create an actor system for application
  implicit val system = ActorSystem("poli-service")

  /**
    * start HTTP server with RestServiceActor.scala as handler
    *
    */
  val restService = system.actorOf(Props[RestServiceActor], "rest-server")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

   IO(Http) ! Http.Bind(restService,interface = host, port = port)

  /**
    * schedule RSS Collector to start pulling xml from internet every 10 minutes
    * use the
    * save the xml files
    */
//  system.scheduler.schedule(0 seconds,10 minutes)(RSSCollector.Go(system, filename))
//  RSSCollector.Go(system)  // or just call once for debug

  //for debugging
//  system.terminate()

}
