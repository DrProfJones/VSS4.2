import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ITable extends Remote
{
	public void addPlate() throws RemoteException;
	
	public boolean removePlate() throws RemoteException;
	
	public void addPhilosopher(Philosopher p) throws RemoteException;
	
	public Philosopher removePhilosopher() throws RemoteException;
	
	public void setStatus(LoadStatus newLoad) throws RemoteException;
}
