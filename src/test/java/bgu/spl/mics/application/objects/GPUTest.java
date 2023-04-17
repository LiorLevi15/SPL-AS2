package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private GPU gpu;
    private Model model;
    private Data data;
    private Student student;


    @Before
    public void setUp() throws Exception {
        this.data = new Data(Data.Type.Images, 20000);
        this.gpu = new GPU(GPU.Type.RTX3090, Cluster.getInstance());
        this.student = new Student("student", "life uni", Student.Degree.MSc);
        this.model = new Model("model", this.data, this.student);
    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void testModel() {
        model.setStatus(Model.Degree.Trained);
        assertTrue(model.getStatus() == Model.Degree.Trained);
        gpu.testModel(model);
        assertTrue(model.getStatus() == Model.Degree.Tested);
    }
}