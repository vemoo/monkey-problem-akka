package com.clluc.monkeyproblem

import akka.actor.{Actor, ActorRef, Props}

object Monkey {

  sealed trait Command

  /**
    * Tells the monkey to get on the rope
    */
  case object Go extends Command

  case object Wait extends Command

  def props(direction: Direction, rope: ActorRef): Props = Props(new Monkey(direction, rope))
}

class Monkey(val direction: Direction, val rope: ActorRef) extends Actor {

  import context.dispatcher
  import com.clluc.monkeyproblem.Monkey._
  import scala.concurrent.duration._

  override def preStart: Unit = {
    rope ! Rope.Join(direction)
  }

  override def receive: Receive = {
    case Wait => ()
    case Go =>
      rope ! Rope.Joining
      //remind ourselves to tell rope we joined
      context.system.scheduler.scheduleOnce(1.second, self, Rope.Joined)
    case Rope.Joined =>
      rope ! Rope.Joined
      //remind ourselves to tell rope we left
      context.system.scheduler.scheduleOnce(4.seconds, self, Rope.Left)
    case Rope.Left =>
      rope ! Rope.Left
      context.stop(self)
  }
}
