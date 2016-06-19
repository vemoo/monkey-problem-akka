package com.clluc.monkeyproblem

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.clluc.monkeyproblem.Monkey._
import com.clluc.monkeyproblem.Rope._
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.duration._

class MonkeySpec extends TestKit(ActorSystem("MonkeySpec"))
  with FunSuiteLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate()
  }

  val timeThres = 100.milliseconds

  test("monkey shouldn't cross until told to") {
    val rope = TestProbe()
    val monkey = system.actorOf(Monkey.props(West, rope.ref))
    rope.expectMsg(Join(_))
    rope.expectNoMsg(300.milliseconds)

    rope.send(monkey, Go)
    rope.expectMsg(Joining)
  }

  //TODO find better way to test this
  test("monkey shold take 1 second to get on the rope, and 4 crossing") {
    val rope = TestProbe()
    val monkey = system.actorOf(Monkey.props(West, rope.ref))
    rope.expectMsg(Join(_))
    rope.send(monkey, Go)
    rope.expectMsg(Joining)
    rope.expectMsg(1.second + timeThres, Joined)
    rope.expectMsg(4.second + timeThres, Left)
  }
}
