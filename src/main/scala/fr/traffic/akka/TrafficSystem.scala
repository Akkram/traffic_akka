package fr.traffic.akka

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import fr.traffic.akka.Smartcrossroadschild.Sensorcrossroad
import scala.concurrent.duration._

object TrafficSystem  extends App {

    // Create the 'supervision' actor system
    val system = ActorSystem("supervision")

    // Create smartcrossroad Actor
    val SmartcrossroadsSupervisor = system.actorOf(Props[SmartcrossroadsSupervisor], "SmartcrossroadsSupervisor")


    implicit val timeout = Timeout(1 seconds)
    val future = SmartcrossroadsSupervisor ! Sensorcrossroad("crossroad A",4.282800,12.282800,Seq(40,60,20,10))

    /*future.onComplete{
        case util.Success(name) =>
            println(s"success: $name")
        case util.Failure(ex) =>
            println(s"FAIL: ${ex.getMessage}")
            ex.printStackTrace()
    }*/

    system.terminate()



}
