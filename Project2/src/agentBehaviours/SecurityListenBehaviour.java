package agentBehaviours;

import agents.Security;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import library.Logs;

import java.util.ArrayList;
import java.util.Random;

public class SecurityListenBehaviour extends CyclicBehaviour {
    private final int floorNr;
    MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM),
            					MessageTemplate.MatchPerformative(ACLMessage.INFORM)),
            MessageTemplate.or( MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM)));

    private int numResponses = -1;
    private int numTables = 0;
    private String course = "";
    private AID librarianAID;
    private ArrayList<AID> tables = new ArrayList<>();
    
    private Random rnd = new Random();

    public SecurityListenBehaviour(Security security) {
        super();
        this.floorNr = security.getFloor().getfloorNr();
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()){
                case ACLMessage.REQUEST:        // librarian request
                    Logs.write(myAgent.getName() + " RECEIVED REQUEST FROM " + msg.getSender(), "security", floorNr);
                    reply = handleLibrarianRequest(msg, reply);
                    myAgent.send(reply);
                    Logs.write(myAgent.getName() + " SENT REPLY:\n" + reply, "security", floorNr);
                    break;
                case ACLMessage.CONFIRM:        //tables response
                    Logs.write(myAgent.getName() + " RECEIVED CONFIRM FROM " + msg.getSender(), "security", floorNr);
                    numTables++;
                    numResponses++;
                    //freeTable = msg.getSender();
                    if(numResponses == tables.size()){
                        replyLibrarian(getSecuritySatisfaction());
                        numTables = 0;
                        numResponses = -1;
                    }
                    break;
                case ACLMessage.DISCONFIRM:     //tables response
                    Logs.write(myAgent.getName() + " RECEIVED DISCONFIRM FROM " + msg.getSender(), "security", floorNr);
                    numResponses++;
                    if(numResponses == tables.size()){
                        replyLibrarian(getSecuritySatisfaction());
                        numTables = 0;
                        numResponses = -1;
                    }
                    break;
                case ACLMessage.INFORM:
                	Logs.write(myAgent.getName() + " RECEIVED INFORM FROM " + msg.getSender(), "security", floorNr);
                	handleInform(msg);
                	break;
                default:
                    break;
            }
        } else {
            block();
        }
    }

    private void handleInform(ACLMessage msg) {
		switch (msg.getOntology()) {
		case "NOISE":
			int rndTolerance = rnd.nextInt(11);
			
			if (rndTolerance > ((Security)myAgent).getNoiseTolerance()) {
				ACLMessage reply = msg.createReply();
				
				reply.setPerformative(ACLMessage.INFORM);
				reply.setOntology("KICK");
				
				((Security)myAgent).addKick();

                Logs.write(myAgent.getName() + " SENT KICK INFORM:\n" + reply, "security", floorNr);
				myAgent.send(reply);
			}
			
			break;
		}
	}

	private ACLMessage handleLibrarianRequest(ACLMessage request, ACLMessage reply) {
        switch (request.getOntology()){
            case "TABLE":
                //agree
                if(numResponses != -1){
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("TABLE request refused");
                }
                else {
                    reply.setPerformative(ACLMessage.AGREE);
                    reply.setContent("TABLE request accepted");
                    this.course = request.getContent();
                    this.librarianAID = request.getSender();
                    this.numResponses = 0;
                    getFreeTables();
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

    void getFreeTables(){
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.clearAllReceiver();
        tables = getTablesAID();
        for (AID table : tables) {
            request.addReceiver(table);
        }
        request.setOntology("TABLE");
        request.setContent("isFree?");
        request.setSender(myAgent.getAID());
        myAgent.send(request);
        Logs.write(myAgent.getName() + " SENT REQUEST:\n" + request, "security", floorNr);
    }

    private ArrayList<AID> getTablesAID() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        ArrayList<AID> tables = new ArrayList<>();

        sd.setType("table_" + ((Security)myAgent).getFloor().getfloorNr());
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(myAgent, dfd);
            tables = new ArrayList<AID>();

            for (DFAgentDescription agent : result) {
                Logs.write(myAgent.getName() + " FOUND " + agent.getName(), "security", floorNr);
                tables.add(agent.getName());
            }
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        return tables;
    }



    int getSecuritySatisfaction(){
        if(numTables == 0){
            return 0;
        }
        int satisfaction = 0;
        if(this.course.equals(((Security)myAgent).getFloor().getCourse())){
            satisfaction += 50;
        }
        satisfaction += numTables * 50 / tables.size();

        return satisfaction;

    }

    private void replyLibrarian(int satisfaction) {
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        reply.clearAllReceiver();
        reply.addReceiver(librarianAID);
        reply.setOntology("SATISFACTION");
        reply.setContent(satisfaction + " " + ((Security)myAgent).getFloor().getfloorNr());
        reply.setSender(myAgent.getAID());
        myAgent.send(reply);
        Logs.write(myAgent.getName() + " SENT REPLY:\n" + reply, "security", floorNr);
    }
}
