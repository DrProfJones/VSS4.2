import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Plate extends Thread
{
	private boolean									first = false;
	private final Object							fork;
	private Plate									nextPlate;
	private final LinkedBlockingQueue<Philosopher>	queue	= new LinkedBlockingQueue<Philosopher>();
	private final Random							rand	= new Random();
	private Philosopher								currentPhilosopher;

	public Plate(Plate p)
	{
		fork = new Object();
		this.nextPlate = p;
	}

	public Plate()
	{
		this(null);
	}

	public Plate getNextPlate()
	{
		return nextPlate;
	}

	public void setFirst(boolean first)
	{
		this.first = first;
	}

	public boolean isFirst()
	{
		return first;
	}

	public void setNextPlate(Plate nextPlate)
	{
		this.nextPlate = nextPlate;
	}

	public Object getFork()
	{
		return fork;
	}

	@Override
	public void run()
	{
		while (true)
		{
			eat();
		}
	}

	public void eat()
	{
		try
		{
			currentPhilosopher = queue.take();

		}
		catch (InterruptedException e1)
		{

		}
		if (first)
		{
			synchronized (fork)
			{
				log(currentPhilosopher, "<- L Fork");
				synchronized (nextPlate.getFork())
				{
					log(currentPhilosopher, "<- R Fork");
					log(currentPhilosopher, "eating");
					try
					{
						Thread.sleep(rand.nextInt(Control.MAXWAIT));
					}
					catch (InterruptedException e)
					{

					}
					log(currentPhilosopher, " finished");
				}
				log(currentPhilosopher, "-> R Fork");
			}
			log(currentPhilosopher, "-> L Fork");
		}
		else
		{
			synchronized (nextPlate.getFork())
			{
				log(currentPhilosopher, "<- R Fork");
				synchronized (fork)
				{
					log(currentPhilosopher, "<- L Fork");
					log(currentPhilosopher, "eating");
					try
					{
						Thread.sleep(rand.nextInt(Control.MAXWAIT));
					}
					catch (InterruptedException e)
					{

					}
					log(currentPhilosopher, " finished");
				}
				log(currentPhilosopher, "-> L Fork");
			}
			log(currentPhilosopher, "-> R Fork");
		}
		synchronized (currentPhilosopher)
		{
			currentPhilosopher.notify();
		}
	}

	public Philosopher getCurrentPhilosopher()
	{
		return currentPhilosopher;
	}

	public int getQueueLength()
	{
		return queue.size();
	}

	public void addToQueue(Philosopher p)
	{
		queue.add(p);
	}

	private void log(Philosopher p, String s)
	{
		if (Control.LOG)
			System.out.printf("%-25s %-16s \t Queue [%d]\n", p.getPhilosopherName() + " [" + p.getTimesEaten() + "] ", s, queue.size());

	}
}
