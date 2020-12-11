package agents;

import agentBehaviours.*;
import jade.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.WakerBehaviour;
import sajas.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import library.Logs;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;


public class Student extends Agent implements Drawable {

    private final String course;
    private final int noise;
    private final int action; // 0 -> get table; 1 -> get book
    private final int timeOfArrival;

    public Color color;

    private int x, y;
    
    private AID tableAID;
    private int color_counter = 0;

    public Student( String course, int noise, int action, int timeOfArrival, int num) {
        this.course = course;
        this.noise = noise;
        this.action = action;
        this.timeOfArrival = timeOfArrival;	//milliseconds
        this.setXY(1, num * 2);
        this.color = Color.BLACK;

    }

    public void setColor(Color color){
        this.color = color;
    }
    
    public void setTableAID(AID tableAID) {
    	this.tableAID = tableAID;
    }
    
    public AID getTableAID() {
    	return tableAID;
    }

    public String getCourse() {
        return course;
    }

    public int getNoise() {
        return noise;
    }

    public int getAction() {
        return action;
    }

    public void setup() {
    	super.setup();

    	registerStudent();

        addBehaviour(new WakerBehaviour(this, timeOfArrival) {
            @Override
            protected void onWake() {
                super.onWake();
                ((Student)myAgent).setColor(Color.PINK);
                addBehaviour(new StudentRequestBehaviour());
                addBehaviour(new StudentListenBehaviour());

            }
        });
    }

    private void registerStudent(){
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("student");
        sd.setName(this.getName()); // name: 008_barbara@192.168.1.91:1099/JADE
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            Logs.write(this.getName() + " REGISTERED AS STUDENT WITH NOISE=" + this.noise, "student");
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
            Logs.write(this.getName() + " TAKEN DOWN AND UNREGISTERED FROM DFSERVICE", "student");
            setXY(1, 1);
            setColor(Color.black);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        if(color_counter == 5){
            color = Color.PINK;
        }
        if(color == Color.RED){
            color_counter++;
        }
        simGraphics.drawFastRect(color);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }
}
