package com.starworks.kronos.logging;

import com.starworks.kronos.exception.Exceptions;
import com.starworks.kronos.toolkit.Ansi;
import com.starworks.kronos.toolkit.Ansi.AnsiTrait;
import com.starworks.kronos.toolkit.Ansi.ComplexColor;

public final class Level {

	public static final byte OFF_FLAG     	= 0b000000;
	public static final byte TRACE_FLAG   	= 0b000001;
	public static final byte DEBUG_FLAG   	= 0b000010;
	public static final byte INFO_FLAG    	= 0b000100;
	public static final byte WARN_FLAG    	= 0b001000;
	public static final byte ERROR_FLAG   	= 0b010000;
	public static final byte FATAL_FLAG   	= 0b100000;
	public static final byte ALL_FLAG     	= 0b111111;

	public static final String OFF_NAME   	= "OFF";
	public static final String TRACE_NAME 	= "TRACE";
	public static final String DEBUG_NAME 	= "DEBUG";
	public static final String INFO_NAME  	= "INFO";
	public static final String WARN_NAME    = "WARN";
	public static final String ERROR_NAME   = "ERROR";
	public static final String FATAL_NAME   = "FATAL";
	public static final String ALL_NAME     = "ALL";
	public static final String RELEASE_NAME = "RELEASE";

	public static final Level OFF   	  	= new Level(OFF_FLAG,   OFF_NAME,   Ansi.Traits.BLACK_FG, Ansi.Traits.BLACK_BG);
	public static final Level TRACE 	  	= new Level(TRACE_FLAG, TRACE_NAME, ComplexColor.FG_FROM_RGB(184, 109, 219));
	public static final Level DEBUG 	  	= new Level(DEBUG_FLAG, DEBUG_NAME, ComplexColor.FG_FROM_RGB(54, 191, 105));
	public static final Level INFO  	  	= new Level(INFO_FLAG,  INFO_NAME,  ComplexColor.FG_FROM_RGB(97, 148, 255));
	public static final Level WARN  	  	= new Level(WARN_FLAG,  WARN_NAME,  ComplexColor.FG_FROM_RGB(255, 216, 0));
	public static final Level ERROR 	  	= new Level(ERROR_FLAG, ERROR_NAME, ComplexColor.FG_FROM_RGB(255, 58, 58));
	public static final Level FATAL 	  	= new Level(FATAL_FLAG, FATAL_NAME, ComplexColor.FG_FROM_RGB(210, 210, 210), ComplexColor.BG_FROM_RGB(178, 33, 33));
	public static final Level ALL   	  	= new Level(ALL_FLAG,   ALL_NAME,   ComplexColor.FG_FROM_RGB(175, 175, 175));

	public static final Level RELEASE     	= ALL.exclude(TRACE, DEBUG);
	
	private static final AnsiTrait s_combinedColor = ComplexColor.FG_FROM_RGB(199, 107, 152);
	
	private final byte m_flag;
	private final String m_name;
	private final AnsiTrait[] m_traits;

	private Level(int level, String name) {
		this(level, name, s_combinedColor);
	}
	
	private Level(int level, String name, AnsiTrait... traits) {
		this.m_flag = (byte) level;
		this.m_name = name;
		this.m_traits = traits;
	}

	public static Level of(String level) {
		return switch (level.toUpperCase()) {
			case OFF_NAME     -> OFF;
			case TRACE_NAME   -> TRACE;
			case DEBUG_NAME   -> DEBUG;
			case INFO_NAME    -> INFO;
			case WARN_NAME    -> WARN;
			case ERROR_NAME   -> ERROR;
			case FATAL_NAME   -> FATAL;
			case ALL_NAME     -> ALL;
			case RELEASE_NAME -> RELEASE;
			default 	      -> throw new IllegalArgumentException(Exceptions.getMessage("logging.level.invalidLevelString"));
		};
	}
	
	public static Level of(int level) {
		Level[] levels = values();
		if (level >= levels.length) {
			throw new IllegalArgumentException(Exceptions.getMessage("logging.level.invalidLevelInteger"));
		}
		return levels[level];
	}

	public Level include(Level level) {
		return new Level(m_flag | level.m_flag, m_name + "|" + level.m_name);
	}

