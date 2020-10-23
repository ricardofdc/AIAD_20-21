package agents;

import agentBehaviours.*;
import jade.core.Agent;

public class Student extends Agent {

    private final String id;
    private final String course;
    private final int noise;

    public Student(String id, String course, int noise){
        this.id = id;
        this.course = course;
        this.noise = noise;

        //later we can make noise and course be random
    }

    public String getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }

    public int getNoise() {
        return noise;
    }



    public void setup() {
        addBehaviour(new WorkingBehaviour());
        addBehaviour(new ListeningBehaviour(this));

        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
