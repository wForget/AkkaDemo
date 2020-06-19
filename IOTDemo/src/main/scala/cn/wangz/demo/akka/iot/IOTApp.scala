package cn.wangz.demo.akka.iot

import akka.actor.typed.ActorSystem

/**
 *
 * @author wang_zh
 * @date 2020/6/11
 */
object IOTApp {

  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](IotSupervisor(), "iot-system")
  }

}
