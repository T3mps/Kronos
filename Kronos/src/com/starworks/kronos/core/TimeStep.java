package com.starworks.kronos.core;

public class TimeStep {
    
    /** The last elapsed time */
	protected double dt0;
	
	/** The last inverse elapsed time */
	protected double invdt0;
	
	/** The elapsed time */
	protected double dt;
	
	/** The inverse elapsed time */
	protected double invdt;
	
	/** The elapsed time ratio from the last to the current */
	protected double dtRatio;

    public TimeStep(double dt) {
		if (dt <= 0.0) throw new IllegalArgumentException("dt must be greater than zero");
		this.dt = dt;
		this.invdt = 1.0 / dt;
		this.dt0 = this.dt;
		this.invdt0 = this.invdt;
		this.dtRatio = 1.0;
	}

    public void update(double dt) {
		if (dt <= 0.0) throw new IllegalArgumentException("dt must be greater than zero");
		this.dt0 = this.dt;
		this.invdt0 = this.invdt;
		this.dt = dt;
		this.invdt = 1.0 / dt;
		this.dtRatio = this.invdt0 * dt;
	}

    public double getDeltaTime() {
        return this.dt;
    }

    public double getInverseDeltaTime() {
        return this.invdt;
    }

    public double getDeltaTimeRatio() {
        return this.dtRatio;
    }

    public double getPreviousDeltaTime() {
        return this.dt0;
    }

    public double getPreviousInverseDeltaTime() {
        return this.invdt0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeStep[dt=").append(this.dt)
        .append("|invdt=").append(this.invdt)
        .append("|dtRatio=").append(this.dtRatio)
        .append("|dt0=").append(this.dt0)
        .append("|invdt0=").append(this.invdt0)
        .append("]");
        return sb.toString();
    }
}
