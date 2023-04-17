package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {
    private CPU cpu;
    private Data data;

    @Before
    public void setUp() throws Exception {

this.cpu = new CPU(1000);
        //this.data = new Data();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getTimeToProcess() {
        assertTrue(cpu.getTimeToProcess() >= 0);
    }
}

