package com.starworks.kronos.core;

import com.starworks.kronos.exception.KronosRuntimeException;

public final class TimeStep {

	private double m_dt;
	private double m_invdt;
	private double m_dt0;
	private double m_invdt0;
	private double m_dtRatio;

	public TimeStep(double dt) {
		this.m_dt = dt;
		this.m_invdt = 1.0 / m_dt;
		this.m_dt0 = m_dt;
		this.m_invdt0 = m_invdt;
		this.m_dtRatio = 1.0;
	}

	public TimeStep update(double dt) {
		if (dt <= 0.0) throw new KronosRuntimeException("dt must be greater than zero");
		m_dt0 = m_dt;
		m_invdt0 = m_invdt;
		m_dt = dt;
		m_invdt = 1.0 / dt;
		m_dtRatio = m_invdt0 * dt;
		return this;
	}

	public TimeStep update(TimeStep timeStep) {
		return update(timeStep.getDeltaTime());
	}

	public double getDeltaTime() {
		return m_dt;
	}
	
	public double getInverseDeltaTime() {
		return m_invdt;
	}

	public double getPreviousDeltaTime() {
		return m_dt0;
	}

	public double getPreviousInverseDeltaTime() {
		return m_invdt0;
	}
	
	public double getRatio() {
		return m_dtRatio;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeStep [dt=");
		builder.append(m_dt);
		builder.append(", invdt=");
		builder.append(m_invdt);
		builder.append(", dt0=");
		builder.append(m_dt0);
		builder.append(", invdt0=");
		builder.append(m_invdt0);
		builder.append(", dtRatio=");
		builder.append(m_dtRatio);
		builder.append("]");
		return builder.toString();
	}
}
