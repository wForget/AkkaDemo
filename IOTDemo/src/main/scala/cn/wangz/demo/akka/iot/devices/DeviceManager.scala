package cn.wangz.demo.akka.iot.devices

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors.Receive
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext}

/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
class DeviceManager(context: ActorContext[DeviceManager.Command]) extends AbstractBehavior[DeviceManager.Command](context) {

  context.log.info(s"DeviceManager started")

  var groupIdToActor = Map.empty[String, ActorRef[DeviceGroup.Command]]

  override protected def createReceive: Receive[DeviceManager.Command] = {

  }

}

object DeviceManager {

  sealed trait Command

  final case class DeviceRegistered(device: ActorRef[Device.Command])

  final case class RequestTrackDevice(groupId: String, deviceId: String, replyTo: ActorRef[DeviceRegistered]) extends DeviceManager.Command with DeviceGroup.Command

  final case class ReplyDeviceList(requestId: Long, ids: Set[String])

  final case class RequestDeviceList(requestId: Long, groupId: String, replyTo: ActorRef[ReplyDeviceList]) extends DeviceManager.Command with DeviceGroup.Command

  private final case class DeviceGroupTerminated(groupId: String) extends DeviceManager.Command


}
