# DBSE (Discord Bot Spring Edition)
A remastered version of the University of Guelph Bachelor of Computing Discord Bot using Kotlin, Spring Boot, and Java
Discord API. Currently hosted on Google Cloud Platform and designed for use with MariaDB.

# Functionality
* Message backups - every message that is not made by a bot and does not trigger a bot command is stored with MariaDB
* Edited Message Tracking - when a user edits a message, the previous version of that message is logged in a channel
chosen by configuration
* Deleted Message Tracking - when a user deletes a message, the latest version of that message is logged in a channel
chosen by configuration
* Custom Private Channel Generation - messages sent by the bot in the channelOptions channel lead to the creation of a
new channel/role named after the message
* Private Channel Join/Leave - when a user "confirm" reacts to a message on the channelOptions channel, they gain access
to the corresponding private channel; removing the react removes access
* Terms of Service Agreement Forms - don't let newcomers see any channel except the ones you want until they click a
reaction to accept your terms of service
* Karma System - upvote and downvote each other's messages and track your karma rating
* Economy - create and view a marketplace with custom roles for sale, buy the roles with currency earned from games

### Admin Commands:
  1. `echo <content>` - deletes the caller's message and repeats the content requested, including attachments
  2. `purge <number>` - deletes the caller's message + the given number of prior messages in the channel (max 99)
  3. `showchanneloptions` - prints contents of channelOptions.txt in the channelOptions channel (each line is a message)
  
### General Commands:
  1. `flip` - flips a coin, displays an image and text of the side the coin landed on
  2. `id <number>` - shows the name of the user with the corresponding id number
  3. `info <course id>` - provides detailed information about the UoGuelph course with the corresponding id
  4. `ping` - shows the latency between client and Discord servers in ms
  5. `market` - shows the roles available for purchase on the marketplace
  6. `buy <listing #>` - buy a role from the marketplace
  7. `removerole` - remove your currently active purchased role
  8. `gift <@user> <amount>` - send a gift of GryphCoins to a specified user

# Forking
In order to use this bot, you must create an `application.properties` file in src/main/resources.
Below is the full list of properties to use, and further down is an explanation of each property and its purpose.
If you delete a line, functionality that depends on that property will stop.

##### All properties under `spring` and those at the top level of `bot` are mandatory.
##### Comment out any property line you do not fill. This will turn off any functionality using that property.

```
# Mandatory bot settings
bot.token = <bot token>
bot.serverId = <the Discord server's ID>
bot.ownerId = <your Discord user ID>
bot.commandPrefix = <command prefix (eg. !)>

# Channel id's
bot.channel.id.welcome = <id of channel with welcome message and terms of service agreement>
bot.channel.id.bulkDeletedMessages = <id of channel where purged messages are logged>
bot.channel.id.deletedMessages = <id of channel where deleted messages are logged>
bot.channel.id.editedMessages = <id of channel where edited messages are logged>
bot.channel.id.channelOptions = <id of channel where opt-in channel options are displayed>

# Emote id's
bot.emote.id.confirm = <id of emote used for confirmation (like a checkmark)>
bot.emote.id.upVote = <id of emote used to signify an upvote>
bot.emote.id.downVote = <id of emote used to signify a downvote>

# Role id's
bot.role.id.globalAccess = <comma-separated list of id's of roles that can see all generated channels>
bot.role.id.notify = <id of role identifying members who want to receive notifications>
bot.role.id.welcome = <id of role given to members who accepted the terms of service in the welcome channel>

# Market listings
bot.market.listings[0].name = <name of role>
bot.market.listings[0].cost = <price of role>
bot.market.listings[0].roleId = <id of role on Discord>
bot.market.listings[0].durationDays = <number of days the role lasts before expiry>

bot.market.listings[1].name = Example
bot.market.listings[1].cost = 35
bot.market.listings[1].roleId = 584811465622552751
bot.market.listings[1].durationDays = 7

# Database configuration
spring.datasource.url = jdbc:mariadb://<database server URL:port>/<database name>
spring.datasource.username = <database username>
spring.datasource.password = <database password>
spring.datasource.driver-class-name = org.mariadb.jdbc.Driver
spring.datasource.hikari.minimum-idle = 0
spring.datasource.hikari.initialization-fail-timeout = -1
spring.datasource.continue-on-error = true
spring.jpa.database-platform = org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto = update
spring.jpa.show-sql = true
```

### Database
Ensure that you have a database for the bot to connect to. All tables will be autogenerated when DBSE
starts up for the first time.

###### Note: if the database disconnects, all components that do not depend on it will still function

### Private Channel Generation
You should modify src/main/resources/channelOptions.txt to contain the information you want. Each line is a new message.
Lines starting with "-" are considered decorative and will not lead to the creation of a new private channel or role.

To start generating custom private channels, ensure you have configured `bot.channel.id.channelOptions` and
`bot.emote.id.confirm` in application.properties. It is recommended that you disallow adding new reactions or sending
messages on the channelOptions channel to prevent chaos. Then, simply use the "showchanneloptions" command in your
Discord server and the bot will dynamically create channels based on the content of channelOptions.txt. Alongside each
new channel, an associated role will be created, which is what gives users access to the private channel upon "confirm"
reacting to a listed channel option.

Feel free to organize the locations of the channels on the Discord sidebar to your liking once they are created.

### Terms of Service Form
Choose a channel to be your "welcome" channel and assign its ID to `bot.channel.id.welcome` in
application.properties. Post the rules of your server on this channel and react your "confirm" emote 
(`bot.emote.id.confirm`) so that users have something to click on. It is recommended that you disallow adding
new reactions or sending messages on this channel to prevent chaos.

Create a role for people who click "confirm" on your terms of service and set that role's ID to
`bot.role.id.acceptedToS` in application.properties. Set the role permissions for @everyone to disallow "read messages"
on every channel except the welcome channel, and the role permissions for your acceptedToS role to allow "read
messages" on all those channels.

When a user clicks the "confirm" reaction on your welcome message, they will receive the acceptedToS role and be able
to see all of the previously hidden channels.

# Running the Application
The easiest ways to run this application are as follows:
* Using Gradle on the command line
* Using Intellij IDEA

### Executing with Gradle CLI
1. Navigate to the root folder of this project
2. On terminal, enter the command `gradle assemble` to generate a jar file in build/libs
3. Use `java -jar <path/to/jar/file.jar>` to execute the jar

### Executing with Intellij IDEA
1. Open Intellij IDEA
2. Choose the open or import project option
3. Navigate to this project and select build.gradle.kts
4. Select "Open as Project"
5. Once Intellij is finished building and indexing, open src/main/kotlin/com.wink.dbse
6. Right click DBSEApplication.kt and select "Run DBSEApplication"
