import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.Time;
import java.time.LocalTime;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<String> currFuture;

    @Before
    public void setUp() throws Exception {
        currFuture= new Future<String>();
    }

    @Test
    public void get() {
        assertFalse(currFuture.isDone());
        Thread t = new Thread(()-> {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currFuture.resolve("result");
        });
        t.start();
        assertEquals("result",currFuture.get());
    }

    @Test
    public void resolve() {
        assertFalse(currFuture.isDone());
        currFuture.resolve("result");
        assertTrue(currFuture.isDone());
        assertEquals(currFuture.get(),"result");
    }


    @Test
    public void testGet() {
        assertFalse(currFuture.isDone());
        Thread t = new Thread(()-> {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currFuture.resolve("result");
        });
        t.start();
        assertNull(currFuture.get(300, TimeUnit.MILLISECONDS));
        Thread t2 = new Thread(()-> {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currFuture.resolve("result");
        });
        t2.start();
        assertEquals("result",currFuture.get(3000, TimeUnit.MILLISECONDS));
    }
}