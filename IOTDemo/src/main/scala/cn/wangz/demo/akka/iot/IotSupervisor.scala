package cn.wangz.demo.akka.iot

import akka.actor.typed.{Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
class IotSupervisor(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing](context)  {

  context.log.info("IoT Application started")

  override def onMessage(msg: Nothing): Behavior[Nothing] = Behaviors.unhandled

  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = {
    case PostStop =>
      context.log.info("IoT Application stopped")
      this
    case signal: Signal =>
      context.log.info(s"get signal: $signal")
      this
  }

}

object IotSupervisor {

  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing](new IotSupervisor(_))

}