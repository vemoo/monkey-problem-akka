package com.clluc.monkeyproblem

import akka.actor.Actor

sealed trait Direction

case object East extends Direction

case object West extends Direction

object Rope {

  sealed trait RopeMsg

  /**
    * The sender is asking to join the rope
    */
  case class Join(direction: Direction) extends RopeMsg

  /**
    * This message signals the sender is getting on the rope
    */
  case object Joining extends RopeMsg

  /**
    * This message signals the sender has started crossing the rope
    */
  case object Joined extends RopeMsg

  /**
    * The sender has ended crossing the rope
    */
  case object Left extends RopeMsg

}

class Rope extends Actor {
  override def receive: Receive = ???
}
