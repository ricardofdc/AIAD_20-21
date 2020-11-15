package agents;

import agentBehaviours.SecurityListenBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;
import library.Logs;

import java.util.ArrayList;

public class Security extends Agent {
    private final Floor floor;
    private final int noiseTolerance;

    public Security(Floor floor, int noiseTolerance){
        this.floor=floor;
        this.noiseTolerance=noiseTolerance;
    }

    public Floor getFloor(){ return floor; }

    public int getNoiseTolerance() { return noiseTolerance; }

    public void setup() {
        registerSecurity();
        addBehaviour(new SecurityListenBehaviour(this));
    }

    private void registerSecurity() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("security");
        sd.setName(this.getName()); // name: security_1@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            Logs.write(this.getName() + " REGISTERED AS SECURITY", "security");
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "security");
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

}
