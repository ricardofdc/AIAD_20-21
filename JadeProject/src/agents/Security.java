package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;

public class Security extends Agent {
    private final int floor;
    private final int noiseTolerance;

    public Security(int floor, int noiseTolerance){
        this.floor=floor;
        this.noiseTolerance=noiseTolerance;
    }

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
