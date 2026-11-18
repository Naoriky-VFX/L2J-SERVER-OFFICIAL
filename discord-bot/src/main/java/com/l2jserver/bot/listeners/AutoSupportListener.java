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
package com.l2jserver.bot.listeners;

import com.l2jserver.bot.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.l2jserver.bot.config.Configuration.discord;

/**
 * Auto Support.
 * @author Stalitsa, Mayhem
 * @version 1.0
 */
public class AutoSupportListener extends ListenerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(AutoSupportListener.class);
	private static final Map<Integer, String> questions = new HashMap<>();
	private static final Map<Integer, String> answers = new HashMap<>();
	private static final String QUESTION_NOT_FOUND = "Question not found";
	private static final String numbersMessage = "```diff\n-Please type only the number of the question.```";
	private static final long userDeleteDelay = discord().getUserDeletionDelay();
	private static final long botDeleteDelay = discord().getBotDeletionDelay();

	@Override
	public void onReady(ReadyEvent event) {
		loadAutoSupport();
		printAutoSupport(event);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		if (event.getTextChannel().getId().equals(discord().getAutoSupportChannel()) && !event.getAuthor().isBot()) {
			String[] args = event.getMessage().getContentRaw().split(" ");
			EmbedBuilder response = new EmbedBuilder().setColor(Color.GREEN);
			Guild guild = event.getJDA().getGuildById(discord().getServer());
			TextChannel supportChannel = event.getJDA().getTextChannelById(discord().getAutoSupportChannel());
			Member guildMember = guild != null ? guild.getMember(event.getMessage().getAuthor()) : null;

			if ((guildMember == null) || (supportChannel == null) || (event.getTextChannel() != supportChannel)) {
				return;
			}

			if (!Util.isInt(args[0]) || args.length > 1) {
				event.getMessage().delete().queueAfter(userDeleteDelay, TimeUnit.SECONDS);
				event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + numbersMessage).queue(message ->
					message.delete().queueAfter(userDeleteDelay, TimeUnit.SECONDS));
				return;
			}

			int number = Integer.parseInt(args[0]);
			response.setAuthor("Question", null, null);
			response.setDescription(getQuestionFor(number));
			if (answers.containsKey(number)) {
				response.addField("Answer", getAnswerFor(number), false);
			}
			event.getMessage().delete().queueAfter(userDeleteDelay, TimeUnit.SECONDS);
			//only for question number 9 we will ignore the saved answer above and respond with a beautiful embed with links.
			event.getTextChannel().sendMessageEmbeds((number == 9) ? getLinks(event).build() : response.build()).queue(message ->
				message.delete().queueAfter(answers.containsKey(number) ? botDeleteDelay : userDeleteDelay, TimeUnit.SECONDS));
		}
	}

	public static void printAutoSupport(ReadyEvent event) {
		EmbedBuilder response = new EmbedBuilder().setColor(Color.GREEN);
		Guild guild = event.getJDA().getGuildById(discord().getServer());
		MessageChannel supportChannel = event.getJDA().getTextChannelById(discord().getAutoSupportChannel());
		if ((guild != null) && (supportChannel != null)) {
			supportChannel.purgeMessages(supportChannel.getIterableHistory().complete());
			String welcome = "Welcome to " + guild.getName() + " Auto Support \n";
			response.setDescription(numbersMessage);
			response.setAuthor(welcome, null, guild.getIconUrl());
			response.addField("Questions" + "\n" +  Util.repeatN(20),String.join("\n", questions.values()) + "\n" + Util.repeatN(20), false);
			supportChannel.sendMessageEmbeds(response.build()).queue();
		}
	}

	/**
	 * Load Questions and Answers from a remote storage.
	 */
	public static void loadAutoSupport() {
		try {
			URL url = new URL("https://bitbucket.org/l2jserver/l2j-server-discord-bot/raw/develop/AutoSupport.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			List<String> lines = in.lines().sorted().collect(Collectors.toList());
			for (String line : lines) {
				if (line.startsWith("Q")) {
					final int key = Integer.parseInt(line.substring(1, 4).trim());
					questions.put(key, line.substring(4));
				}
				if (line.startsWith("A")) {
					final int key = Integer.parseInt(line.substring(1, 4).trim());
					answers.put(key, line.substring(4));
				}
			}
			in.close();
			LOG.info("Auto Support channel Questions and Answers are loaded.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static EmbedBuilder getLinks(MessageReceivedEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED);
		eb.appendDescription("***___LINKS INFO___***\n" + "```http\n " + guild.getName() + " Official Links\n```");
		eb.addField("Web Site", "[Click here](https://www.l2jserver.com/)", true);
		eb.addField("Forum", "[Click here](https://www.l2jserver.com/forum/)", true);
		eb.addField("Repository", "[Click here](https://bitbucket.org/l2jserver/)", true);
		eb.addField("Discord", "[Click here](https://discord.com/invite/HyE2VfY)", true);
		eb.addField("Trello", "[Click here](https://trello.com/b/qjLoH966/l2j)", true);
		eb.addField("Twitter", "[Click here](https://twitter.com/l2jserver)", true);
		eb.addBlankField(false);
		eb.setImage("https://cdn.discordapp.com/attachments/812428354980020234/858727082804117514/l2jserver.png");
		eb.setFooter("Discord Server Creation Date: " + Util.getDateAndTimestamps(guild.getTimeCreated()), guild.getIconUrl());
		return eb;
	}

	private static String getQuestionFor(int number) {
		if (questions.containsKey(number)) {
			return questions.get(number);
		}
		return QUESTION_NOT_FOUND;
	}

	private static String getAnswerFor(int number) {
		return answers.get(number);
	}
}
