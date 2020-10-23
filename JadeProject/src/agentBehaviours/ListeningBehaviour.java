package agentBehaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningBehaviour extends CyclicBehaviour {

    Agent agent;

    public ListeningBehaviour(Agent a){
        this.agent = a;
    }

    public void action() {
        ACLMessage msg = agent.receive();
        if(msg != null) {
            System.out.println(msg);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            agent.send(reply);
        } else {
            block();
        }
    }

}
