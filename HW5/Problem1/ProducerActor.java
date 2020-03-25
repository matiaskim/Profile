/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.BlockingQueue;

public class ProducerActor extends AbstractActor
{
    static public Props props(BlockingQueue<Integer> buffer, ActorRef bufferActor)
    {
        return Props.create(ProducerActor.class, () -> new ProducerActor(buffer, bufferActor));
    }
    
    static class Done {}
    static class Produced {}
    
    final BlockingQueue<Integer> buffer;
    final ActorRef bufferActor;
    
    public ProducerActor(BlockingQueue<Integer> buffer, ActorRef bufferActor)
    {
        this.buffer = buffer;
        this.bufferActor = bufferActor;
    }
    
    @Override 
    public Receive createReceive()
    {
        return receiveBuilder()
                .match(BufferActor.RequestItems.class, p -> {
                    for(int x = 0; x < 100; x++)
                    {
                        buffer.put(x);
                        System.out.println(Thread.currentThread().getName() + " Produced " + x);
                        bufferActor.tell(new Done(), ActorRef.noSender());
                    }
                    bufferActor.tell(new Done(), ActorRef.noSender());
                })
                .build();
    }
}
