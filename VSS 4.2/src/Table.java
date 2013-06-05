import java.rmi.RemoteException;
import java.util.ArrayList;

public class Table extends Thread implements ITable
{
	private final ArrayList<Plate>			plates;
	private final ArrayList<Philosopher>	philosophers;
	private final ArrayList<Philosopher>	blockedPhilosophers;
	private int								size;
	private int								median			= 0;
	// Hier evtl. eine Liste mit "Loads" von mehreren Clients im Netz
	private LoadStatus						otherClientLoad	= new LoadStatus();
	private Control							control;

	public Table(final int numberOfPlates, Control c)
	{
		this.control = c;
		plates = new ArrayList<Plate>();
		philosophers = new ArrayList<Philosopher>();
		blockedPhilosophers = new ArrayList<Philosopher>();

		if (numberOfPlates < 2)
			size = 2;
		else
			size = numberOfPlates;

		final Plate first = new Plate();
		first.setFirst(true);
		plates.add(first);
		Plate next = first;

		for (int i = 0; i < size - 1; i++)
		{
			next = new Plate(next);
			plates.add(next);
		}

		first.setNextPlate(next);

		for (final Plate p : plates)
		{
			p.start();
		}
	}

	public Table(Table oldTable)
	{
		this.plates = oldTable.plates;
		this.philosophers = oldTable.philosophers;
		this.blockedPhilosophers = oldTable.blockedPhilosophers;
		this.size = oldTable.size;
		this.median = oldTable.median;
		this.otherClientLoad = oldTable.otherClientLoad;
		this.control = oldTable.control;
	}

	@Override
	public void addPlate()
	{
		Plate p = new Plate();
		final Plate before = plates.get(0);
		p.setNextPlate(before.getNextPlate());
		before.setNextPlate(p);
		size++;
		p.start();
	}

	@Override
	public boolean removePlate()
	{
		if (plates.size() > 2)
		{
			final Plate before = plates.get(0);
			final Plate toRemove = before.getNextPlate();
			before.setNextPlate(toRemove.getNextPlate());
			toRemove.setNextPlate(null);
			plates.remove(toRemove);
			size--;
			return true;
		}
		// TODO was passiert wenn ein Philosoph an soeinem Platz am essen ist?
		// TODO Queues Leeren
		return false;
	}

	// @Override
	// public Plate getPlate(final int i)
	// {
	// return plates.get(i);
	// }
	//

	public ArrayList<Plate> getPlates()
	{
		return plates;
	}

	//
	// @Override
	// public int getSize()
	// {
	// return size;
	// }

	// Philosoph wirdzum essen gelassen, unabhängig davon ob er auf dem Ursprungsrechner geblockt war.
	@Override
	public void addPhilosopher(final Philosopher p)
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

	@Override
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

	public void updatePhilosophers()
	{
		for(Philosopher p : philosophers)
		{
			p.setTable(this);
		}
	}
	
	// public void registerPhilosopher(final Philosopher p)
	// {
	// philosophers.add(p);
	// }

	@Override
	public void setStatus(final LoadStatus newLoad) throws RemoteException
	{
		this.otherClientLoad = newLoad;
	}

	private void slowHungryPhilosophers()
	{

		if (philosophers.size() > 0)
		{
			int med = 0;

			// System.out.println(philosophers + "free");
			// System.out.println(blockedPhilosophers + "blocked");
			for (final Philosopher p : philosophers)
			{
				// if (!blockedPhilosophers.contains(p))
				med += p.getTimesEaten();
			}

			med /= (philosophers.size());// - blockedPhilosophers.size());
			median = med;
			System.out.printf("%100s\n", "Median: " + med);

			for (final Philosopher p : philosophers)
			{
				if (med > 1 && p.getTimesEaten() > 10 + med)
				{
					System.out.println("block");
					p.setBlocked(true);
					if (!blockedPhilosophers.contains(p))
					{
						blockedPhilosophers.add(p);
					}
				}
			}

			final ArrayList<Philosopher> toRemove = new ArrayList<Philosopher>();
			for (final Philosopher p : blockedPhilosophers)
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

	}

	private void sendStatus()
	{
		// TODO
	}

	private void balanceLoad()
	{
		// TODO
	}

	private void checkCotrol()
	{
		if (control.getState() == Thread.State.TERMINATED)
		{
			System.out.println("kaputte control gefunden");
			control = new Control(control);
			control.start();
			System.out.println("kaputte control repariert");
		}
	}

	private void checkPhilosophers()
	{
		final ArrayList<Philosopher> toRemove = new ArrayList<>();
		final ArrayList<Philosopher> toRestart = new ArrayList<>();

		for (final Philosopher p : philosophers)
		{
			if (p.getState() == Thread.State.TERMINATED)
			{
				System.out.println("kaputter Philosoph gefunden");
				toRemove.add(p);
				toRestart.add(new Philosopher(p));
				System.out.println("kaputter Philosoph repariert");
			}
		}

		philosophers.removeAll(toRemove);
		philosophers.addAll(toRestart);
		// logging
		System.out.println(toRestart);
		System.out.println(toRemove);

		for (final Philosopher p : toRestart)
		{
			System.out.println("kaputter Philosoph neustarten");
			p.start();
		}
	}

	private void checkPlates()
	{
		final ArrayList<Plate> toRemove = new ArrayList<>();
		final ArrayList<Plate> toRestart = new ArrayList<>();

		for (final Plate p : plates)
		{
			if (p.getState() == Thread.State.TERMINATED)
			{
				System.out.println("kaputte Plate gefunden");
				toRemove.add(p);
				toRestart.add(new Plate(p));
				System.out.println("kaputte Plate repariert");
			}
		}

		plates.removeAll(toRemove);
		plates.addAll(toRestart);
		// logging
		System.out.println(toRestart);
		System.out.println(toRemove);

		for (final Plate p : toRestart)
		{
			System.out.println("kaputte Plate neustarten");
			p.start();
		}
	}

	@Override
	public void run()
	{
		while (true)
		{
			// philosophen, die zuviel essen werden gebremst
			slowHungryPhilosophers();

			// Control, Philosophen und Teller auf Leben überprüfen
			checkCotrol();
			checkPhilosophers();
			checkPlates();

			// Load von anderem System prüfen und ausbalancieren
			balanceLoad();

			// eigenen Status an anderen Client senden
			sendStatus();

			try
			{
				Thread.sleep(5000);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
