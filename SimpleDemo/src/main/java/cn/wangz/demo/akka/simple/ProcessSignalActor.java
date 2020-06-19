package cn.wangz.demo.akka.simple;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * @author wang_zh
 * @date 2020/6/10
 */
public class ProcessSignalActor extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(ProcessSignalActor::new);
    }
    private ActorRef<String> printActor;

    private ProcessSignalActor(ActorContext<String> context) {
        super(context);
        printActor = this.getContext().spawn(PrintMessageActor.create(), "print-actor");
        System.out.println("printActor: " + printActor);
    }

    // 绑定 receive
    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("stop", this::onStopMessage)
                .onMessage(String.class, this::onMessage)
                .onSignalEquals(PostStop.instance(), this::onStop)
                .onSignal(Signal.class, this::onSignal)
                .build();
    }

    private Behavior<String> onStopMessage() {
        System.out.println("do stop...");
        Behaviors.stopped();
        return this;
    }


    private Behavior<String> onMessage(String message) {
        printActor.tell(message);
        return this;
    }

    private Behavior<String> onSignal(Signal signal) {
        System.out.println("signal: " + signal);
        return this;
    }

    private Behavior<String> onStop() {
        System.out.println("stop...");
        return this;
    }

    public static void main(String[] args) throws InterruptedException {
        // 初始化 ActorSystem， actor 容器
        ActorSystem<String> root = ActorSystem.apply(ProcessSignalActor.create(), "root");
        System.out.println("root: " + root);

        // 发送消息
        root.tell("hello world!!!");

        Thread.sleep(2000);

        root.tell("stop");

        Thread.sleep(2000);

        root.tell("message 111");

        Thread.sleep(2000);

        // terminate ActorSystem
        root.terminate();
    }
}
