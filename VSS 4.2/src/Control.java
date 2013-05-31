import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Control extends Thread
{

	private static final int NR_PHILOSOPHERS = 8;
	private static final int NR_HUNGRY_PHILOSOPHERS = 2;
	
	private static final int NR_PLATES = 5;
	public static final int MAXWAIT = 100;
	public static final boolean LOG = true;
	private static long TSUM = 0;
	private static long CSUM = 0;
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("./philosophers.txt"));

		Table table = new Table(NR_PLATES);
		table.start();

		for (int i = 0; i < NR_PHILOSOPHERS; i++)
		{
			Philosopher p = new Philosopher(br.readLine(), table, false);
			table.registerPhilosopher(p);
			p.start();
		}
		for (int i = 0; i < NR_HUNGRY_PHILOSOPHERS; i++)
		{
			Philosopher p = new Philosopher(br.readLine(), table, true);
			table.registerPhilosopher(p);
			p.start();
		}
		

		// Server starten
		try
		{
		  LocateRegistry.createRegistry( Registry.REGISTRY_PORT );
		}
		catch ( RemoteException e )  
		{
			
		}
		
		ITable tabStub = (ITable) UnicastRemoteObject.exportObject(table,0);
		
		Registry registry = LocateRegistry.getRegistry();
	    registry.rebind( "Table", tabStub);

	    System.out.println("Server l�uft");
		
	}

	@Override
	public void run()
	{
		
	}
	
	
	
	public synchronized static void perform(long l)
	{
		TSUM += l;
		CSUM++;
		System.out.println(TSUM/CSUM);
		
	}

}
