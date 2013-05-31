import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client
{
	public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException
	{
		Registry registry = LocateRegistry.getRegistry("192.168.60.175");
		ITable tab = (ITable) registry.lookup("Table");

		System.out.println("Sende Philosopher an Server");

		

		System.out.println("F�ge 3 plates hinzu");
		
//		for (int i = 0; i < 1000000; i++)
//		{
//			Philosopher p = new Philosopher("CHAOS!!!" + i, null, false);
//			tab.addPhilosopher(p);
//			
//		}
		
		for (int i = 0; i < 400000; i++)
		{
			
			tab.addPlate();
			
		}
		Thread.sleep(5000);

		System.out.println("gr��e des tisches: " + tab.getSize());

	}
}