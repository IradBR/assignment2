package bgu.spl.mics.application.Input;

public class Input {
    private InputStudent[] Students;
    private String[] GPUS;
    private int[] CPUS;
    private InputConfrence[] Conferences;
    private int TickTime;
    private int Duration;

    public InputStudent[] getStudents() {
        return Students;
    }

    public void setStudents(InputStudent[] students) {
        Students = students;
    }

    public String[] getGPUS() {
        return GPUS;
    }

    public void setGPUS(String[] GPUS) {
        this.GPUS = GPUS;
    }

    public int[] getCPUS() {
        return CPUS;
    }

    public void setCPUS(int[] CPUS) {
        this.CPUS = CPUS;
    }

    public InputConfrence[] getConferences() {
        return Conferences;
    }

    public void setConferences(InputConfrence[] conferences) {
        Conferences = conferences;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

}