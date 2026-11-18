/*
 * Copyright © 2021 L2J Discord Bot
 *
 * This file is part of L2J Discord Bot.
 *
 * L2J Discord Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Discord Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.bot.util;

import java.time.OffsetDateTime;

/**
 * Utilities
 * @author Stalitsa
 * @version 1.0
 */
public class Util {

	public static String getDateAndTimestamps(OffsetDateTime dateTime) {
		int year = dateTime.getYear();
		int month = dateTime.getMonth().getValue();
		int dayOfMonth = dateTime.getDayOfMonth();
		String dayOfWeek = dateTime.getDayOfWeek().toString();
		int hour = dateTime.getHour();
		int minutes = dateTime.getMinute();
		int seconds = dateTime.getSecond();
		return dayOfWeek + " - " + dayOfMonth + "." + month + "." + year + " " + "| " + String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + " GMT+0";
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String repeatN(int number) {
		StringBuilder result = new StringBuilder();
		for (int i = number; number > 0; number--) {
			result.append("\uD83D\uDEA5");
		}
		return result.toString();
	}
}
