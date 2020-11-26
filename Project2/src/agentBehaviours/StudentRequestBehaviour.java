package agentBehaviours;

import java.util.ArrayList;
import agents.Student;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import library.Logs;

public class StudentRequestBehaviour extends OneShotBehaviour {
	private ArrayList<AID> librarians;

	@Override
	public void action() {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

		request.clearAllReceiver();
		findLibrariansAID();
		for (AID librarian : librarians) {
			request.addReceiver(librarian);
		}
		request.setSender(myAgent.getAID());
		switch (((Student)myAgent).getAction()) {
			case 0:
				request.setOntology("TABLE");
				break;
			case 1:
				request.setOntology("BOOK");
				break;
		}
		request.setContent(((Student)myAgent).getCourse());
		myAgent.send(request);
		Logs.write(myAgent.getName() + " SENT REQUEST:\n" + request, "student");
	}

	private void findLibrariansAID() {
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();

		sd.setType("librarian");
		dfd.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(myAgent, dfd);
			librarians = new ArrayList<AID>();

			for (DFAgentDescription agent : result) {
				Logs.write(myAgent.getName() + " FOUND " + agent.getName(), "student");
				librarians.add(agent.getName());
			}
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
}
