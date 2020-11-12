package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;

public class Table extends Agent {
    private final library.Floor floor;
    private boolean isFree;


    public Table(library.Floor floor){
        this.floor=floor;
        this.isFree = true;
    }

    public library.Floor getFloor(){ return floor; }


    public boolean isFree(){
        return isFree;
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
