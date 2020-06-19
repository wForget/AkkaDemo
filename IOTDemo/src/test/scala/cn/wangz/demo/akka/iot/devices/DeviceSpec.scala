package cn.wangz.demo.akka.iot.devices

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  import Device._

  "Device acotr" must {
    "reply with empty reading if no temperature is known" in {
      val probe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("gourp", "device"))

      deviceActor ! Device.ReadTemperature(42, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should ===(42)
      response.value should ===(None)
    }

    "reply with latest temperature reading" in {
      val recordProbe = createTestProbe[TemperatureRecorded]()
      val readProbe = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! RecordTemperature(1, 24.0, recordProbe.ref)
      val record1 = recordProbe.receiveMessage()
      record1.requestId should ===(1)

      deviceActor ! ReadTemperature(2, readProbe.ref)
      val respond1 = readProbe.receiveMessage()
      respond1.requestId should ===(2)
      respond1.value should ===(Some(24.0))

      deviceActor ! RecordTemperature(3, 55.0, recordProbe.ref)
      val record2 = recordProbe.receiveMessage()
      record2.requestId should ===(3)

      deviceActor ! ReadTemperature(4, readProbe.ref)
      val respond2 = readProbe.receiveMessage()
      respond2.requestId should ===(4)
      respond2.value should ===(Some(55.0))
    }
  }

}
