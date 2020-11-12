package agents;

import agentBehaviours.ListeningBehaviour;
import agentBehaviours.WorkingBehaviour;
import jade.core.Agent;

public class Security extends Agent {
    private final library.Floor floor;
    private final int noiseTolerance;

    public Security(library.Floor floor, int noiseTolerance){
        this.floor=floor;
        this.noiseTolerance=noiseTolerance;
    }

    public library.Floor getFloor(){ return floor; }

    public int getNoiseTolerance() { return noiseTolerance; }

    public void setup() {
        addBehaviour(new WorkingBehaviour());
        addBehaviour(new ListeningBehaviour(this));

        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
