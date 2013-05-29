import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;

class Server
{
	

	public static void main(String[] args) throws RemoteException
	{
		try
		{
		  LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
		}
		catch ( RemoteException e )  { /* ... */ }


		IHello helloStub = (IHello) UnicastRemoteObject.exportObject(new Hello(),0);

		
		Registry registry = LocateRegistry.getRegistry();
	    registry.rebind( "Hello", helloStub );

	    System.out.println("Server läuft");
		
	}

}
