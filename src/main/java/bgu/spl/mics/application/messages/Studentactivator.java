package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Student;

public class Studentactivator implements Broadcast {

    Student student;

    public Studentactivator(Student student){
        this.student=student;
    }

    public Student getStudent() {
        return student;
    }
}
