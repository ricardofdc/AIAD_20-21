package agentBehaviours;

import agents.Student;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

public class StudentListenBehaviour extends CyclicBehaviour {
    MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM)),
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                    MessageTemplate.MatchPerformative(ACLMessage.NOT_UNDERSTOOD)));

    @Override
    public void action() {

        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            switch (msg.getPerformative()){
                case ACLMessage.AGREE:
                    Logs.write(myAgent.getName() + " RECEIVED AGREE FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.REFUSE:
                    handleRefuse();
                    Logs.write(myAgent.getName() + " RECEIVED REFUSE FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.NOT_UNDERSTOOD:
                    Logs.write(myAgent.getName() + " RECEIVED NOT_UNDERSTOOD FROM " + msg.getSender(), "student");
                    break;
                case ACLMessage.INFORM:
                    Logs.write(myAgent.getName() + " RECEIVED INFORM FROM " + msg.getSender(), "student");
                    break;
            }

        } else {
            block();
        }
    }

    private void handleRefuse() {
        myAgent.addBehaviour(new WakerBehaviour(myAgent, 500) {
            @Override
            protected void onWake() {
                super.onWake();
                myAgent.addBehaviour(new StudentRequestBehaviour());
            }
        });
    }

}
