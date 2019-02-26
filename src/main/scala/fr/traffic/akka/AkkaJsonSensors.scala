package fr.traffic.akka

import java.util.concurrent.ConcurrentLinkedDeque
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._
import scala.concurrent.duration._
import akka.util.Timeout
import scala.collection.JavaConverters._

case class Sensor(name:String,lat:Double,log:Double,sensors:Seq[Int])
object ServiceJsonProtoocol extends DefaultJsonProtocol {
  implicit val sensorProtocol = jsonFormat4(Sensor)
}

object AkkaJsonSensors extends App {


    implicit val actorSystem = ActorSystem("rest-api")

    implicit val actorMaterializer = ActorMaterializer()

    val list = new ConcurrentLinkedDeque[Sensor]()
    val SmartcrossroadsSupervisor = actorSystem.actorOf(Props[SmartcrossroadsSupervisor], "SmartcrossroadsSupervisor")
    import ServiceJsonProtoocol.sensorProtocol

    val route =
      path("sensor") {
        post {
          entity(as[Sensor]) {
            sensor => complete {
             list.add(sensor)

              implicit val timeout = Timeout(1 seconds)
              val future = SmartcrossroadsSupervisor ! sensor
              s"got sensor with name ${StatusCodes.OK}"
            }
          }
        } ~
          get {
            complete {

              list.asScala
            }
          }
      }

    Http().bindAndHandle(route, "localhost", 8080)

}