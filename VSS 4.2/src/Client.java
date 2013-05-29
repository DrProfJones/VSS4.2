import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client
{
	public static void main(String[] args) throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry();
		IHello hello = (IHello) registry.lookup("Hello");
		System.out.println("Sende \"Hallo\" an Server");
		hello.hello("Hallo");
	}
}