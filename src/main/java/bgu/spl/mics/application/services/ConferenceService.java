package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadCast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;


/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link /PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private String Conference_name;
    private ConfrenceInformation conference;
    private LinkedList<String> ResultEvent;

    public ConferenceService(String Conference_name, ConfrenceInformation con_info) {
        super(Conference_name);
        this. conference=con_info;
        con_info.updateConferenceService(this);
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broad)->{
            conference.increaseTick();
        });

        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent event)->{
            Model.Result result= event.getModel().getResult();
            if(result.equals(Model.Result.Good)) {conference.addModel(event.getModel());}
            complete(event, true);
        });
    }

    public void PublicConference(){
        sendBroadcast(new PublishConferenceBroadCast(conference.getModels()));
        terminate();
    }


}