    public Level include(Level... levels) {
        return switch (levels.length) {
            case 1  -> new Level(m_flag | levels[0].m_flag,
            		m_name + "|" + levels[0].m_name);
            case 2  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name);
            case 3  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name);
            case 4  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name);
            case 5  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag | levels[4].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name + "|" + levels[4].m_name);
            case 6  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag | levels[4].m_flag | levels[5].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name + "|" + levels[4].m_name + "|" + levels[5].m_name);
            case 7  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag | levels[4].m_flag | levels[5].m_flag | levels[6].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name + "|" + levels[4].m_name + "|" + levels[5].m_name + "|" + levels[6].m_name);
            case 8  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag | levels[4].m_flag | levels[5].m_flag | levels[6].m_flag | levels[7].m_flag,
            		m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name + "|" + levels[4].m_name + "|" + levels[5].m_name + "|" + levels[6].m_name + "|" + levels[7].m_name);
            case 9  -> new Level(m_flag | levels[0].m_flag | levels[1].m_flag | levels[2].m_flag | levels[3].m_flag | levels[4].m_flag | levels[5].m_flag | levels[6].m_flag | levels[7].m_flag | levels[8].m_flag,
					m_name + "|" + levels[0].m_name + "|" + levels[1].m_name + "|" + levels[2].m_name + "|" + levels[3].m_name + "|" + levels[4].m_name + "|" + levels[5].m_name + "|" + levels[6].m_name + "|" + levels[7].m_name + "|" + levels[8].m_name);
			default -> throw new IllegalArgumentException(Exceptions.getMessage("logging.level.invalidLevelCount"));
        };
    }

    public Level exclude(Level level) {
        return new Level(m_flag & ~level.m_flag, m_name + "&~" + level.m_name);
    }

    public Level exclude(Level... levels) {
        return switch (levels.length) {
            case 1  -> new Level(m_flag & ~levels[0].m_flag,
            		m_name + "&~" + levels[0].m_name);
            case 2  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name);
            case 3  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name);
            case 4  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name);
            case 5  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag & ~levels[4].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name + "&~" + levels[4].m_name);
            case 6  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag & ~levels[4].m_flag & ~levels[5].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name + "&~" + levels[4].m_name + "&~" + levels[5].m_name);
            case 7  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag & ~levels[4].m_flag & ~levels[5].m_flag & ~levels[6].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name + "&~" + levels[4].m_name + "&~" + levels[5].m_name + "&~" + levels[6].m_name);
            case 8  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag & ~levels[4].m_flag & ~levels[5].m_flag & ~levels[6].m_flag & ~levels[7].m_flag,
            		m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name + "&~" + levels[4].m_name + "&~" + levels[5].m_name + "&~" + levels[6].m_name + "&~" + levels[7].m_name);
            case 9  -> new Level(m_flag & ~levels[0].m_flag & ~levels[1].m_flag & ~levels[2].m_flag & ~levels[3].m_flag & ~levels[4].m_flag & ~levels[5].m_flag & ~levels[6].m_flag & ~levels[7].m_flag & ~levels[8].m_flag,
					m_name + "&~" + levels[0].m_name + "&~" + levels[1].m_name + "&~" + levels[2].m_name + "&~" + levels[3].m_name + "&~" + levels[4].m_name + "&~" + levels[5].m_name + "&~" + levels[6].m_name + "&~" + levels[7].m_name + "&~" + levels[8].m_name);
            default -> throw new IllegalArgumentException(Exceptions.getMessage("logging.level.invalidLevelCount"));
        };
    }

	public boolean allows(Level level) {
		return (m_flag & level.m_flag) != 0;
	}
	
	public byte getFlag() {
		return m_flag;
	}

	public String getName() {
		return m_name;
	}
	
	public AnsiTrait[] getTraits() {
		return m_traits;
	}

	public static Level[] values() {
		return new Level[] {
			OFF,
			TRACE,
			DEBUG,
			INFO,
			WARN,
			ERROR,
			FATAL,
			ALL,
			RELEASE
		};
	}
	
	@Override
	public String toString() {
		return m_name;
	}
}
