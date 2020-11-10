package agents;

import agentBehaviours.ListeningBehaviour;
import jade.core.Agent;

public class Librarian extends Agent {

    public void setup() {
    	System.out.println("Librarian setup");
    	
    	addBehaviour(new ListeningBehaviour(this));
    }

}
