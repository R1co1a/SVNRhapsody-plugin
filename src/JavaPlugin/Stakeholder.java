package JavaPlugin;

import com.telelogic.rhapsody.core.IRPActor;

public class Stakeholder implements Comparable<Stakeholder>{
	private IRPActor itself;
	private Double value;
	
	
	public Stakeholder(IRPActor actor, Double value) {
		this.itself = actor;
		this.value = value;
	}
	
	@Override
	public int compareTo(Stakeholder o) {
		if (this.value < o.value) {
			return 1;
		} else if (this.value > o.value) {
			return -1;
		}
		return 0;
	}
	public IRPActor getItself() {
		return itself;
	}
	public void setItself(IRPActor itself) {
		this.itself = itself;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

}
