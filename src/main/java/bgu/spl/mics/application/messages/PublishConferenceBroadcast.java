package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

public class PublishConferenceBroadcast implements Broadcast {

    Vector<Model> models;

    public PublishConferenceBroadcast(Vector<Model> models) {
        this.models = models;
    }

    public Vector<Model> getModels(){ return models;}
}
