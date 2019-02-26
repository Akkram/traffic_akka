package fr.traffic.akka

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor._

class Smartcrossroadschild extends Actor {

  import Smartcrossroadschild._

  override def preStart() = {
    println("Smartcrossroadschild preStart hook....")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("Smartcrossroadschild preRestart SmartcrossroadsSupervisor...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    println("Smartcrossroadschild postRestart SmartcrossroadsSupervisor...")
    super.postRestart(reason)
  }

  override def postStop() = {
    println("Smartcrossroadschild postStop...")

  }

  def receive = {
    case "Resume" =>
      throw ResumeException
    case "Stop" =>
      throw StopException
    case "Restart" =>
      throw RestartException
    case Sensorcrossroad(name, log, lat, sensors) => distributionTrafficlights(sensors).foreach(println(_))
    case _ => throw new Exception
  }

  def distributionTrafficlights(sensors: Seq[Int]) ={
    val poissondistr=sensors.map(mean => poisson(mean))
    val cycles:Seq[Double]=poissondistr.map(p => p.toDouble / poissondistr.sum)
    val vz=cycles.map(p=>p*poissondistr.sum*5)
    vz
  }

  def poisson(mean: Int): Int = {
    var result = 0
    var random = scala.util.Random
    var a = random.nextDouble()
    var p = Math.exp(-mean)
    while (a > p) {
      result += 1
      a = a - p
      p = p * mean / result
    }
    result
  }
}
object Smartcrossroadschild {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
  case class  Sensorcrossroad(name:String,log:Double,lat:Double,sensors:Seq[Int])
}

class SmartcrossroadsSupervisor extends Actor {
  import Smartcrossroadschild._

  var childRef: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
      case ResumeException      => Resume
      case RestartException     => Restart
      case StopException        => Stop
      case _: Exception            => Escalate
    }


  override def preStart() = {
    // Create Aphrodite Actor
    childRef = context.actorOf(Props[Smartcrossroadschild], "Smartcrossroadschild")
  }

  def receive = {
    case msg =>
      println(s"Smartcrossroads received ${msg}")
      childRef ! msg
  }
}