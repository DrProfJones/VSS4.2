import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Control
{

	private static final int NR_PHILOSOPHERS = 10;
	private static final int NR_HUNGRY_PHILOSOPHERS = 10;
	
	private static final int NR_PLATES = 5;
	public static final int MAXWAIT = 50;
	public static final boolean LOG = true;
	public static final boolean PERFORM = false;
	private static long TSUM = 0;
	private static long CSUM = 0;
	
	
	
	
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("./philosophers.txt"));

		Table table = new Table(NR_PLATES);
		table.start();

		for (int i = 0; i < NR_PHILOSOPHERS; i++)
		{
			new Philosopher(br.readLine(), table, false).start();
		}
		for (int i = 0; i < NR_HUNGRY_PHILOSOPHERS; i++)
		{
			new Philosopher(br.readLine(), table, true).start();
		}
	}

	public synchronized static void perform(long l)
	{
		TSUM += l;
		CSUM++;
		System.out.println(TSUM/CSUM);
		
	}

}
