import java.util.ArrayList;

public class Table extends Thread
{
	private ArrayList<Plate>		plates				= new ArrayList<Plate>();
	private ArrayList<Philosopher>	philosophers		= new ArrayList<Philosopher>();
	private ArrayList<Philosopher>	blockedPhilosophers	= new ArrayList<Philosopher>();
	private final int				size;

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
	}

	public void addPlate()
	{
		addPlate(new Plate());
	}
	
	public boolean removePlate()
	{
		if(plates.size() > 1)
		{
			Plate before = plates.get(0);
			Plate toRemove = before.getNextPlate();
			before.setNextPlate(toRemove.getNextPlate());
			toRemove.setNextPlate(null);
			plates.remove(toRemove);
			return true;
		}
		
		return false;	
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (philosophers.size() > 0)
			{
				int median = 0;

//				System.out.println(philosophers + "free");
//				System.out.println(blockedPhilosophers + "blocked");
				for (Philosopher p : philosophers)
				{
					if (!blockedPhilosophers.contains(p))
						median += p.getTimesEaten();
				}

				median /= (philosophers.size() - blockedPhilosophers.size());
				System.out.printf("%100s\n", "Median: " + median);

				for (Philosopher p : philosophers)
				{
					if (median > 1 && p.getTimesEaten() > 10 + median)
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
					if (p.getTimesEaten() <= median + 10)
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
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public void addPhilosopher(Philosopher p)
	{
		philosophers.add(p);

	}

}
