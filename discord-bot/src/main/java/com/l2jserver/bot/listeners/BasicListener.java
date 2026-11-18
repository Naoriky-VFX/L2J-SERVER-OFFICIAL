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
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import static com.l2jserver.bot.config.Configuration.discord;

/**
 * BasicListener
 * @author Stalitsa, Mayhem
 * @version 1.0
 */
public class BasicListener extends ListenerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(BasicListener.class);
	public static Map<String, Message> messages = new HashMap<>();
	private static final long welcomeDeleteDelay = discord().getWelcomeDeletionDelay();

	@Override
	public void onReady(ReadyEvent event) {
		messages.clear();
		LOG.info("Joined Guilds - " + event.getGuildTotalCount());
		LOG.info("WebSocket Ping - " + event.getJDA().getGatewayPing());
	}
	
	@Override
	public void onDisconnect(DisconnectEvent event) {
		if (event.isClosedByServer()) {
			LOG.info(event.getJDA().getSelfUser().getName() + " disconnected (closed by the server) with code: " + event.getCloseCode());
		}
	}
	
	@Override
	public void onReconnected(ReconnectedEvent event) {
		LOG.info(event.getJDA().getSelfUser().getName() + " has reconnected.");
	}

	@Override
	public void onShutdown(ShutdownEvent event) {
		event.getJDA().getRegisteredListeners().clear();
		LOG.info(event.getJDA().getSelfUser().getName() + " has Shut Down.");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String args = event.getMessage().getContentRaw();
		TextChannel logsChannel = event.getJDA().getTextChannelById(discord().getLogsChannel());
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED);
		MessageBuilder msgBuilder = new MessageBuilder().setContent(event.getAuthor().getAsMention());

		if (event.getAuthor().isBot() || event.getChannelType().equals(ChannelType.PRIVATE)) {
			return;
		}

		messages.put(event.getMessageId(), event.getMessage());
		//Check if message is advert or NSFW, post it in log channel and then delete the original one.
		if (discord().getForbiddenWords().stream().anyMatch(args::contains)) {
			if (logsChannel != null) {
				EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED);
				embedBuilder.setDescription("***___Received Message___***");
				embedBuilder.setAuthor(event.getAuthor().getAsTag() + "  (" + event.getAuthor().getId() + ")", null, event.getAuthor().getEffectiveAvatarUrl());
				embedBuilder.addField("Message", "```css\n" + event.getMessage().getContentRaw() + " \n```", false);
				embedBuilder.addField("Channel", event.getTextChannel().getAsMention() + "`(" + event.getTextChannel().getId() + ")`", true);
				embedBuilder.setThumbnail(event.getGuild().getIconUrl());
				logsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
			}
			event.getMessage().delete().queueAfter(10, TimeUnit.MILLISECONDS);
			eb.setDescription("Please stop posting inappropriate content.");
			event.getTextChannel().sendMessage(msgBuilder.setEmbeds(eb.build()).build()).queue(message ->
				message.delete().queueAfter(10, TimeUnit.SECONDS));
			LOG.warn("Member : " + event.getMember() + " - Message Received : " + args);
		}
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		String args = event.getMessage().getContentRaw();
		TextChannel logsChannel = event.getJDA().getTextChannelById(discord().getLogsChannel());
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED);
		MessageBuilder msgBuilder = new MessageBuilder().setContent(event.getAuthor().getAsMention());

		if (event.getAuthor().isBot() || event.getChannelType().equals(ChannelType.PRIVATE)) {
			return;
		}

		//Check if message is advert or NSFW, post it in log channel and then delete the original one.
		if (discord().getForbiddenWords().stream().anyMatch(args::contains)) {
			if (logsChannel != null) {
				EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED);
				embedBuilder.setDescription("***___Updated Message___***");
				embedBuilder.setAuthor(event.getAuthor().getAsTag() + "  (" + event.getAuthor().getId() + ")", null, event.getAuthor().getEffectiveAvatarUrl());
				embedBuilder.addField("Message", "```css\n" + event.getMessage().getContentRaw() + " \n```", false);
				embedBuilder.addField("Channel", event.getTextChannel().getAsMention() + "`(" + event.getTextChannel().getId() + ")`", true);
				embedBuilder.setThumbnail(event.getGuild().getIconUrl());
				logsChannel.sendMessageEmbeds(embedBuilder.build()).queue();
			}
			event.getMessage().delete().queueAfter(10, TimeUnit.MILLISECONDS);
			eb.setDescription("Please stop posting inappropriate content.");
			event.getTextChannel().sendMessage(msgBuilder.setEmbeds(eb.build()).build()).queue(message ->
				message.delete().queueAfter(10, TimeUnit.SECONDS));
			LOG.warn("Member : " + event.getMember() + " - Message Update : " + args);
		}
	}

	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		TextChannel logsChannel = event.getJDA().getTextChannelById(discord().getLogsChannel());
		TextChannel autoSupportChannel = event.getJDA().getTextChannelById(discord().getAutoSupportChannel());
		MessageBuilder messageBuilder = new MessageBuilder();
		Message message = messages.get(event.getMessageId());
		if ((event.getTextChannel() == autoSupportChannel)  || (message == null) || (logsChannel == null)) {
			return;
		}
		EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED);
		embedBuilder.setDescription("***___Deleted Message___***");
		embedBuilder.setAuthor(message.getAuthor().getAsTag() + "  (" + message.getAuthor().getId() + ")", null, message.getAuthor().getEffectiveAvatarUrl());
		if (message.getContentRaw().length() <= 1024) {
			embedBuilder.addField("Message", "```css\n" + message.getContentRaw() + " \n```", false);
		}
		else {
			messageBuilder.setContent("Message: " + message.getContentRaw());
		}
		embedBuilder.addField("Channel", event.getTextChannel().getAsMention() + "`(" + event.getTextChannel().getId() + ")`", true);
		embedBuilder.setThumbnail(event.getGuild().getIconUrl());
		logsChannel.sendMessage(messageBuilder.setEmbeds(embedBuilder.build()).build()).queue();
		messages.remove(event.getMessageId());
		LOG.warn(message.getMember() + " - Message Deleted: " + message.getContentRaw());
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Guild gld = event.getJDA().getGuildById(discord().getServer());
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED);
		TextChannel welcomeChannel = event.getJDA().getTextChannelById(discord().getWelcomeChannel());
		TextChannel autoSupportChannel = event.getJDA().getTextChannelById(discord().getAutoSupportChannel());
		TextChannel rulesChannel = event.getJDA().getTextChannelById(discord().getRulesChannel());
		TextChannel announceChannel = event.getJDA().getTextChannelById(discord().getAnnounceChannel());
		TextChannel optionalChannelOne = event.getJDA().getTextChannelById(discord().getOptionalChannelOne());
		TextChannel optionalChannelTwo = event.getJDA().getTextChannelById(discord().getOptionalChannelTwo());
		TextChannel optionalChannelThree = event.getJDA().getTextChannelById(discord().getOptionalChannelThree());

		if (event.getMember().getUser().isBot() || (event.getGuild() != gld) || (gld.getMember(event.getUser()) == null)) {
			return;
		}

		// If auto role is enabled then the bot will assign the role to all new users that join the server.
		if ((discord().AutoRoleEnable()) && (!discord().getAutoRoleId().isEmpty())) {
			Role autoRole = gld.getRoleById(discord().getAutoRoleId());
			if (autoRole != null) {
				// Assign the role.
				final AuditableRestAction<Void> voidAuditableRestAction = gld.addRoleToMember(event.getUser().getId(), autoRole);
				voidAuditableRestAction.queue();
				LOG.info("Member : " + event.getUser().getName() + " - Role given : " + autoRole.getName());
			}
			else {
				LOG.warn("AutoRole: AutoRole is enabled but the role id looks like is not set in configs.");
			}
		}

		//If the welcome message is enabled then the bot will post a message to welcome the new member.
		if (discord().WelcomeEnable()) {
			//All the channel ids in the configuration must be correct in order to post the welcome message.
			if ((welcomeChannel == null) || (autoSupportChannel == null) || (rulesChannel == null) || (announceChannel == null)
				|| (optionalChannelOne == null)  || (optionalChannelTwo == null)  || (optionalChannelThree == null)) {
				LOG.warn("Welcome: Welcome message is enabled but there are some channel ids that is not set correctly in configs.");
				return;
			}
			StringBuilder welcomeMessage = new StringBuilder();
			welcomeMessage.append(event.getMember().getAsMention() + " Hello. You have arrived at  " + gld.getName() + " Discord \n ");
			welcomeMessage.append(" \n");
			welcomeMessage.append(" My name is " + event.getJDA().getSelfUser().getName() + ". And i'm here to provide assistance \n");
			welcomeMessage.append(" If you wish to receive automated support, please use the " + autoSupportChannel.getAsMention() + " channel.\n ");

			welcomeMessage.append(" \n");
			welcomeMessage.append(" If you require human assistance, please consider reading the " + rulesChannel.getAsMention() +" first \n ");
			welcomeMessage.append(" After that, you can move to one of the designated sections: \n");
			welcomeMessage.append(optionalChannelOne.getAsMention() + " for the HighFive branch\n");
			welcomeMessage.append(optionalChannelTwo.getAsMention() + " for the Interlude branch\n");
			welcomeMessage.append(" or " + optionalChannelThree.getAsMention());
			welcomeMessage.append(" \n");
			welcomeMessage.append(" Please dont forget that we have video guides in English and Spanish located here " + announceChannel.getAsMention() + " \n");
			welcomeMessage.append(" Also, for your convenience, i will leave a list of links to all the resources, that you may need \n");
			eb.setAuthor(event.getUser().getName(), null, event.getUser().getEffectiveAvatarUrl());
			eb.appendDescription(" \n" );
			eb.addField("Web Site", "[Link](https://www.l2jserver.com/)", true);
			eb.addField("Forum", "[Link](https://www.l2jserver.com/forum/)", true);
			eb.addField("Discord", "[Link](https://discord.com/invite/HyE2VfY)", true);
			eb.addField("Trello", "[Link](https://trello.com/b/qjLoH966/l2j)", true);
			eb.addField("Twitter", "[Link](https://twitter.com/l2jserver)", true);
			eb.addField("Source code", "[Link](https://bitbucket.org/l2jserver/)", true);
			eb.addBlankField(false);
			eb.setImage("https://cdn.discordapp.com/attachments/812428354980020234/858727082804117514/l2jserver.png");
			eb.setFooter("User Account Creation Date: " + Util.getDateAndTimestamps(event.getUser().getTimeCreated()), event.getUser().getEffectiveAvatarUrl());
			MessageBuilder welcomeBuilder = new MessageBuilder().setContent(welcomeMessage.toString());
			welcomeChannel.sendMessage(welcomeBuilder.setEmbeds(eb.build()).build()).queue(message ->
				message.delete().queueAfter(welcomeDeleteDelay, TimeUnit.MINUTES)); //The welcome message will be deleted after <welcomeDeleteDelay> minutes.
		}
	}
}
