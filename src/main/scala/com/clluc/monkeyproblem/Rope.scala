package com.clluc.monkeyproblem

import akka.actor.{Actor, ActorRef}

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

  import com.clluc.monkeyproblem.Monkey.{Go, Wait}
  import com.clluc.monkeyproblem.Rope._

  import scala.collection.immutable.Queue

  var direction: Option[Direction] = None

  var waiting: Queue[(ActorRef, Direction)] = Queue.empty

  var crossing: Set[ActorRef] = Set.empty

  /**
    * Indicates that someone is getting on to the rope
    * but hasn't started crossing yet
    */
  var beingClimbed = false


  /**
    * tells the actor to go and makes the appropiate state changes
    *
    * @param ref actor that can join the rope
    */
  def tellGo(ref: ActorRef): Unit = {
    crossing += ref
    ref ! Go
    beingClimbed = true
  }

  override def receive: Receive = {
    case Join(dir) =>
      direction match {
        case None =>
          direction = Some(dir)
          tellGo(sender)
        case Some(curDir) =>
          if (waiting.isEmpty && dir == curDir && !beingClimbed) {
            tellGo(sender)
          } else {
            waiting = waiting.enqueue((sender, dir))
            sender ! Wait
          }
      }
    case Joining => ()
    case Joined =>
      beingClimbed = false
      waiting.dequeueOption match {
        case Some(((actorRef, dir), newWaiting)) if dir == direction.get =>
          tellGo(actorRef)
          waiting = newWaiting
        case _ => ()
      }
    case Left =>
      crossing -= sender
      waiting.dequeueOption match {
        case Some(((actorRef, dir), newWaiting)) =>
          direction = Some(dir)
          tellGo(actorRef)
          waiting = newWaiting
        case _ =>
          direction = None
      }
  }
}
