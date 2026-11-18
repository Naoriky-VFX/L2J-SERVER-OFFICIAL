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
package com.l2jserver.bot.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Configuration.
 * @author Stalitsa
 * @version 1.0
 */
public class Configuration {

	private static final BotConfiguration discord = ConfigFactory.create(BotConfiguration.class);

	private Configuration() {
		//Do nothing.
	}

	public static BotConfiguration discord() {
		return discord;
	}
}
