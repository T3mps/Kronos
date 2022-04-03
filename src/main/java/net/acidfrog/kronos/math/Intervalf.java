package net.acidfrog.kronos.math;

import net.acidfrog.kronos.core.util.Validatable;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.error.KronosGeometryError;

public class Intervalf implements Validatable {

	private float min;

	private float max;
	
    public Intervalf() {
        this(0, 0);
    }

	public Intervalf(float min, float max) {
        validate();
		this.min = min;
		this.max = max;
	}
	
	public Intervalf(Intervalf interval) {
        this(interval.min, interval.max);
	}

    public boolean validate() {
        boolean valid = !(min > max);
        if (!valid) throw new KronosGeometryError(KronosErrorLibrary.INVALID_INTERVAL);
        return valid;
    }

    /**
     * Clamps the value to the interval.
     * 
     * @param value
     * @return
     */
	public float clamp(float value) {
		return Mathk.clamp(value, this.min, this.max);
	}

    public void union(Intervalf interval) {
		this.min = Math.min(interval.min, this.min);
		this.max = Math.max(interval.max, this.max);
	}

	public Intervalf intersection(Intervalf interval) {
		if (this.overlaps(interval))return new Intervalf(Math.max(interval.min, this.min), Math.min(interval.max, this.max));
		return new Intervalf(0, 0);
	}

    public float distance(Intervalf interval) {
		// make sure they arent overlapping
		if (!this.overlaps(interval)) {
			// the distance is calculated by taking the max of one - the min of the other
			// the interval whose max will be used is determined by the interval with the max
			// less than the other's min
			if (this.max < interval.min) return interval.min - this.max;
			else return this.min - interval.max;
		}
		// if they are overlapping then return 0
		return 0;
	}

	public Intervalf expand(float value) {
		float e = value * 0.5f;
		this.min -= e;
		this.max += e;

		// verify the interval is still valid
		if (value < 0f && this.min > this.max) {
			// if its not then set the min/max to
			// the middle value of their current values
			float p = (this.min + this.max) * 0.5f;
			this.min = p;
			this.max = p;
		}

        return this;
    }

	public boolean includesInclusive(float value) {
		return value <= this.max && value >= this.min;
	}

	public boolean includesExclusive(float value) {
		return value < this.max && value > this.min;
	}
	
	public boolean includesInclusiveMin(float value) {
		return value < this.max && value >= this.min;
	}
	
	public boolean includesInclusiveMax(float value) {
		return value <= this.max && value > this.min;
	}
	
	public boolean overlaps(Intervalf interval) {
		return !(this.min > interval.max || interval.min > this.max);
	}
	
	public float getOverlap(Intervalf interval) {
		if (this.overlaps(interval)) return Math.min(this.max, interval.max) - Math.max(this.min, interval.min);
		return 0;
	}

	public boolean containsExclusive(Intervalf interval) {
		return interval.min > this.min && interval.max < this.max;
	}

	public boolean containsInclusive(Intervalf interval) {
		return interval.min >= this.min && interval.max <= this.max;
	}

	public boolean containsInclusiveMax(Intervalf interval) {
		return interval.min > this.min && interval.max <= this.max;
	}

	public boolean containsInclusiveMin(Intervalf interval) {
		return interval.min >= this.min && interval.max < this.max;
	}
	
	public float getLength() {
		return this.max - this.min;
	}

    public float getMin() {
        return this.min;
    }

    public void setMin(float min) {
		if (min > this.max) throw new KronosGeometryError(KronosErrorLibrary.INVALID_INTERVAL);
		this.min = min;
	}
	
    public float getMax() {
        return this.max;
    }

	public void setMax(float max) {
		if (max < this.min) throw new KronosGeometryError(KronosErrorLibrary.INVALID_INTERVAL);
		this.max = max;
	}

    public boolean isDegenerate() {
		return this.min == this.max;
	}
	
	public boolean isDegenerate(float error) {
		return Math.abs(this.max - this.min) <= error;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Interval [max=");
        builder.append(max);
        builder.append(", min=");
        builder.append(min);
        builder.append("]");
        return builder.toString();
    }

}