package com.clluc.monkeyproblem

import akka.actor.{ActorSystem, Props}
import scala.io.StdIn.readLine
import scala.concurrent.duration._
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global


object ApplicationMain extends App {
  def timeToNextRnd = (1 + Random.nextInt(8)).seconds

  def dirRnd = if (Random.nextBoolean()) East else West

  override def main(args: Array[String]) {
    val system = ActorSystem("MyActorSystem")
    val rope = system.actorOf(Props[Rope])

    var monkeyId = 0

    def nextMonkeyRunnable: Runnable = new Runnable {
      override def run(): Unit = {
        monkeyId += 1
        system.actorOf(Monkey.props(monkeyId, dirRnd, rope))
        system.scheduler.scheduleOnce(timeToNextRnd, nextMonkeyRunnable)
      }
    }
    nextMonkeyRunnable.run()

    println("press enter to end program")
    readLine
    system.terminate()
  }
}