package agentBehaviours;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;
import java.util.ArrayList;


//FIPA Request Responder
public class LibrarianListenBehaviour extends CyclicBehaviour {

    MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.REFUSE),
                                MessageTemplate.MatchPerformative(ACLMessage.AGREE)),
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.MatchPerformative(ACLMessage.INFORM)));

    private ArrayList<AID> floorsSecurity = new ArrayList<>();
    private int numResponses = -1;
    private int bestSatisfacton = -1;
    private int bestFloor = -1;
    private AID studentAID = null;
    private ACLMessage lastRequestSentToSecurity;


    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()){
                case ACLMessage.REQUEST:  // student request
                    Logs.write(myAgent.getName() + " RECEIVED REQUEST FROM " + msg.getSender(), "librarian");
                    reply = handleStudentRequest(msg, reply);
                    Logs.write(myAgent.getName() + " SENT REPLY: \n" + reply, "librarian");
                    myAgent.send(reply);
                    break;
                case ACLMessage.AGREE:
                    Logs.write(myAgent.getName() + " RECEIVED AGREE FROM " + msg.getSender(), "librarian");
                    break;
                case ACLMessage.REFUSE:
                    Logs.write(myAgent.getName() + " RECEIVED REFUSE FROM " + msg.getSender(), "librarian");
                    handleRefuse(msg.getSender());
                    return;
                    //break;
                case ACLMessage.INFORM:   // security inform
                    Logs.write(myAgent.getName() + " RECEIVED INFORM FROM " + msg.getSender() + "\n" + msg, "librarian");
                    handleSecurityInform(msg);
                    break;
                default:
                    break;
            }
        } else {
            block();
        }
    }

    private void handleRefuse(AID sender) {
        myAgent.addBehaviour(new WakerBehaviour(myAgent, 200) {
            @Override
            protected void onWake() {
                super.onWake();
                ACLMessage request = lastRequestSentToSecurity;
                request.clearAllReceiver();
                request.addReceiver(sender);
                myAgent.send(request);
                Logs.write(myAgent.getName() + " SENT REQUEST:\n" + request, "librarian");
            }
        });
    }

    private void handleSecurityInform(ACLMessage inform) {
        numResponses++;

        String content = inform.getContent();
        String[] array = content.split(" ");

        int curr_sat = Integer.parseInt(array[0]);
        int curr_floor = Integer.parseInt(array[1]);
        if(curr_sat > bestSatisfacton){
            bestSatisfacton = curr_sat;
            bestFloor = curr_floor;
        }

        if(numResponses == floorsSecurity.size()){
            sendStudentResponse();
        }
    }

    private void sendStudentResponse() {
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.setSender(myAgent.getAID());
        reply.addReceiver(studentAID);
        if(bestSatisfacton == 0){
            reply.setOntology("NO_FREE_SPACE");
            reply.setContent("Unfortunately the library is full.");
        }
        else{
            reply.setOntology("BEST_FLOOR");
            reply.setContent(String.valueOf(bestFloor));
        }

        myAgent.send(reply);
        Logs.write(myAgent.getName() + " SENT REPLY:\n" + reply, "librarian");
        numResponses = -1;
    }

    private ACLMessage handleStudentRequest(ACLMessage request, ACLMessage reply) {
        switch (request.getOntology()){
            case "TABLE":
                //agree
                if(numResponses != -1){
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("TABLE request refused");
                }
                else{
                    studentAID = request.getSender();
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("TABLE request accepted");
                    sendRequestToSecurity(request);
                    numResponses = 0;
                    bestSatisfacton = -1;
                    bestFloor = -1;

                }
                break;
            case "BOOK":
                //refuse
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("BOOK request refused");
                break;
            default:
                //refuse
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                break;
        }
        return reply;
    }

    private void sendRequestToSecurity(ACLMessage request){
        request.clearAllReceiver();
        getFloorsSecurityAID();
        for (AID aid : floorsSecurity) {
            request.addReceiver(aid);
        }
        request.setSender(myAgent.getAID());
        lastRequestSentToSecurity = request;
        myAgent.send(request);
        Logs.write(myAgent.getName() + " SENT REQUEST:\n" + request, "librarian");
    }

    private void getFloorsSecurityAID() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("security");
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(myAgent, dfd);
            floorsSecurity = new ArrayList<AID>();

            for (DFAgentDescription agent : result) {
                Logs.write(myAgent + " FOUND " + agent.getName(), "librarian");
                floorsSecurity.add(agent.getName());
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
