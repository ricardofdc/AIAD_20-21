package agentBehaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class StudentListenBehaviour extends CyclicBehaviour {
    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);

    @Override
    public void action() {

        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            switch (msg.getPerformative()){
                case ACLMessage.AGREE:
                    break;
                case ACLMessage.REFUSE:
                    break;
                case ACLMessage.NOT_UNDERSTOOD:
                    break;
            }

        } else {
            block();
        }
    }

}
