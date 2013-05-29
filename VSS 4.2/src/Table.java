import java.util.ArrayList;

public class Table extends Thread implements ITable
{
	private ArrayList<Plate>		plates				= new ArrayList<Plate>();
	private ArrayList<Philosopher>	philosophers		= new ArrayList<Philosopher>();
	private ArrayList<Philosopher>	blockedPhilosophers	= new ArrayList<Philosopher>();
	private int						size;
	private int						median				= 0;

	public Table(int numberOfPlates)
	{
		size = numberOfPlates;

		Plate first = new Plate();
		first.setFirst(true);
		plates.add(first);
		Plate next = first;

		for (int i = 2; i <= numberOfPlates - 2; i++)
		{
			next = new Plate(next);
			plates.add(next);
		}

		first.setNextPlate(next);

		for (Plate p : plates)
			p.start();
	}

	public void addPlate(Plate p)
	{
		Plate before = plates.get(0);
		p.setNextPlate(before.getNextPlate());
		before.setNextPlate(p);
		size++;
		p.start();
		// TODO was passiert wenn ein Philosoph an soeinem Platz am essen ist?
	}

	public void addPlate()
	{
		addPlate(new Plate());
	}

	public boolean removePlate()
	{
		if (plates.size() > 1)
		{
			Plate before = plates.get(0);
			Plate toRemove = before.getNextPlate();
			before.setNextPlate(toRemove.getNextPlate());
			toRemove.setNextPlate(null);
			plates.remove(toRemove);
			size--;
			return true;
		}

		return false;
	}

	public Plate getPlate(int i)
	{
		return plates.get(i);
	}

	public ArrayList<Plate> getPlates()
	{
		return plates;
	}

	public int getSize()
	{
		return size;
	}

	// Philosoph wirdzum essen gelassen, unabhängig davon ob er auf dem Ursprungsrechner geblockt war.
	public void addPhilosopher(Philosopher p)
	{
		p.setTable(this);
		p.setTimesEaten(median);
		philosophers.add(p);
		p.start();
		p.setBlocked(false);
		synchronized (p)
		{
			p.notify();
		}
	}

	public void registerPhilosopher(Philosopher p)
	{
		philosophers.add(p);
	}

	public Philosopher removePhilosopher()
	{
		Philosopher p = null;
		if (!philosophers.isEmpty())
		{
			p = philosophers.remove(0);
			p.setBlocked(true);
			blockedPhilosophers.remove(p);
		}
		return p;
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (philosophers.size() > 0)
			{
				int med = 0;

				// System.out.println(philosophers + "free");
				// System.out.println(blockedPhilosophers + "blocked");
				for (Philosopher p : philosophers)
				{
					// if (!blockedPhilosophers.contains(p))
					med += p.getTimesEaten();
				}

				med /= (philosophers.size());// - blockedPhilosophers.size());
				median = med;
				System.out.printf("%100s\n", "Median: " + med);

				for (Philosopher p : philosophers)
				{
					if (med > 1 && p.getTimesEaten() > 10 + med)
					{
						System.out.println("block");
						p.setBlocked(true);
						if (!blockedPhilosophers.contains(p))
							blockedPhilosophers.add(p);
					}
				}

				ArrayList<Philosopher> toRemove = new ArrayList<Philosopher>();
				for (Philosopher p : blockedPhilosophers)
				{
					if (p.getTimesEaten() <= med + 10)
					{
						p.setBlocked(false);
						synchronized (p)
						{
							p.notify();
						}
						System.out.println("release");
						toRemove.add(p);
					}
				}
				blockedPhilosophers.removeAll(toRemove);

			}
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
