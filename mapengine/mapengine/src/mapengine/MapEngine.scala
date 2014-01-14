package mapengine

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.AddRoutee
import akka.routing.RemoveRoutee
import akka.routing.Router
import akka.routing.BroadcastRouter
import akka.routing.BroadcastRoutingLogic
import akka.routing.BroadcastGroup
import scala.collection.immutable.List
import akka.actor.ActorRef
import akka.routing.Routee
import akka.routing.ActorRefRoutee
import akka.routing.BroadcastPool

object MapEngine extends App {

def neighbors(a: ActorRef, b: ActorRef) ={
  a ! LinkNeighbor(b)
  b ! LinkNeighbor(a)
}
  
val system = ActorSystem("MySystem")
val greeter1 = system.actorOf(Props[PlayerActor], name = "greeter1")
val greeter2 = system.actorOf(Props[PlayerActor], name = "greeter2")
val greeter3 = system.actorOf(Props[PlayerActor], name = "greeter3")

val cell1 = system.actorOf(Props[MapCell], name = "cell1")
val cell2 = system.actorOf(Props[MapCell], name = "cell2")
val cell3 = system.actorOf(Props[MapCell], name = "cell3")

println("Linking Neighbors")

neighbors(cell1, cell2)
neighbors(cell2, cell3 )

println("Adding Players")

cell1 ! EnterCell(greeter1)
cell2 ! EnterCell(greeter2)
cell3 ! EnterCell(greeter3)

Thread.sleep(500)

println("Send Greeting")

greeter1 ! Name("Test1")
Thread.sleep(500)
greeter2 ! Name("Test2")


println("Done")

Thread.sleep(500)
system.shutdown();
}