# DBSE (Discord Bot Spring Edition)
A remastered version of the University of Guelph Bachelor of Computing Discord Bot using Kotlin, Spring Boot, and Java Discord API.

# Functionality
* Message backups - every message that is not made by a bot and does not trigger a bot command is stored with MariaDB
* Edited Message Tracking - when a user edits a message, the previous version of that message is logged in a channel chosen by configuration
* Deleted Message Tracking - when a user deletes a message, the latest version of that message is logged in a channel chosen by configuration
* Custom Private Channel Generation - messages sent by the bot in the channelOptions channel lead to the creation of a new channel/role named after the message
* Private Channel Join/Leave - when a user "confirm" reacts to a message on the channelOptions channel, they gain access to the corresponding private channel. Removing the react removes access.
* Admin Commands:

  1. `!echo <content>` - deletes the caller's message and repeats the content requested, including attachments
  2. `!purge <number>` - deletes the caller's message + the given number of prior messages in the channel (max 99)
  3. `!showchanneloptions` - prints contents of channelOptions.txt in the channelOptions channel (each line is a message)
  
* General Commands:

  1. `!flip` - flips a coin, displays an image and text of the side the coin landed on
  2. `!id <number>` - shows the name of the user with the corresponding id number
  3. `!info <course id>` - provides detailed information about the UoGuelph course with the corresponding id
  4. `!ping` - shows the latency between client and Discord servers in ms

# Forking
In order to use this bot, you must create an `application.properties` file in src/main/resources. The following properties should be used:

```
bot.token = <bot token>
bot.ownerId = <your Discord user ID>
bot.commandPrefix = <command prefix (eg. !)>
channel.name.welcome = <name of channel with welcome message and terms of service agreement>
channel.name.bulkDeletedMessages = <name of channel where purged messages are logged>
channel.name.deletedMessages = <name of channel where deleted messages are logged>
channel.name.editedMessages = <name of channel where edited messages are logged>
channel.name.channelOptions = <name of channel where opt-in channel options are displayed>
bot.emote.id.confirm = <id of emote used for confirmation (like a checkmark)>
bot.role.id.muted = <id of role representin members who cannot speak>
bot.role.id.globalAccess = <comma-separated list of id's of roles that can see all generated channels>
bot.role.id.notify = <id of role identifying members who want to receive notifications>
spring.datasource.url = <database URL>
spring.datasource.username = <database username>
spring.datasource.password = <database password>
spring.datasource.driver-class-name = org.mariadb.jdbc.Driver
spring.datasource.hikari.minimum-idle = 0
spring.datasource.hikari.initialization-fail-timeout = -1
spring.datasource.continue-on-error = true
spring.jpa.hibernate.ddl-auto = validate
spring.jpa.show-sql = true
```

### Database

If you are using your own database, ensure that you have a table called "message" with the following columns:
1. message_id - BigInt (PRIMARY KEY)
2. author_id - BigInt
3. channel_id - BigInt
4. time_sent_secs - BigInt
5. content - VarChar
6. attachment - VarChar

###### Note: if the database connection fails for any reason, all components that do not depend on the db will still function

### Private Channel Generation
You should modify src/main/resources/channelOptions.txt to contain the information you want. Each line is a new message.
Lines starting with "-" are considered decorative and will not lead to the creation of a new private channel or role.

To start generating custom private channels, simply use `!showchanneloptions` in your Discord server and the bot will
dynamically create channels based on the content of channelOptions.txt. Alongside the new channel, an associated
role will be created. This role is what gives users access to the private channel and is given to a user when they react
to the channel options displayed by `!showchanneloptions`.

Feel free to organize the locations of the channels on the Discord sidebar to your liking once they are created!