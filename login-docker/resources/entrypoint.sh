#!/bin/sh

L2JLOGIN_DB_INIT=${L2JLOGIN_DB_INIT:-0}
L2JLOGIN_DB_HOST=${L2JLOGIN_DB_HOST:-localhost}
L2JLOGIN_DB_PORT=${L2JLOGIN_DB_PORT:-3306}
L2JLOGIN_DB_NAME=${L2JLOGIN_DB_NAME:-l2jls}
L2JLOGIN_DB_USER=${L2JLOGIN_DB_USER:-l2jls}
L2JLOGIN_DB_PASS=${L2JLOGIN_DB_PASS:-l2jls}
L2JLOGIN_DB_INSTALL_USER=${L2JLOGIN_DB_INSTALL_USER:-root}
L2JLOGIN_DB_INSTALL_PASS=${L2JLOGIN_DB_INSTALL_PASS:-root}

sleep 5

. /procman.sh

if [ $L2JLOGIN_DB_INIT -eq 1 ]; then
  echo "Initializing database..."
  /init_database.sh "$L2JLOGIN_DB_HOST" "$L2JLOGIN_DB_PORT" "$L2JLOGIN_DB_NAME" "$L2JLOGIN_DB_USER" "$L2JLOGIN_DB_PASS" "$L2JLOGIN_DB_INSTALL_USER" "$L2JLOGIN_DB_INSTALL_PASS" "$L2JLOGIN_DIR" "LOGIN"
  status=$?
  if [ $status -ne 0 ]; then
    echo "Failed to initialize database '$L2JLOGIN_DB_NAME'! Quitting..."
    return 1
  fi
else
  # Modify default configuration by environment variables
  echo "Processing environment variable configs..."
  env -0 | while IFS='=' read -r -d '' key value; do
    #echo "$key"
    if [[ $key == L2J* ]]; then
      # Get first part of key
      serverComponent="$(echo $key | cut -d'_' -f1)"
      # Get second part of key
      propertiesFile="$(echo $key | cut -d'_' -f2)"
      # Get third part of key
      property="$(echo $key | cut -d'_' -f3)"

      # extract actual server component (L2JLOGIN becomes login)
      serverComponent="$(echo $serverComponent | cut -c4-)"
      serverComponent="$(echo $serverComponent | tr '[A-Z]' '[a-z]')"

      propertiesDir="$L2J_DEPLOY_DIR/$serverComponent/config"
      propertiesFile="$propertiesFile.properties"

      if [ -f "$propertiesDir/$propertiesFile" ]; then
        #echo "-> $serverComponent $propertiesFile: $property = $value"
        sed -i -e "s&^$property\\s*=.*&$property=$value&" "$propertiesDir/$propertiesFile"
      fi
    fi
  done

  # todo - database upgrade - L2JLOGIN_DB_AUTO_UPDATE

  # launch loginserver
  L2JLOGIN_JAVA_ARGS=${L2JLOGIN_JAVA_ARGS:-"-Xms128m -Xmx256m"}
  L2JLOGIN_APP_ARGS=${L2JLOGIN_APP_ARGS:-""}

  LOGDIR=logs

  echo "Changing directory to: '$L2J_DEPLOY_DIR/$L2JLOGIN_DIR/'"
  cd "$L2J_DEPLOY_DIR/$L2JLOGIN_DIR/"

  err=2
  while [ $err -eq 2 ]; do

    # Delete old *.lck files and archive old logs
    [ -f "$LOGDIR/*.lck" ] && rm "$LOGDIR/*.lck"
    for LOGFILE in "$LOGDIR/*"; do
      [ "$LOGFILE" == "$LOGDIR/*" ] && continue
      LOGFILE_NAME="${LOGFILE#*/}"
      [[ "$LOGFILE_NAME" == [0-9]* ]] && continue
      mv "$LOGFILE" "$LOGDIR/`date +%Y-%m-%d_%H-%M-%S`_$LOGFILE_NAME"
    done

    java $L2JLOGIN_JAVA_ARGS -jar l2jlogin.jar $L2JLOGIN_APP_ARGS &
    server_pid=$!
    procman_add_child_pid "$server_pid"
    echo "Loginserver started with PID $server_pid."

    wait "$server_pid"
    err=$?

    echo "Loginserver exited with exit code $err."
  done

fi
