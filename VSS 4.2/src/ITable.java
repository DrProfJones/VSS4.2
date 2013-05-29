import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ITable extends Remote
{
	public void addPlate(Plate p) throws RemoteException;
	
	public void addPlate() throws RemoteException;
	
	public boolean removePlate() throws RemoteException;

	public Plate getPlate(int i) throws RemoteException;

	public ArrayList<Plate> getPlates() throws RemoteException;

	public int getSize() throws RemoteException;
	
	public void addPhilosopher(Philosopher p) throws RemoteException;
	
	public Philosopher removePhilosopher() throws RemoteException;
}
