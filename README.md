# Discord-Bot-Spring-Edition
A remastered version of the University of Guelph Bachelor of Computing Discord Bot using Kotlin Spring

# Functionality
* Message backups - every message that is not made by a bot and does not trigger a bot command is stored in a database
* Edited Message Tracking - when a user edits a message, the previous version of that message is logged in a channel named "edited-messages"
* Deleted Message Tracking - when a user deletes a message, the latest version of that message is logged in a channel named "deleted-messages"
* Commands:

  1. `!echo <content>` - deletes the caller's message and repeats the content requested, including attachments
  2. `!purge <number>` - deletes the caller's message + the given number of prior messages in the channel (max 99)

# Forking
In order to use this bot, you must create an `application.properties` file in src/main/resources. The following properties are required:

```
bot.token = <bot token>
bot.ownerId = <your Discord user ID>
bot.commandPrefix = <command prefix (eg. !)>
spring.datasource.url = <database URL>
spring.datasource.username = <database username>
spring.datasource.password = <database password>
spring.datasource.driver-class-name = org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto = validate
spring.jpa.show-sql = true
```

#### If you choose to use a database other than MariaDB, you will have to change the existing dependencies according to your needs
