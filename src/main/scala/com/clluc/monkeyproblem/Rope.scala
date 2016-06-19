package com.clluc.monkeyproblem

import akka.actor.Actor

sealed trait Direction

case object East extends Direction

case object West extends Direction

object Rope {

  sealed trait RopeMsg

  case class Join(direction: Direction) extends RopeMsg

  case object Joining extends RopeMsg

  case object Joined extends RopeMsg

  case object Left extends RopeMsg

}

class Rope extends Actor {
  override def receive: Receive = ???
}
