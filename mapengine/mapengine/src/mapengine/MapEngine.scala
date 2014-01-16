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
  
def move(who: ActorRef, from:ActorRef, to:ActorRef) ={
  from ! LeaveCell(who)
  to ! EnterCell(who)
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

val map = Array.ofDim[ActorRef](12, 12) //leave a null border
for(x <- 1 to 10){
  for(y <- 1 to 10){
    if(((y==1 || y==10) && (x>3 && x<8)) || ((x==1 || x==10) && (y>3 && y<8))){
      map(x)(y) = null
    }
    else{
      map(x)(y) = system.actorOf(Props[MapCell], name = "cell"+x+"_"+y)
    }
  }
}

for(x <- 1 to 10){
  for(y <- 1 to 10){
    if(map(x)(y) != null)
    {
      if(map(x-1)(y-1)	!= null) map(x)(y) ! LinkNeighbor(map(x-1)(y-1))
      if(map(x)(y-1) 	!= null) map(x)(y) ! LinkNeighbor(map(x)(y-1))
      if(map(x+1)(y-1) 	!= null) map(x)(y) ! LinkNeighbor(map(x+1)(y-1))
      if(map(x-1)(y) 	!= null) map(x)(y) ! LinkNeighbor(map(x-1)(y))
      if(map(x+1)(y) 	!= null) map(x)(y) ! LinkNeighbor(map(x+1)(y))
      if(map(x-1)(y+1) 	!= null) map(x)(y) ! LinkNeighbor(map(x-1)(y+1))
      if(map(x)(y+1) 	!= null) map(x)(y) ! LinkNeighbor(map(x)(y+1))
      if(map(x+1)(y+1) 	!= null) map(x)(y) ! LinkNeighbor(map(x+1)(y+1))
    }
  }
}
Thread.sleep(500)
      
val player1 = system.actorOf(Props[PlayerActor], name = "player1")
val player2 = system.actorOf(Props[PlayerActor], name = "player2")

map(3)(5) ! EnterCell(player1)
map(4)(6) ! EnterCell(player2)

Thread.sleep(500)

player1 ! Name("Yo!")
Thread.sleep(5000)
move(player1, map(3)(5), map(4)(6))
Thread.sleep(500)
player1 ! Name("Yo2") //TODO: Not showing up to other in the same cell
Thread.sleep(5000)
move(player1, map(4)(6), map(5)(7))
Thread.sleep(500)
player1 ! Name("Yo3")
Thread.sleep(5000)
move(player1, map(5)(7), map(6)(8))
Thread.sleep(500)
player1 ! Name("Yo4")
Thread.sleep(5000)
move(player1, map(6)(8), map(7)(9))
Thread.sleep(500)
player1 ! Name("Yo5")

Thread.sleep(500)
system.shutdown();
}