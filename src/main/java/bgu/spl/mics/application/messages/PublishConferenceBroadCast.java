package bgu.spl.mics.application.messages;
import java.util.LinkedList;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

public class PublishConferenceBroadCast implements Broadcast {
    private LinkedList<Model> modelsToPublish;

    public PublishConferenceBroadCast(LinkedList<Model> modelsToPublish) {
    this.modelsToPublish= modelsToPublish;
    }

    public LinkedList<Model> getModelsToPublish(){
        return modelsToPublish;
    }

}
