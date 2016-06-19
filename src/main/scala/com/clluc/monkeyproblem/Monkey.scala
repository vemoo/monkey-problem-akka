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
  override def receive: Receive = ???
}
