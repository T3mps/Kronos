package com.starworks.kronos.toolkit.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public final class ArrivalGate {

	private final AtomicInteger m_count;
	private final int m_allowedIn;

	public ArrivalGate(final int allowedIn) {
		this.m_count = new AtomicInteger(0);
		this.m_allowedIn = allowedIn;
	}

	public void arrive() throws InterruptedException {
		for (;;) {
			int count = m_count.get();
			if (count >= m_allowedIn) {
				throw new InterruptedException("Cannot enter gate; gate is full.");
			}
			if (m_count.compareAndSet(count, count + 1)) {
				break;
			}
		}
	}

	public void depart() throws InterruptedException {
		for (;;) {
			int count = m_count.get();
			if (count == 0) {
				throw new InterruptedException("Cannot depart thread, for it has not entered the gate.");
			}
			if (m_count.compareAndSet(count, count - 1)) {
				break;
			}
		}
	}

	public boolean tryArrive() {
		boolean success = false;
		try {
			arrive();
			success = true;
		} catch (InterruptedException ignored) {
		}
		return success;
	}

	public boolean tryDepart() {
		boolean success = false;
		try {
			depart();
			success = true;
		} catch (InterruptedException ignored) {
		}
		return success;
	}

	public boolean isFull() {
		return m_count.get() == m_allowedIn;
	}

	public boolean isEmpty() {
		return m_count.get() == 0;
	}
}
