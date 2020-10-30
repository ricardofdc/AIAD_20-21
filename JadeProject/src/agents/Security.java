package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;

public class Security extends Agent {
    private final String id;
    private final int floor;
    private final int noiseTolerance;

    public Security(String id, int floor, int noiseTolerance){
        this.id=id;
        this.floor=floor;
        this.noideTolerance=noiseTolerance;
    }

    public String getID(){ return id; }

    public int getFloor(){ return floor; }

    public int getNoiseTolerance() { return floor; }

    public void setup() {
        addBehaviour(new WorkingBehaviour());
        addBehaviour(new ListeningBehaviour(this));

        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
