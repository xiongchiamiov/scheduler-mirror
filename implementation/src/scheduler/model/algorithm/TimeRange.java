package scheduler.model.algorithm;

public class TimeRange {
	int begin;
	int end;
	public TimeRange(int begin, int end) {
		this.begin = begin;
		this.end = end;
	}
	public TimeRange(TimeRange that) {
		begin = that.begin;
		end = that.end;
	}
	public int getS() {
		return begin;
	}
	public void setS(int begin) {
		this.begin = begin;
	}
	public int getE() {
		return end;
	}
	public void setE(int end) {
		this.end = end;
	}
	public void addHalf() {
		if (this.getE() > 44)
			return;
		this.setS(this.getS() + 1);
		this.setE(this.getE() + 1);
	}
	
	public String toString() {
		return this.getS() + " to " + this.getE();
	}
}
