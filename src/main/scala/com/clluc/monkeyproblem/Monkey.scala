package com.clluc.monkeyproblem

import akka.actor.{Actor, ActorRef, Props}

object Monkey {

  sealed trait Command

  /**
    * Tells the monkey to get on the rope
    */
  case object Go extends Command

  case object Wait extends Command

  def props(id: Int, direction: Direction, rope: ActorRef): Props = Props(new Monkey(id, direction, rope))
}

class Monkey(val id: Int, val direction: Direction, val rope: ActorRef) extends Actor {

  import com.clluc.monkeyproblem.Monkey._
  import context.dispatcher

  import scala.concurrent.duration._

  override def preStart: Unit = {
    rope ! Rope.Join(direction)
    println(s"$this asking to join")
  }

  override def receive: Receive = {
    case Wait =>
      println(s"$this waiting")
      ()
    case Go =>
      rope ! Rope.Joining
      println(s"$this getting on the rope")
      //remind ourselves to tell rope we joined
      context.system.scheduler.scheduleOnce(1.second, self, Rope.Joined)
    case Rope.Joined =>
      rope ! Rope.Joined
      println(s"$this is on the rope")
      //remind ourselves to tell rope we left
      context.system.scheduler.scheduleOnce(4.seconds, self, Rope.Left)
    case Rope.Left =>
      rope ! Rope.Left
      println(s"$this left")
      context.stop(self)
  }

  override def toString = s"Monkey($id, $direction)"
}
