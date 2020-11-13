package agentBehaviours;

import java.util.Date;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

public class StudentRequestTable extends WakerBehaviour {

	public StudentRequestTable(Agent a, long timeout) {
		super(a, timeout);
	}

	protected void onWake() {
		System.out.println("Agent " + this.myAgent.getLocalName() + " started working at " + new Date(this.getWakeupTime()));
	}
}
