package com.starworks.kronos.input.action;

import com.starworks.kronos.event.Event;

/**
 * Below is a basic implementation example:
 * <pre>
 * enum PlayerActions implements ActionMap {
 * 	MOVE(ActionType.AXIS_2D, Input.KR_KEY_W, Input.KR_KEY_S, Input.KR_KEY_A, Input.KR_KEY_D),
 *	TILT(ActionType.AXIS_1D, Input.KR_KEY_Q, Input.KR_KEY_E),
 *	JUMP(ActionType.BINDING, Input.KR_KEY_SPACE);
 *	
 *	private ActionType m_type;
 *	private int[] m_keyCodes;
 *	
 *	PlayerActions(ActionType type, int... keyCodes) {
 *		this.m_type = type;
 *		this.m_keyCodes = keyCodes;
 *	}
 *	
 *	public ActionType getType() {
 *		return m_type;
 *	}
 *
 *	public ActionProcessor getProcessor() {
 *		return null;
 *	}
 *	
 *	public int[] getKeyCodes() {
 *		return m_keyCodes;
 *	}
 *	
 *	public Class<? extends Event> getEventType() {
 *		return Event.KeyPressed.class;
 *	}
 * }
 *	</pre>
 * @return
 */

public interface ActionMap {
	
    public ActionType getType();
    
    public ActionProcessor getProcessor();
    
    public int[] getBindings();
    
    public Class<? extends Event> getEventType();
}
