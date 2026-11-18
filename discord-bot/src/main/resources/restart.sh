#!/bin/sh
cd /opt/l2j/bot && tmux kill-session -t L2JBot && tmux new -d -s L2JBot 'cd /opt/l2j/bot && java -Xms128m -Xmx128m -jar DiscordBot.jar > log/stdout.log 2>&1'