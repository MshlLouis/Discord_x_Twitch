# A Program that reads Messages from Twitch Channels and saves the Messages on a Discord Server


## 1: Notes

Warning: This program might not be approved by Discords TOS and could potentially get your account banned. I'm not taking any responsibility for your actions. Use at your own risk!



## 2: Requirements

- IDE + Gradle v8.4+
- Discord Account
- Twitch Account


## 3: Initializing

There are several steps to get the program up and running. They are categorized into the following sections:

- 3.1 Setting up Twitch Credentials
- 3.2 Setting up Discord Credentials
- 3.3 Setting up Discord


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
In the category, create text channels of the streamers that you want to save Chat Messages from. Make sure the Discord channelname is exactly like the streamers Twitch channelname.


## 4: Running

Now you're ready to run the bot. Once it's up it should appear in the memberlist. Now the only thing left to do is to open a text channel and write the command "!register [Twitch Channelname]" and the bot will
save all the received messages. The textchannel will be saved to the "registered_channels.txt" file, so whenever you restart the bot you can just write the command "!start" and the bot will automatically join
all previously registered channels.


## 5: How it works


Every Discord application is rate limited to 5 messages per 5 seconds per textchannel. Since big streamers chats regularly exceed this limit, the bot would be overloaded which is why messages are not handled individually but in batches.
Discords max length for 1 message for standard accounts is 2000 characters. Whenever a new message is received for a channel, the number of chars is calculated. If the number of chars of the current batch plus the new messages number of chars
is bigger than 1980 (this is currently just a test value), the whole batch is printed and a new one is being created with the new message as first entry. This way the batch size is not fixed like in previous versions making the whole program
more efficient and allowing for more messages to be saved in spam situations.
