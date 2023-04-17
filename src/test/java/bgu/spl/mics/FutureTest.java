package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<String> future;

    @Before
    public void setUp() {
        future = new Future<String>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        future.resolve("test");
        String res = future.get();
        assertTrue(future.isDone());
    }

    @Test
    public void resolve() {
        assertFalse(future.isDone());
        future.resolve("test");
        assertTrue(future.isDone());
        assertTrue(future.get()=="test");
    }

    @Test
    public void isDone() {
    }

    @Test
    public void testGet() {
    }
}