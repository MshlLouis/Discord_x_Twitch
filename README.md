# A Program that reads Messages from Twitch Channels and saves the Messages on a Discord Server


## 1: Notes

Warning: This program might not be approved by Discords TOS and could potentially get your account banned. I'm not taking any responsibility for your actions. Use at your own risk!



## 2: Requirements

- IDE + Gradle v8.4+
- Discord Account
- Twitch Account


## 3: Initializing

There are several steps to get the program up and running. They are categorized into the following sections:

3.1 Setting up Twitch Credentials


3.2 Setting up Discord Credentials


3.3 Setting up Discord


### 3.1 Setting up Twitch Credentials

First, visit https://twitchtokengenerator.com and generate a bot chat token. To do so, scroll down, select all scopes and generate a token. Copy the Access Token and put it in your 
"credentials.txt" file.


Next, you will have to create an application to get your client tokens. To do so, visit https://dev.twitch.tv/console and login with your Twitch Account. Next, click 
"Register Your Application" and fill in every field. Important: For "OAuth Redirect URLs", set it to "https://twitchtokengenerator.com". Once created, click "manage" and 
copy your Client ID and Client Secret into your "credentials.txt" file.


### 3.2 Setting up Discord Credentials

To get credentials for Discord, visit https://discord.com/developers/applications and login. There, create a new application and give it a name. Navigate to "Bot" and grant all 3 
Privileged Gateway Intents. Then click "Reset Token" which will ask for verification of your identity and then show you the token. Copy the token and paste it into your 
"credentials.txt" file. That's it.


### 3.3 Setting up Discord

Now you're ready to setup Discord  itself. Create a new server and add your application to the server. To do so go to your Discord Application, go to "Installation" and scroll down.
Under Guild Install add "bot" to scopes and "admin" to permissions. Now you can use the Install Link above to add the app to your server. On your server, create a new Category. 
In the category, create text channels of the streamers that you want to save Chat Messages from.



## 4: Running

Now you're ready to run the bot. Once it's up it should appear in the memberlist. Now the only thing left to do is write the command "!register [Twitch Channelname]" and the bot will
save all the received messages. Since the rate limit of Discord Bots is 5 messages per 5 seconds, the messages are collected into batches of 20 and then printed as 1 message.
This can be changed depending on the chats activity.
