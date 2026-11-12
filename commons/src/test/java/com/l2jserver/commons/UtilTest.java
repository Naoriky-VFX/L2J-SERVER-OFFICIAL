/*
 * Copyright © 2004-2026 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.l2jserver.commons.util.Util;

/**
 * Util test.
 * @author Zoey76
 * @version 2.6.5.0
 */
public class UtilTest {
	
	private static final String IGNORE_QUESTS = "-noquest";
	
	private static final String DP = "-dp";
	
	private static final String DP_PATH = "../../../L2J_DataPack/dist/game";
	
	@ParameterizedTest
	@MethodSource("provideArgs")
	public void testParseArg(String[] args, String arg, boolean hasArgValue, String expected) {
		assertEquals(expected, Util.parseArg(args, arg, hasArgValue));
	}
	
	@ParameterizedTest
	@MethodSource("provideArgsFail")
	public void testParseArgFail(String[] args, String arg, boolean hasArgValue) {
		assertThrows(IllegalArgumentException.class, () -> Util.parseArg(args, arg, hasArgValue));
	}
	
	private static Object[][] provideArgs() {
		// @formatter:off
		return new Object[][] {
			{ null, null, false, null },
			{ new String[] {}, IGNORE_QUESTS, false, null },
			{ new String[] { IGNORE_QUESTS }, null, false, null },
			{ new String[] { IGNORE_QUESTS }, "", false, null },
			{ new String[] { DP, DP_PATH }, DP, true, DP_PATH },
			{ new String[] { IGNORE_QUESTS }, IGNORE_QUESTS, false, IGNORE_QUESTS },
			{ new String[] { IGNORE_QUESTS, DP, DP_PATH }, DP, true, DP_PATH }
		};
		// @formatter:on
	}
	
	private static Object[][] provideArgsFail() {
		// @formatter:off
		return new Object[][] {
			{ new String[] { IGNORE_QUESTS }, IGNORE_QUESTS, true }
		};
		// @formatter:on
	}
}
