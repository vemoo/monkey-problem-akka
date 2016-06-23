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

  test("monkey shouldn't cross until told to") {
    val rope = TestProbe()
    val monkey = system.actorOf(Monkey.props(0, West, rope.ref))
    rope.expectMsgPF() {
      case Join(_) => ()
    }
    rope.expectNoMsg(300.milliseconds)

    rope.send(monkey, Go)
    rope.expectMsg(Joining)
  }

  //TODO find better way to test this
  test("monkey shold take 1 second to get on the rope, and 4 crossing") {

    val rope = TestProbe()
    val monkey = system.actorOf(Monkey.props(0, West, rope.ref))
    rope.expectMsgPF() {
      case Join(_) => ()
    }
    rope.send(monkey, Go)
    rope.expectMsg(Joining)
    rope.expectNoMsg(1.second)
    rope.expectMsg(Joined)
    rope.expectNoMsg(4.second)
    rope.expectMsg(Left)
  }
}
