package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class FinishTrain implements Broadcast {

    private Model model;

    public FinishTrain(Model m) {
        model = m;
    }

    public Model getModel() {
        return model;
    }

}
