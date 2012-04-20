package scheduler.model.db.simple;

import java.util.Set;
import java.util.TreeSet;

import scheduler.model.db.IDBDayPattern;

public class DBDayPattern extends DBObject implements IDBDayPattern {
	public DBDayPattern(Integer id) {
		super(id);
	}
	
	public DBDayPattern(DBDayPattern that) {
		this(that.id);
	}

	public DBDayPattern(Set<Integer> dayPattern) {
		super(0);
		setDays(dayPattern);
	}

	@Override
	public Set<Integer> getDays() {
		Set<Integer> result = new TreeSet<Integer>();
		for (int i = 0; i < 7; i++)
			if ((id & (1 << i)) > 0)
				result.add(i);
		return result;
	}

	@Override
	public void setDays(Set<Integer> days) {
		id = 0;
		for (int i = 0; i < 7; i++)
			if (days.contains(i))
				id |= 1 << i;
	}

	public void sanityCheck() {
	}

}
