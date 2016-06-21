package com.clluc.monkeyproblem

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.clluc.monkeyproblem.Monkey._
import com.clluc.monkeyproblem.Rope._
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.duration._

class RopeSpec extends TestKit(ActorSystem("RopeSpec"))
  with FunSuiteLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate()
  }

  test("empty rope tells monkey to go") {
    val monkey = TestProbe()
    val rope = system.actorOf(Props[Rope])

    monkey.send(rope, Join(West))
    monkey.expectMsg(Go)
  }

  test("rope tells second monkey on same direction to wait until first monkey ends joining") {
    val monkey1 = TestProbe()
    val monkey2 = TestProbe()
    val rope = system.actorOf(Props[Rope])

    monkey1.send(rope, Join(West))
    monkey1.expectMsg(Go)

    monkey2.send(rope, Join(West))
    monkey2.expectNoMsg(300.milliseconds)

    monkey1.send(rope, Joined)

    monkey2.expectMsg(Go)
  }

  test("rope doesn't tell second monkey crossing in opposite direction to go") {
    val monkey1 = TestProbe()
    val monkey2 = TestProbe()
    val rope = system.actorOf(Props[Rope])

    monkey1.send(rope, Join(West))
    monkey1.expectMsg(Go)
    monkey1.send(rope, Joined)

    monkey2.send(rope, Join(East))
    monkey2.expectNoMsg(300.milliseconds)
  }

  test("avoid starvation") {
    val monkey1 = TestProbe()
    val monkey2 = TestProbe()
    val monkey3 = TestProbe()
    val rope = system.actorOf(Props[Rope])

    monkey1.send(rope, Join(West))
    monkey1.expectMsg(Go)
    monkey1.send(rope, Joined)

    monkey2.send(rope, Join(East))

    monkey3.send(rope, Join(West))
    monkey3.expectNoMsg(300.milliseconds)

    monkey1.send(rope, Left)

    monkey2.expectMsg(Go)
    monkey2.send(rope, Joined)

    monkey3.expectNoMsg(300.milliseconds)

    monkey2.send(rope, Left)

    monkey3.expectMsg(Go)
  }
}
