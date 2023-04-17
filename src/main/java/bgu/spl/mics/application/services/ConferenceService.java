package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link },
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrenceInformation;
    private long tickCount;
    Vector<Model> models;
    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        this.confrenceInformation=confrenceInformation;
        tickCount=0;
        models=new Vector<Model>();
    }

    public Vector<Model> getModels(){ return models;}
    public void addModel(Model model){ models.add(model);}
    public long getTickCount() {
        return tickCount;
    }
    public void setTickCount(long tickCount){
        this.tickCount=tickCount;
    }

    public ConfrenceInformation getConfrenceInf(){return confrenceInformation;}

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast b)-> {terminate();});
        Callback<TickBroadcast> tickBroadcastCallback= (TickBroadcast c)->{
            setTickCount(getTickCount()+1);

            if( confrenceInformation.getDate()==getTickCount()){
                sendBroadcast(new PublishConferenceBroadcast(getModels()));
                this.terminate();
            }
            };

        Callback<PublishResultsEvent> publishResultsEvent= (PublishResultsEvent c)->{
            addModel(c.getModel());
        };

        Callback<TerminateBroadcast> terminateBroadCastCallback = (TerminateBroadcast c)->{
            this.terminate();
        };

        subscribeEvent(PublishResultsEvent.class,publishResultsEvent);
        subscribeBroadcast(TickBroadcast.class,tickBroadcastCallback);
        subscribeBroadcast(TerminateBroadcast.class,terminateBroadCastCallback);



    }
}
