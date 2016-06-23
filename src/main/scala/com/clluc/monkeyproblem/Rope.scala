package com.clluc.monkeyproblem

import akka.actor.{Actor, ActorRef}

import scala.collection.immutable.Queue

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


case class RopeState(direction: Option[Direction], waiting: Queue[(ActorRef, Direction)], crossing: Set[ActorRef], beingClimbed: Boolean)

class Rope extends Actor {

  import com.clluc.monkeyproblem.Monkey.{Go, Wait}
  import com.clluc.monkeyproblem.Rope._

  import scala.collection.immutable.Queue

  var state = RopeState(None, Queue.empty, Set.empty, false)

  override def receive: Receive = {
    case Join(dir) =>
      state = {
        state.direction match {
          case None =>
            sender ! Go
            state.copy(
              direction = Some(dir),
              crossing = state.crossing + sender,
              beingClimbed = true
            )
          case Some(curDir) =>
            if (state.waiting.isEmpty && dir == curDir && !state.beingClimbed) {
              sender ! Go
              state.copy(
                crossing = state.crossing + sender,
                beingClimbed = true
              )
            } else {
              sender ! Wait
              state.copy(
                waiting = state.waiting.enqueue((sender, dir))
              )
            }
        }
      }
    case Joining => ()
    case Joined =>
      state = {
        state.waiting.dequeueOption match {
          case Some(((actorRef, dir), newWaiting)) if dir == state.direction.get =>
            actorRef ! Go
            state.copy(
              crossing = state.crossing + actorRef,
              beingClimbed = true,
              waiting = newWaiting
            )
          case _ =>
            state.copy(
              beingClimbed = false
            )
        }
      }
    case Left =>
      val newCrossing = state.crossing - sender
      state = {
        if (newCrossing.isEmpty) {
          state.waiting.dequeueOption match {
            case Some(((actorRef, dir), newWaiting)) =>
              actorRef ! Go
              state.copy(
                direction = Some(dir),
                crossing = newCrossing + actorRef,
                waiting = newWaiting,
                beingClimbed = true
              )
            case _ =>
              state.copy(
                direction = None,
                crossing = newCrossing
              )
          }
        }
        else {
          state.copy(
            crossing = newCrossing
          )
        }
      }
  }
}
