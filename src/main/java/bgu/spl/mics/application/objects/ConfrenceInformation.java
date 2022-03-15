package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> models;
    private int currTick;
    private ConferenceService con_microService;

    public ConfrenceInformation (String name, int date) {
        this.name=name;
        this.date =date;
        this.models=new LinkedList<>();
        currTick=1;
    }

    public void updateConferenceService(ConferenceService con_microService){
        this.con_microService=con_microService;
    }


    public String getName(){
        return name;
    }

    public int getDate(){
        return date;
    }
    public LinkedList<Model> getModels(){
        return models;
    }

    public void addModel(Model model){
        models.add(model);
    }

    public boolean TimeUp (){ //check if is the date of the confrence
        if(currTick==date)
            return true;
        return false;
    }

    public void increaseTick(){
        currTick=currTick+1;
        if (TimeUp()) {
            con_microService.PublicConference();


        }
    }

}