import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
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
    private MessageBusImpl message_Bus;
    private ExampleBroadcastListenerService MS;
    private ExampleEventHandlerService MS2;
    private ExampleEventHandlerService MS3;
    private ExampleEvent event;
    private ExampleBroadcast broadcast;

    @Before
    public void setUp() throws Exception {
        message_Bus = MessageBusImpl.getInstance();
        String [] temp = {"1"};
        MS = new ExampleBroadcastListenerService("MS",temp);
        String [] temp2 = {"2"};
        MS2 = new ExampleEventHandlerService("MS2",temp2);
        String [] temp3 = {"2"};
        MS3 = new ExampleEventHandlerService("MS3",temp3);
        event = new ExampleEvent("Irad");
        broadcast = new ExampleBroadcast("Noam");
    }

    @After
    public void tearDown() throws Exception {
        message_Bus.cleanBus();;
    }
    @Test
    public void subscribeEvent() {
        assertFalse(message_Bus.isSentBroadcast(broadcast)); //we except "false" because the broadcast didn't registered
        message_Bus.register(MS);
        assertFalse(message_Bus.isSubscribeEvent(event.getClass(),MS)); //we except "false" because we didn't SubscribeEvent
        message_Bus.subscribeEvent(event.getClass(),MS);
        assertTrue(message_Bus.isSubscribeEvent(event.getClass(),MS));//we except "true" because we SubscribeEvent
    }

    @Test
    public void subscribeBroadcast() {
        assertFalse(message_Bus.isSentBroadcast(broadcast)); //we except "false" because the broadcast didn't registered
        message_Bus.register(MS);
        assertFalse(message_Bus.isSubscribeBroadcast(broadcast.getClass(),MS)); //we except "false" because we didn't SubscribeBroadcast
        message_Bus.subscribeBroadcast(broadcast.getClass(),MS);
        assertTrue(message_Bus.isSubscribeBroadcast(broadcast.getClass(),MS));//we except "true" because we SubscribeBroadcast
    }

    @Test
    public void complete() {
        ExampleEvent eventCOM = new ExampleEvent("EVENT");
        assertFalse(message_Bus.isComplete(eventCOM,"result")); //Operation not completed, we except "false"
        message_Bus.register(MS3);
        message_Bus.subscribeEvent(event.getClass(),MS3);
        message_Bus.sendEvent(eventCOM);
        message_Bus.complete(eventCOM,"result");
        assertTrue(message_Bus.isComplete(eventCOM,"result")); //Operation completed, we except "true"
    }

    @Test
    public void sendBroadcast() {
        assertFalse(message_Bus.isSentBroadcast(broadcast)); //we except "false" because the broadcast didn't registered
        message_Bus.register(MS);
        assertFalse(message_Bus.isSentBroadcast(broadcast)); //we except "false" because the broadcast didn't subscribe
        message_Bus.subscribeBroadcast(broadcast.getClass(),MS);
        assertFalse(message_Bus.isSentBroadcast(broadcast)); //we except "false" because the broadcast didn't sent
        message_Bus.sendBroadcast(broadcast);
        assertTrue(message_Bus.isSentBroadcast(broadcast)); //we except "true" because the broadcast sent
        try {
            assertEquals(message_Bus.awaitMessage(MS), broadcast);
        } catch (InterruptedException exception){
            fail("excepted MS will get the message");
        }
    }

    @Test
    public void sendEvent() {
        assertFalse(message_Bus.isSentEvent(event)); //we except "false" because no Micro Service is subscribeEvent
        message_Bus.register(MS);
        message_Bus.subscribeEvent(event.getClass(),MS);
        message_Bus.register(MS2);
        message_Bus.subscribeEvent(event.getClass(),MS2);
        assertFalse(message_Bus.isSentEvent(event)); //we except "false" because the event didn't sent
        message_Bus.sendEvent(event);

        try {
            assertEquals(message_Bus.awaitMessage(MS), event);
        } catch (InterruptedException exception){
            fail("excepted MS will get the message");
        }

        //check Round Robbin
        ExampleEvent event2 = new ExampleEvent("spl");
        ExampleEvent event3 = new ExampleEvent("HW2");
        message_Bus.sendEvent(event2);
        message_Bus.sendEvent(event3);
        try {
            assertEquals(message_Bus.awaitMessage(MS2), event2);
            assertEquals(message_Bus.awaitMessage(MS), event3);
        } catch (InterruptedException exception){
            fail("Round Robbin not work properly");
        }
    }

    @Test
    public void register() {
        assertFalse(message_Bus.isRegister(MS)); //we except false because MS didn't register
        message_Bus.register(MS);
        assertTrue(message_Bus.isRegister(MS));//we except true because MS registered
    }

    @Test
    public void unregister() {
        assertTrue(message_Bus.isUnRegister(MS)); //we except "false" because Micro Service is not register
        message_Bus.register(MS);
        assertFalse(message_Bus.isUnRegister(MS)); //we except "false" because MS is Register
        message_Bus.unregister(MS);
        assertTrue(message_Bus.isUnRegister(MS));//we except "true" because MS UnRegister

    }

    @Test
    public void awaitMessage() {
        assertThrows(Exception.class,()->message_Bus.awaitMessage(MS3)); //we asked to throw exception if the micro Service never register
        ExampleBroadcast broadcast2 = new ExampleBroadcast("SPL");
        Thread t = new Thread(()-> {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message_Bus.sendBroadcast(broadcast2);
        });
        t.start();
        message_Bus.register(MS3);
        Message message = null;
        message_Bus.subscribeBroadcast(broadcast2.getClass(),MS3);
        try {
            message = message_Bus.awaitMessage(MS3);
        } catch (InterruptedException e) {
            fail("No more messages");
        }
        assertEquals(message,broadcast2);

    }
}
