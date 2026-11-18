#!/bin/sh
cd /opt/l2j/bot && tmux new -d -s L2JBot 'cd /opt/l2j/bot && java -Xms128m -Xmx128m -jar DiscordBot.jar' && tmux a -t L2JBot