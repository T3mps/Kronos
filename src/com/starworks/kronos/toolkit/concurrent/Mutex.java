package com.starworks.kronos.toolkit.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class Mutex implements Lock {

	private final Sync m_sync;

	public Mutex() {
		this.m_sync = new Sync();
	}

	@Override
	public void lock() {
		m_sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		m_sync.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return m_sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
		return m_sync.tryAcquireNanos(1, unit.toNanos(timeout));
	}

	@Override
	public void unlock() {
		m_sync.release(1);
	}

	public boolean isLocked() {
		return m_sync.isHeldExclusively();
	}

	public boolean hasQueuedThreads() {
		return m_sync.hasQueuedThreads();
	}

	@Override
	public Condition newCondition() {
		return m_sync.newCondition();
	}

	private static class Sync extends AbstractQueuedSynchronizer {
		private static final long serialVersionUID = 1L;

		public Sync() {
		}

		@Override
		protected boolean isHeldExclusively() {
			return getExclusiveOwnerThread() == Thread.currentThread();
		}

		@Override
		public boolean tryAcquire(int acquires) {
			if (compareAndSetState(0, 1)) {
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}

		@Override
		protected boolean tryRelease(int releases) {
			if (getState() == 0) throw new IllegalMonitorStateException();
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}

		Condition newCondition() {
			return new ConditionObject();
		}
	}
}
