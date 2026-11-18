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

import com.l2jserver.bot.listeners.AutoSupportListener;
import com.l2jserver.bot.listeners.BasicListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import static com.l2jserver.bot.config.Configuration.discord;

/**
 * Main class of Discord Bot.
 * @author Stalitsa
 * @version 1.0
 */
public class DiscordBot {
	
	private static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);

	private static final Object[] COMMANDS = {
		new BasicListener(),
		new AutoSupportListener(),
	};
	
	public static void main(String[] args) {
		try {
			JDA jda = JDABuilder.createDefault(discord().getBotToken()) //
				.setAutoReconnect(true) //
				.addEventListeners(COMMANDS) //
				.enableIntents(GatewayIntent.GUILD_MEMBERS) //
				.enableIntents(GatewayIntent.GUILD_MESSAGES) //
				.setMemberCachePolicy(MemberCachePolicy.ALL) //
				.setChunkingFilter(ChunkingFilter.ALL) //
				// Login to Discord now that we are all setup.
				.build() //
				.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
			String guildName = jda.getGuildById(discord().getServer()).getName();
			jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening(": -- " + guildName));
			LOG.info("Discord Bot Started.");
		} catch (InterruptedException | LoginException ex) {
			LOG.error("Failed to start the Discord Bot!", ex);
		}
	}
}
