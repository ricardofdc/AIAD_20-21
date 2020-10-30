package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;

public class Table extends Agent {
    private final String id;
    private final int floor;
    private final int space;

    public Table(String id, int floor, int space){
        this.id=id;
        this.floor=floor;
        this.space=space;
    }

    public String getID(){ return id; }

    public int getFloor(){ return floor; }

    public int getSpace(){ return space; }

    private int addStudent(){
        return this.space-1;
    }

    private int removeStudent(){ return this.space+1; }

    public boolean isFree(){
        if (space == 0)
            return false;
        else
            return true;
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
