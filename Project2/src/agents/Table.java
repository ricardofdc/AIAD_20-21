package agents;

import agentBehaviours.TableListenBehaviour;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import library.Floor;
import library.Logs;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class Table extends Agent implements Drawable {
    private final Floor floor;
    private boolean isFree;
    private int satisfaction;

    private int x, y;
    private static final Color freeColor = new Color(0, 255, 0);
    private static final Color occupiedColor = new Color(255, 0, 0);

    public Table(Floor floor, int x, int y) {
        this(floor);

        this.x = x;
        this.y = y;
    }

    public Table(Floor floor){
        this.floor=floor;
        this.isFree = true;
        this.satisfaction = 0;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Floor getFloor(){ return floor; }

    public boolean isFree(){
        return isFree;
    }
    
    public void setIsFree(boolean isFree) {
    	this.isFree = isFree;
    }

    public void setup() {
        registerTable();
        addBehaviour(new TableListenBehaviour(this));
    }

    private void registerTable() {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("table_" + this.floor.getfloorNr());
        sd.setName(this.getName()); // name: 008_barbara@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            Logs.write(this.getName() + " REGISTERED AS TABLE_" + this.floor.getfloorNr(), "table", floor.getfloorNr());
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "table", floor.getfloorNr());
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        if (isFree()) {
            simGraphics.drawFastRect(freeColor);
        } else {
            simGraphics.drawFastRect(occupiedColor);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
