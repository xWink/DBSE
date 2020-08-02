# Discord-Bot-Spring-Edition
A remastered version of the University of Guelph Bachelor of Computing Discord Bot using Kotlin Spring

# Functionality
* Edited Message Tracking - when a user edits a message, the previous version of that message is logged in a channel named "edited-messages"
* Deleted Message Tracking - when a user deletes a message, the latest version of that message is logged in a channel named "deleted-messages"
* Commands:

  1. `!echo <content>` - deletes the caller's message and repeats the content requested, including attachments
  2. `!purge <number>` - deletes the caller's message + the given number of prior messages in the channel (max 99)
