import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Philosopher extends Thread
{
	private final Random	r						= new Random();
	private final Table		table;
	private final String	name;
	private final boolean	isHungry;
	private boolean			isBlocked = false;
	private int				timesEaten;
	private static int		nrOfHobbyPhilosophers	= 1;
	private final int		maxWait;

	public Philosopher(final String name, Table t, boolean hungry)
	{
		this.table = t;
		this.isHungry = hungry;
		
		String tmpName = name == null ? "Hobby Philosoph " + nrOfHobbyPhilosophers++ : name;
		tmpName += isHungry ? " H" : "";		
		this.name = tmpName;
		this.maxWait = isHungry ? Control.MAXWAIT / 3 : Control.MAXWAIT;
		table.addPhilosopher(this);
	}

	@Override
	public void run()
	{
		while (true)
		{
			log("meditating");
			meditate();
			log("going to dine");

			if (isBlocked)
				waitBeforeEating();

			dine();

			timesEaten++;
		}
	}

	private void waitBeforeEating()
	{
		try
		{
			synchronized (this)
			{
				this.wait();
			}
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void meditate()
	{
		try
		{

			sleep(r.nextInt(this.maxWait));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void dine()
	{
		long start;
		if (Control.PERFORM)
			start = System.nanoTime();
		// TODO Platzwahl der Philosophen
		List<Plate> plates = table.getPlates();
		Plate pMin = plates.get(r.nextInt(plates.size()));

		for (Plate p : plates)
		{
			if (p.getQueueLength() < pMin.getQueueLength())
				pMin = p;
		}
		if (Control.PERFORM)
		{
			Control.perform(System.nanoTime() - start);
		}
		pMin.addToQueue(this);
		// TODO Ende

		try
		{
			synchronized (this)
			{
				this.wait();
			}
		}

		catch (InterruptedException e)
		{

		}
	}

	public String getPhilosopherName()
	{
		return name;
	}

	private void log(String s)
	{
		if (Control.LOG)
			System.out.printf("%-25s %-16s\n", name + " [" + timesEaten + "] ", s);
	}

	public int getTimesEaten()
	{
		return timesEaten;
	}

	public void setBlocked(boolean isBlocked)
	{
		this.isBlocked = isBlocked;
	}
}
