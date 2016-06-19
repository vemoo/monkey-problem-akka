package com.clluc.monkeyproblem

import akka.actor.Actor

object Monkey {

  sealed trait MonkeyMsg

  case object Go extends MonkeyMsg

  case object Wait extends MonkeyMsg

}

class Monkey extends Actor {
  override def receive: Receive = ???
}
