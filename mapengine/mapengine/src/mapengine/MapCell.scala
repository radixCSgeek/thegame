package mapengine

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.routing.BroadcastPool
import akka.actor.Props
import akka.routing.AddRoutee
import akka.routing.ActorRefRoutee
import akka.routing.RemoveRoutee

class MapCell extends Actor with ActorLogging {
  val incomingRouter = context.actorOf(BroadcastPool(1).props(Props[NoOp]), "incoming")
  val outgoingRouter = context.actorOf(BroadcastPool(1).props(Props[NoOp]), "outgoing")
  val eventHandler = context.actorOf(Props(classOf[Retransmit], outgoingRouter, self), "eventHandler")
  
  def enter(who: ActorRef) = {
    incomingRouter ! AddRoutee(ActorRefRoutee(who))
    who ! AddRoutee(ActorRefRoutee(eventHandler))
  }
  
  def leave(who: ActorRef) = {
    incomingRouter ! RemoveRoutee(ActorRefRoutee(who))
    who ! RemoveRoutee(ActorRefRoutee(eventHandler))
  }
  
  def linkNeighbor(neighbor: ActorRef) = {
    outgoingRouter ! AddRoutee(ActorRefRoutee(neighbor))
  }
  
 def receive = {
    //Filter out "echos"
    case msg: Degraded => if(msg.origSender.compareTo(self)!=0) incomingRouter ! msg
    case EnterCell(who) => enter(who)
    case LeaveCell(who) => leave(who)
    case LinkNeighbor(neighbor) => linkNeighbor(neighbor)
    case msg => incomingRouter ! msg
    		outgoingRouter ! Degraded(msg, sender)
  }
}

case class Degraded(msg: Any, origSender: ActorRef)
case class EnterCell(who: ActorRef)
case class LeaveCell(who: ActorRef)
case class LinkNeighbor(neighbor: ActorRef)


class NoOp extends Actor {def receive = {case _ => ;}}

class Retransmit(router: ActorRef, cell: ActorRef) extends Actor {
  def receive = {
    case msg => router.tell(msg, cell)
  }
}
  

