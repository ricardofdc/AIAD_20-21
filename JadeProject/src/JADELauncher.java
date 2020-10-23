import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JADELauncher {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile p1 = new ProfileImpl();
        p1.setParameter(Profile.GUI, "true");
        ContainerController mainContainer = rt.createMainContainer(p1);

        AgentController ac1;
        try {
            ac1 = mainContainer.acceptNewAgent("helloWorldAgent", new HelloWorldAgent());
            ac1.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }

}
