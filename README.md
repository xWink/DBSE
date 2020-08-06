# DBSE (Discord Bot Spring Edition)
A remastered version of the University of Guelph Bachelor of Computing Discord Bot using Kotlin Spring

# Functionality
* Message backups - every message that is not made by a bot and does not trigger a bot command is stored with MariaDB
* Edited Message Tracking - when a user edits a message, the previous version of that message is logged in a channel named "edited-messages"
* Deleted Message Tracking - when a user deletes a message, the latest version of that message is logged in a channel named "deleted-messages"
* Admin Commands:

  1. `!echo <content>` - deletes the caller's message and repeats the content requested, including attachments
  2. `!purge <number>` - deletes the caller's message + the given number of prior messages in the channel (max 99)
  
* General Commands:

  1. `!ping` - shows the latency between client and Discord servers in ms
  2. `!flip` - flips a coin, displays an image and text of the side the coin landed on
  3. `!id <number>` - shows the name of the user with the corresponding id number

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

#### Note: if the database connection fails for any reason, all components that do not depend on the db will still function
