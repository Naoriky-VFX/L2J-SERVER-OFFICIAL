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
package com.l2jserver.bot;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.l2jserver.bot.config.Configuration.discord;

/**
 * AbstractCommand.
 * @author Stalitsa
 * @version 1.0
 */
public abstract class AbstractCommand extends ListenerAdapter {

	public static long commandsCount = 0;

	public abstract List<String> getCommands();
	
	public abstract void executeCommand(MessageReceivedEvent event, String[] args, String prefix);
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getChannelType().equals(ChannelType.PRIVATE)) {
			return;
		}
		String[] args = event.getMessage().getContentRaw().split(" ");
		if (isCommand(args, discord().getPrefix())) {
			executeCommand(event, args, discord().getPrefix());
			commandsCount++;
		}
	}

	private boolean isCommand(String[] args, String prefix) {
		List<String> commands = new ArrayList<>();
		for (String cmd : getCommands()) {
			commands.add(prefix + cmd);
		}
		return commands.contains(args[0]);
	}
}
