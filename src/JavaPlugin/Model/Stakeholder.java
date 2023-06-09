package JavaPlugin.Model;

import com.telelogic.rhapsody.core.IRPActor;

/**
 * @author adumez
 *
 */
public class Stakeholder implements Comparable<Stakeholder>{
	private IRPActor itself;
	private Double value;
	
	
	/**
	 * @param actor
	 * @param value
	 */
	public Stakeholder(IRPActor actor, Double value) {
		this.itself = actor;
		this.value = value;
	}
	
	@SuppressWarnings("boxing")
	@Override
	public int compareTo(Stakeholder o) {
		if (this.value < o.value) {
			return 1;
		} else if (this.value > o.value) {
			return -1;
		}
		return 0;
	}
	/**
	 * @return The IRPActor of this
	 */
	public IRPActor getItself() {
		return this.itself;
	}
	/**
	 * @param itself
	 */
	public void setItself(IRPActor itself) {
		this.itself = itself;
	}
	/**
	 * @return The value of this
	 */
	public Double getValue() {
		return this.value;
	}
	/**
	 * @param value
	 */
	public void setValue(Double value) {
		this.value = value;
	}

}
