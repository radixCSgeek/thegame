package mapengine

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.routing.BroadcastPool
import akka.actor.Props
import akka.routing.AddRoutee
import akka.routing.RemoveRoutee

case class Greeting(who: String)
case class Name(what: String)

class PlayerActor extends Actor with ActorLogging {
  val outgoingEventRouter = context.actorOf(BroadcastPool(1).props(Props[NoOp]), "events")

  def receive = {
    case msg: AddRoutee => outgoingEventRouter ! msg;
    case msg: RemoveRoutee => outgoingEventRouter ! msg;
    case Event(msg, player) => msg match {
      case Greeting(who) => log.info("Saw " + who)
    }
    case Name(what) => log.info("Sending Event for "+what)
    			outgoingEventRouter ! Greeting(what)
    case Degraded(msg, _) => msg match {
        case Event(msg, player) => msg match {
          case Greeting(who) => log.info("Saw Degraded " + who)    
        }
    }
  }
}