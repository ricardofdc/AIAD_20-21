package agents;

import agentBehaviours.SecurityListenBehaviour;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;
import library.Logs;

public class Security extends Agent {
    private final Floor floor;
    private final int noiseTolerance;
    private int numberKicks;

    public Security(Floor floor, int noiseTolerance){
        this.floor = floor;
        this.noiseTolerance = noiseTolerance;
        this.numberKicks = 0;
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
            Logs.write(this.getName() + " REGISTERED AS SECURITY WITH NOISE_TOLERANCE=" + this.noiseTolerance, "security", floor.getfloorNr());
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "security", floor.getfloorNr());
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    public int getNumberKicks() {
        return numberKicks;
    }

    public void addKick() {
        numberKicks++;
    }
}
