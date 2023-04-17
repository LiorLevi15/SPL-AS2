package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    private MessageBusImpl massageBus;


    @Before
    public void setUp() {
        massageBus = MessageBusImpl.getInstance();

    }

    @After
    public void tearDown() {
    }

    @Test
    public void subscribeEvent() {
        ExampleEvent e = new ExampleEvent("event");
        MicroService m = new ExampleEventHandlerService("handler", new String[]{"4"});
        massageBus.subscribeEvent(e.getClass(),m);
        assertTrue(massageBus.isSubscribedToEvent(e.getClass(),m));
    }

    @Test
    public void subscribeBroadcast() {
        ExampleBroadcast b = new ExampleBroadcast("broadcast");
        MicroService m = new ExampleBroadcastListenerService("listener", new String[]{"4"});
        massageBus.subscribeBroadcast(b.getClass(),m);
        assertTrue(massageBus.isSubscribedToBroadcast(b.getClass(),m));
    }


    @Test
    public void sendBroadcast() {
        ExampleBroadcast b = new ExampleBroadcast("broadcast");
        MicroService m2 = new ExampleBroadcastListenerService("listener", new String[]{"4"});
        massageBus.register(m2);
        massageBus.subscribeBroadcast(b.getClass(),m2);
        massageBus.sendBroadcast(b);
        try {
            assertEquals(b, massageBus.awaitMessage(m2));
        } catch (InterruptedException e) { fail(); }
    }

    @Test
    public void sendEvent() {
        ExampleEvent e = new ExampleEvent("event");
        MicroService m2 = new ExampleEventHandlerService("handler", new String[]{"4"});
        massageBus.register(m2);
        massageBus.subscribeEvent(e.getClass(),m2);
        massageBus.sendEvent(e);
        try {
            assertEquals(e, massageBus.awaitMessage(m2));
        } catch (InterruptedException ei) { fail(); }
    }

    @Test
    public void register() {
        MicroService m1 = new ExampleEventHandlerService("handler", new String[]{"4"});
        assertFalse(massageBus.isRegistered(m1));
        massageBus.register(m1);
        assertTrue(massageBus.isRegistered(m1));
    }

}