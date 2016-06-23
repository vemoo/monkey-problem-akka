package com.clluc.monkeyproblem

import akka.actor.{ActorSystem, Props}
import com.clluc.monkeyproblem.MonkeyGenerator.GenerateMonkeys

import scala.io.StdIn.readLine


object ApplicationMain extends App {

  override def main(args: Array[String]) {
    val system = ActorSystem("MyActorSystem")
    val rope = system.actorOf(Props[Rope])
    val monkeyGenerator = system.actorOf(MonkeyGenerator.props(rope))

    monkeyGenerator ! GenerateMonkeys

    println("press enter to end program")

    readLine
    system.terminate()
  }
}