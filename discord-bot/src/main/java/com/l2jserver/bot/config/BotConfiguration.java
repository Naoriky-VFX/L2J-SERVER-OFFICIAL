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

import org.aeonbits.owner.Config.HotReload;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.aeonbits.owner.Config.HotReloadType.ASYNC;
import static org.aeonbits.owner.Config.LoadType.MERGE;

/**
 * DiscordConfiguration.
 * @author Stalitsa
 * @version 1.0
 */
@Sources({
	"file:./config/custom/discord.properties",
	"file:./config/discord.properties",
	"classpath:config/discord.properties"
})
@LoadPolicy(MERGE)
@HotReload(value = 20, unit = MINUTES, type = ASYNC)
public interface BotConfiguration extends Mutable, Reloadable {

	@Key("BotPrefix")
	String getPrefix();

	@Key("BotToken")
	String getBotToken();

	@Key("ServerId")
	String getServer();

	@Key("ModeratorRoleId")
	String getModerationRoleId();

	@Key("WelcomeMessageEnable")
	Boolean WelcomeEnable();

	@Key("WelcomeChannelId")
	String getWelcomeChannel();

	@Key("WelcomeMessageDeleteDelay")
	Long getWelcomeDeletionDelay();

	@Key("RulesChannelId")
	String getRulesChannel();

	@Key("OptionalChannelOneId")
	String getOptionalChannelOne();

	@Key("OptionalChannelTwoId")
	String getOptionalChannelTwo();

	@Key("OptionalChannelThreeId")
	String getOptionalChannelThree();

	@Key("AutoRoleEnable")
	Boolean AutoRoleEnable();

	@Key("AutoRoleId")
	String getAutoRoleId();

	@Key("AnnouncementsChannelId")
	String getAnnounceChannel();

	@Key("AutoSupportChannelId")
	String getAutoSupportChannel();

	@Key("LogsChannelId")
	String getLogsChannel();

	@Key("BotDeleteDelay")
	Long getBotDeletionDelay();

	@Key("UserDeleteDelay")
	Long getUserDeletionDelay();

	@Key("ForbiddenWords")
	Set<String> getForbiddenWords();
}
