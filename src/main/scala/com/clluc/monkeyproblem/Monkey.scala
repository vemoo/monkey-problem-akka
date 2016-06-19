package com.clluc.monkeyproblem

import akka.actor.Actor

object Monkey {

  sealed trait Command

  case object Go extends Command

  case object Wait extends Command

}

class Monkey extends Actor {
  override def receive: Receive = ???
}
