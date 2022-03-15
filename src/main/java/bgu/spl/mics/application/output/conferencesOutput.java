package bgu.spl.mics.application.output;

import java.util.LinkedList;

public class conferencesOutput {
     private String name;
     private int date;
     private LinkedList<ModelsOutput> publications;

     public conferencesOutput (String name, int date, LinkedList<ModelsOutput> publications){
          this.name=name;
          this.date=date;
          this.publications=publications;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public int getDate() {
          return date;
     }

     public void setDate(int date) {
          this.date = date;
     }

     public LinkedList<ModelsOutput> getPublications() {
          return publications;
     }

     public void setPublications(LinkedList<ModelsOutput> publications) {
          this.publications = publications;
     }
}
