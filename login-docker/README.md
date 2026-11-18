# Build the Image

Use the `make_docker_image.sh` utility to build the docker image:

`sudo ./make_docker_image.sh`

This will build `l2j-server-login:latest`. For more options call `./make_docker_image.sh -h`.

# Quickstart

If you are familar with `docker-compose` you can take a quick look into the example `docker-compose.yml`. It provides all the basic configurations required to get started.

# Internal Structure

The container has the following l2j specific directories inside:

- `/opt/l2j/deploy/`: Clean l2j files, extracted from a build of the l2j repositories.
- `/opt/l2j/custom/`: Custom files, overwriting or extending clean l2j files.
    - `/opt/l2j/custom/login/config/*.properties:` Overwrite properties from clean l2j files.

# Database Initialization

> :warning: **The database initialization destroys your database if it already exists!**

If you do not have a loginserver database yet, you need to perform database initialization.

Specify `L2JLOGIN_DB_INIT=1` to tell the container to perform database initialization. After database initialization, the container automatically stops.

Additionally to `L2JLOGIN_DB_INIT` the following environment variables are required for the initialization to succeed:

- `L2JLOGIN_DB_HOST`: db server host
- `L2JLOGIN_DB_PORT`: db server port
- `L2JLOGIN_DB_USER`: db user created for the loginserver db
- `L2JLOGIN_DB_PASS`: password for the db user created for the loginserver db
- `L2JLOGIN_DB_NAME`: db name for the loginserver db
- `L2JLOGIN_DB_INSTALL_USER`: db user used to install the loginserver db
- `L2JLOGIN_DB_INSTALL_PASS`: password of the db user to install the loginserver db

When the database initialization is done, you have to remove the environment variables as the database is destroyed every time the intitialization is performed.

# Environment Variables

- `L2JLOGIN_JAVA_ARGS`: defaults to `-Xms128m -Xmx256m`
    - Arguments to be added to the invokation of the java virtual machine. Additional to the arguments specified here, only the `-jar` argument is added.
- `L2JLOGIN_APP_ARGS`: by default empty
    - Arguments passed to the l2j server application.
- `L2JLOGIN_<propertiesFileName>_<propertyName>`
    - Overwrite a property of a properties file from l2j's config folder. See the next section `Modifying Server Configuration` for more details.

# Modifying Server Configuration

You have two possibilities to overwrite server configuration properties:

1. Environment Variables: Name the environment variables `L2JLOGIN_<propertiesFileName>_<propertyName>`. Example:
    - `L2JLOGIN_server_GameServerHost`: Overwrites the `GameServerHost` property of the `server.properties` file.
1. .properties files:
    1. Map the custom config directory to your host or a named volume.
    1. Create a .properties file with the same name as a clean l2j .properties file in the mapped directory.
    1. Only specify the properties which you want to change.

The later possibility overwrites the previous possibility.

# Important Server Configurations

- `server.properties GameServerHost`: If your gameservers connect from outside and not localhost, you either need to specify the IP of your gameservers or specify `*` to allow gameservers to connect to the loginserver from everywhere.
- `database.properties URL`: jdbc db url `jdbc:mariadb://<dbHost>:<dbPort>/<dbName>`
- `database.properties User`: db user
- `database.properties Password`: password of db user

For more server configurations, consult L2Js documentation.
