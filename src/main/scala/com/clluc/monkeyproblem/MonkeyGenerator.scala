package com.clluc.monkeyproblem

import akka.actor.{Actor, ActorRef, Props}
import com.clluc.monkeyproblem.MonkeyGenerator.GenerateMonkeys
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import scala.concurrent.duration._

object MonkeyGenerator {

  case object GenerateMonkeys

  def props(rope: ActorRef): Props = Props(new MonkeyGenerator(rope))
}

class MonkeyGenerator(val rope: ActorRef) extends Actor {

  def timeToNextRnd = (1 + Random.nextInt(8)).seconds

  def dirRnd = if (Random.nextBoolean()) East else West

  var monkeyId = 0

  override def receive: Receive = {
    case GenerateMonkeys =>
      monkeyId += 1
      context.system.actorOf(Monkey.props(monkeyId, dirRnd, rope))
      context.system.scheduler.scheduleOnce(timeToNextRnd, self, GenerateMonkeys)
  }
}
