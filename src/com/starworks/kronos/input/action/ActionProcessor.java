package com.starworks.kronos.input.action;

import com.starworks.kronos.maths.Vector2f;

public abstract sealed class ActionProcessor permits ActionProcessor.InvertAxis1D, ActionProcessor.InvertAxis2D {

	private ActionProcessor() {
	}

	public abstract Object process(Object value);

	public static InvertAxis1D invertAxis1D() {
		return new InvertAxis1D();
	}

	public static InvertAxis2D invertAxis2D() {
		return new InvertAxis2D();
	}

	public static final class InvertAxis1D extends ActionProcessor {

		private static InvertAxis1D s_instance = null;
		
		private InvertAxis1D() {
		}

		@Override
		public Object process(Object value) {
			if (!(value instanceof Float)) {
				return null;
			}
			float number = (float) value;
			return -number;
		}
		
		public InvertAxis1D get() {
			if (s_instance == null) {
				synchronized (InvertAxis1D.class) {
					if (s_instance == null) {
						s_instance = new InvertAxis1D();
					}
				}
			}
			return s_instance;
		}
	}

	public static final class InvertAxis2D extends ActionProcessor {

		private static InvertAxis2D s_instance = null;

		private boolean m_invertX;
		private boolean m_invertY;

		private InvertAxis2D() {
			this.m_invertX = true;
			this.m_invertY = true;
		}

		@Override
		public Object process(Object value) {
			if (!(value instanceof Vector2f)) {
				return null;
			}
			Vector2f vector = (Vector2f) value;
			float x = vector.x;
			float y = vector.y;
			vector.set(m_invertX ? -x : x, m_invertY ? -y : y);
			return vector;
		}

		public InvertAxis2D invertX(boolean invert) {
			m_invertX = invert;
			return this;
		}

		public InvertAxis2D invertY(boolean invert) {
			m_invertY = invert;
			return this;
		}

		public InvertAxis2D get() {
			if (s_instance == null) {
				synchronized (InvertAxis2D.class) {
					if (s_instance == null) {
						s_instance = new InvertAxis2D();
					}
				}
			}
			return s_instance;
		}
	}
}
