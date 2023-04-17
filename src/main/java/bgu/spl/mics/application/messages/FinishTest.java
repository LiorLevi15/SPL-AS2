
package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class FinishTest implements Broadcast {

    private Model model;

    public FinishTest(Model m) {
        model = m;
    }

    public Model getModel() {
        return model;
    }

}