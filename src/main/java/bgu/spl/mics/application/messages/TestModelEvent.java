package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event {

    private Model model;

    public TestModelEvent(Model m) {
        model = m;
    }

    public Model getModel() {
        return model;
    }
}
