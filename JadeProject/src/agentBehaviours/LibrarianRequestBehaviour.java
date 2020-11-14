package agentBehaviours;

import java.util.Vector;

import agents.Librarian;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import library.Logs;

//FIPA Request Initiator
public class LibrarianRequestBehaviour extends AchieveREInitiator {
    private final Librarian librarian;

    public LibrarianRequestBehaviour(Librarian a, ACLMessage msg) {
        super(a, msg);
        this.librarian = a;
    }

    protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
        Vector<ACLMessage> v = new Vector<ACLMessage>();

        msg.clearAllReceiver();

        for(int i=0; i<librarian.getFloorsSecurity().size(); i++){
            msg.addReceiver(librarian.getFloorsSecurity().get(i));
        }

        msg.setSender(librarian.getAID());
        v.add(msg);

        return v;
    }

    protected void handleAgree(ACLMessage agree) {
        Logs.write(librarian.getName() + " " + agree.getContent(), "librarian");
    }

    protected void handleRefuse(ACLMessage refuse) {
        Logs.write(librarian.getName() + " " + refuse.getContent(), "librarian");
    }

    protected void handleInform(ACLMessage inform) {
        ACLMessage msg = inform.createReply();
        System.out.println(inform);
        msg.clearAllReceiver();
        msg.setPerformative(ACLMessage.INFORM);
        //msg.addReceiver();
    }

    protected void handleFailure(ACLMessage failure) {
        // ...
    }

}
