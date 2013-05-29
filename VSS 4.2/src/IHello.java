import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHello extends Remote
{
	void hello(String text) throws RemoteException;
}
