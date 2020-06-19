package cn.wangz.demo.akka.iot.devices

import akka.actor.typed.{ActorRef, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import cn.wangz.demo.akka.iot.devices.DeviceGroup.DeviceTerminated
import cn.wangz.demo.akka.iot.devices.DeviceManager.{DeviceRegistered, ReplyDeviceList, RequestDeviceList, RequestTrackDevice}


/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
class DeviceGroup(context: ActorContext[DeviceGroup.Command], groupId: String) extends AbstractBehavior[DeviceGroup.Command](context) {

  context.log.info(s"DeviceGroup $groupId started")

  private var deviceIdToActor = Map.empty[String, ActorRef[Device.Command]]

  override def onMessage(msg: DeviceGroup.Command): Behavior[DeviceGroup.Command] = msg match {
    case trackMsg @ RequestTrackDevice(`groupId`, deviceId, replyTo) =>
      deviceIdToActor.get(deviceId) match {
        case Some(deviceActor) =>
          replyTo ! DeviceRegistered(deviceActor)
        case None =>
          context.log.info("creating device actor for {}", trackMsg.deviceId)
          val deviceActor = context.spawn(Device(groupId, deviceId), s"device-$deviceId")
          context.watchWith(deviceActor, DeviceTerminated(deviceActor, groupId, deviceId))
          deviceIdToActor += deviceId -> deviceActor
          replyTo ! DeviceRegistered(deviceActor)
      }
      this
    case RequestTrackDevice(gId, _, _) =>
      context.log.warn(s"Ignoring TrackDevice request for $gId, This actor is responsible for $groupId")
      this
    case RequestDeviceList(requestId, gId, replyTo) =>
      if (gId == groupId) {
        replyTo ! ReplyDeviceList(requestId, deviceIdToActor.keySet)
        this
      } else {
        Behaviors.unhandled
      }
    case DeviceTerminated(_, _, deviceId) =>
      context.log.info(s"Device actor for $deviceId has been terminated")
      deviceIdToActor -= deviceId
      this
  }

  override def onSignal: PartialFunction[Signal, Behavior[DeviceGroup.Command]] = {
    case PostStop =>
      context.log.info(s"DeviceGroup $groupId stopped")
      this
  }
}

object DeviceGroup{

  def apply(groupId: String): Behavior[Command] = Behaviors.setup(new DeviceGroup(_, groupId))

  trait Command

  private final case class DeviceTerminated(device: ActorRef[Device.Command], groupId: String, deviceId: String) extends Command

}
