package com.starworks.kronos.input.action;

import com.starworks.kronos.maths.Mathk;
import com.starworks.kronos.maths.Vector2f;

public abstract class ActionProcessor {

	private ActionProcessor() {
	}

	public abstract Object process(Object value);

	public static Clamp clamp() {
		return Clamp.s_instance;
	}
	
	public static InvertAxis1F invertAxis1F() {
		return InvertAxis1F.s_instance;
	}

	public static InvertAxis2F invertAxis2F() {
		return InvertAxis2F.s_instance;
	}

	public static Normalize normalize() {
		return Normalize.s_instance;
	}

	public static Normalize2F normalize2F() {
		return Normalize2F.s_instance;
	}
	
	public static Scale scale() {
		return Scale.s_instance;
	}
	
	public static Scale2F scale2F() {
		return Scale2F.s_instance;
	}

	public static final class Clamp extends ActionProcessor {

		private static final Clamp s_instance = new Clamp();
		
		private float m_min;
		private float m_max;

		public Clamp() {
			this.m_min = 0f;
			this.m_max = 1f;
		}
		
		@Override
		public Object process(Object value) {
			if (value instanceof Float) {
				float number = (float) value;
				return Mathk.clamp(m_min, m_max, number);
			}
			if (value instanceof Vector2f) {
				Vector2f vector = (Vector2f) value;
				vector.x = Mathk.clamp(m_min, m_max, vector.x);
				vector.y = Mathk.clamp(m_min, m_max, vector.y);
				return vector;
			}
			return null;
		}
		
		public Clamp setMin(float min) {
			if (min >= m_max) {
				return this;
			}
			m_min = min;
			return this;
		}

		public Clamp setMax(float max) {
			if (max <= m_min) {
				return this;
			}
			m_max = max;
			return this;
		}
	}
	
	public static final class InvertAxis1F extends ActionProcessor {

		private static final InvertAxis1F s_instance = new InvertAxis1F();

		private InvertAxis1F() {
		}

		@Override
		public Object process(Object value) {
			if (!(value instanceof Float)) {
				return null;
			}
			float number = (float) value;
			return -number;
		}
	}

	public static final class InvertAxis2F extends ActionProcessor {

		private static final InvertAxis2F s_instance = new InvertAxis2F();

		private boolean m_invertX;
		private boolean m_invertY;

		private InvertAxis2F() {
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

		public InvertAxis2F invertX(boolean invert) {
			m_invertX = invert;
			return this;
		}

		public InvertAxis2F invertY(boolean invert) {
			m_invertY = invert;
			return this;
		}
	}

	public static final class Normalize extends ActionProcessor {

		private static final Normalize s_instance = new Normalize();

		private float m_min;
		private float m_max;
		private float m_zero;

		public Normalize() {
			this.m_min = 0f;
			this.m_max = 1f;
			this.m_zero = 0f;
		}

		@Override
		public Object process(Object value) {
			if (value instanceof Float) {
				float number = (float) value;
				return normalize(number);
			}
			if (value instanceof Vector2f) {
				Vector2f vector = (Vector2f) value;
				vector.x = normalize(vector.x);
				vector.y = normalize(vector.y);
				return vector;
			}
			return null;
		}

		private float normalize(float value) {
			if (m_min >= m_zero) {
				// unsigned normalized form [0..1]
				return (value - m_min) / (m_max - m_min);
			} else {
				// signed normalized form [-1..1]
				if (value >= m_zero) {
					return (value - m_zero) / (m_max - m_zero);
				} else {
					return (value - m_zero) / (m_zero - m_min);
				}
			}
		}

		public Normalize setMin(float min) {
			if (min >= m_max) {
				return this;
			}
			m_min = min;
			return this;
		}

		public Normalize setMax(float max) {
			if (max <= m_min) {
				return this;
			}
			m_max = max;
			return this;
		}

		public Normalize setZero(float zero) {
			if (zero < m_min || zero > m_max) {
				return this;
			}
			m_zero = zero;
			return this;
		}
	}

	public static final class Normalize2F extends ActionProcessor {

		private static final Normalize2F s_instance = new Normalize2F();

		private Normalize2F() {
		}

		@Override
		public Object process(Object value) {
			if (!(value instanceof Vector2f)) {
				return null;
			}
			Vector2f vector = (Vector2f) value;
			return vector.normalize();
		}
	}

	public static final class Scale extends ActionProcessor {

		private static final Scale s_instance = new Scale();

		private float m_scale;
		
		private Scale() {
			this.m_scale = 1.0f;
		}

		@Override
		public Object process(Object value) {
			if (value instanceof Float) {
				float number = (float) value;
				return number * m_scale;
			}
			if (value instanceof Vector2f) {
				Vector2f vector = (Vector2f) value;
				return vector.mul(m_scale);
			}
			return null;
		}
		
		public Scale setScale(float scale) {
			m_scale = scale;
			return this;
		}
	}
	
	public static final class Scale2F extends ActionProcessor {

		private static final Scale2F s_instance = new Scale2F();

		private float m_scaleX;
		private float m_scaleY;
		
		private Scale2F() {
			this.m_scaleX = 1.0f;
			this.m_scaleY = 1.0f;
		}

		@Override
		public Object process(Object value) {
			if (!(value instanceof Vector2f)) {
				return null;
			}
			Vector2f vector = (Vector2f) value;
			return vector.mul(m_scaleX, m_scaleY);
		}
		
		public Scale2F setScaleX(float scaleX) {
			m_scaleX = scaleX;
			return this;
		}
		
		public Scale2F setScaleY(float scaleY) {
			m_scaleY = scaleY;
			return this;
		}
	}
}
