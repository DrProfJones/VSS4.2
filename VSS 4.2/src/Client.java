import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client
{
	public static void main(String[] args) throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry();
		ITable tab = (ITable) registry.lookup("Table");
		System.out.println("Sende Philosopher an Server");
		Philosopher p = new Philosopher("ALU ALU ALU ALU ALU", null, false);
		tab.addPhilosopher(p);
	}
}