package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class StudentService extends MicroService {
    private Student student;
    private int nextModel;

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        nextModel=0;
    }

    @Override
    protected void initialize() {

        Callback<TerminateBroadcast> terminateBroadCastCallback = (TerminateBroadcast c)->{this.terminate();};
        Callback<PublishConferenceBroadcast> publishConferenceBroadcastCallback = (PublishConferenceBroadcast c) ->{
           for(Model model: c.getModels()){
               if(model.getStatus()!=Model.Degree.Posted) model.setStatus(Model.Degree.Posted);
               if(student.getModels().contains(model)){
                    student.addPublications();
               } else student.addReadConff(model);
           }
        };
        Callback<FinishTrain> finishTrainBroadcastCallback = (FinishTrain e) -> {
            if(student.getModels().contains(e.getModel()) ){
                sendEvent(new TestModelEvent(e.getModel()));
            }
        };
        Callback<FinishTest> finishTestBroadcastCallback = (FinishTest e) ->{
            if(student.getModels().contains(e.getModel()) ){
                if(e.getModel().getResult()==Model.Result.Good ) sendEvent(new PublishResultsEvent(e.getModel()));
                nextModel++;
                if(nextModel<student.getModels().size()) sendEvent(new TrainModelEvent(student.getModels().get(nextModel)));

            }
        };

        subscribeBroadcast(TerminateBroadcast.class,terminateBroadCastCallback);
        subscribeBroadcast(PublishConferenceBroadcast.class,publishConferenceBroadcastCallback);
        subscribeBroadcast(FinishTrain.class, finishTrainBroadcastCallback);
        subscribeBroadcast(FinishTest.class,finishTestBroadcastCallback);

        sendEvent(new TrainModelEvent(student.getModels().get(nextModel)));




    }
}
