package cn.wangz.demo.akka.iot.devices

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, LoggerOps}
import cn.wangz.demo.akka.iot.devices.Device.{ReadTemperature, RecordTemperature, RespondTemperature, TemperatureRecorded}

/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
class Device(context: ActorContext[Device.Command], groupId: String, deviceId: String) extends AbstractBehavior[Device.Command](context) {

  context.log.info2("Device actor {}-{} started", groupId, deviceId)

  private var lastTemperature: Option[Double] = None

  override def onMessage(msg: Device.Command): Behavior[Device.Command] = msg  match {
    case RecordTemperature(requestId, value, replyTo) =>
      lastTemperature = Some(value)
      replyTo ! TemperatureRecorded(requestId)
      this
    case ReadTemperature(requestId, replyTo) =>
      replyTo ! RespondTemperature(requestId, lastTemperature)
      this
  }

  override def onSignal: PartialFunction[Signal, Behavior[Device.Command]] = {
    case PostStop =>
      context.log.info2("Device actor {}-{} stopped", groupId, deviceId)
      this
  }
}

object Device {

  def apply(groupId: String, deviceId: String): Behavior[Device.Command] = {
    Behaviors.setup[Device.Command](new Device(_, groupId, deviceId))
  }

  sealed trait Command

  final case class ReadTemperature(requestId: Long, replyTo: ActorRef[RespondTemperature]) extends Command
  final case class RespondTemperature(requestId: Long, value: Option[Double])

  final case class RecordTemperature(requestId: Long, value: Double, replyTo: ActorRef[TemperatureRecorded]) extends Command
  final case class TemperatureRecorded(requestId: Long)

}
