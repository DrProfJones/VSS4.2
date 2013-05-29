import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client
{
	public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException
	{
		Registry registry = LocateRegistry.getRegistry();
		ITable tab = (ITable) registry.lookup("Table");
		
		System.out.println("Sende Philosopher an Server");
		
		Philosopher p = new Philosopher("CHAOS!!!", null, false);
		tab.addPhilosopher(p);
		
		//System.out.println("Füge 3 plates hinzu");
		//tab.addPlate();
		//tab.addPlate();
		//tab.addPlate();
		
		Thread.sleep(5000);
		
		System.out.println("größe des tisches: " + tab.getSize());
	
	}
}