package cn.wangz.demo.akka.simple;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * @author wang_zh
 * @date 2020/6/10
 */
public class PrintMessageActor extends AbstractBehavior<String> {

    // 类似工厂方法
    static Behavior<String> create() {
        return Behaviors.setup(PrintMessageActor::new);
    }

    public PrintMessageActor(ActorContext<String> context) {
        super(context);
    }

    // 绑定 receive
    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessage(String.class, this::onMessage).build();
    }

    // 处理事件
    private Behavior<String> onMessage(String message) {
        System.out.println("get message: " + message);
        return this;
    }

    public static void main(String[] args) throws InterruptedException {
        // 初始化 ActorSystem， actor 容器
        ActorSystem<String> root = ActorSystem.apply(PrintMessageActor.create(), "root");
        System.out.println("root: " + root);

        // 发送消息
        root.tell("hello world!!!");

        Thread.sleep(5000);

        // terminate ActorSystem
        root.terminate();
    }

}
