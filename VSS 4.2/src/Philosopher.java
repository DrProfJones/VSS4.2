import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Philosopher extends Thread implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final Random		r					= new Random();
	private Table				table;
	private final String		name;
	private final boolean		isHungry;
	private boolean				isBlocked			= false;
	private int					timesEaten;

	private final int			maxWait;

	public Philosopher(final String name, Table t, boolean hungry)
	{
		this.table = t;
		this.isHungry = hungry;
		this.name = name;
		this.maxWait = isHungry ? Control.MAXWAIT / 3 : Control.MAXWAIT;
	}

	public Philosopher(Philosopher p)
	{
		this(p.name, p.table, p.isHungry);
		this.timesEaten = p.timesEaten;
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
				waitUntilWakeup();

			dine();

			timesEaten++;
		}
	}

	private void waitUntilWakeup()
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

		List<Plate> plates = table.getPlates();
		Plate pMin = plates.get(r.nextInt(plates.size()));

		for (Plate p : plates)
		{
			if (p.getQueueLength() < pMin.getQueueLength())
				pMin = p;
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

	public void setTimesEaten(int timesEaten)
	{
		this.timesEaten = timesEaten;
	}

	public void setBlocked(boolean isBlocked)
	{
		this.isBlocked = isBlocked;
	}

	public boolean isBlocked()
	{
		return isBlocked;
	}

	public void setTable(Table table)
	{
		this.table = table;
		table.registerPhilosopher(this);
	}

	public Table getTable()
	{
		return table;
	}
}
