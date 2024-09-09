import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainFile extends ListenerAdapter {

    static String accessToken;
    static OAuth2Credential credential;
    static TwitchClient twitchClient;
    static JDABuilder builder;
    static int batchSize = 20;
    static ArrayList<StructWhole> all_channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        setCredentials();
        detectIRCMessageEvent(twitchClient);

    }

    public static void setCredentials() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("credentials.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            list.add(line);
        }

        if (list.size() != 4) {
            System.out.println("4 Credentials required, please check credentials.txt for inputs. Exiting program!");
            System.exit(1);
        }

        accessToken = list.get(0);
        credential = new OAuth2Credential("twitch", accessToken);
        twitchClient = TwitchClientBuilder.builder()
                .withClientId(list.get(1))
                .withClientSecret(list.get(2))
                .withEnableChat(true)
                .withEnableHelix(true)
                .withChatAccount(credential)
                .build();

        builder = JDABuilder.createDefault("")
                .setToken(list.get(3))
                .enableIntents(GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.watching("the world burn"))
                .addEventListeners(new MainFile());
        builder.build();
    }

    public static void detectIRCMessageEvent(TwitchClient twitchClient) {
        twitchClient.getEventManager().onEvent(IRCMessageEvent.class, event -> {
            String eventType = event.getCommandType();
            if(eventType.equals("PRIVMSG")) {
                String channelname = event.getChannelName().get();
                MessageChannel channel;

                for (StructWhole a : all_channels) {
                    if(a.getChannelname().equals(channelname)) {
                        a.addMessage(event);
                        System.out.println(a.getMessageSize());
                        if(a.getMessageSize() >= batchSize) {
                            channel = a.getChannel();
                            String out = "";

                            for (int i = 0; i<a.getMessageSize(); i++) {
                                out += a.getDate(i) +" "
                                        +a.getEvent(i).getUserName().replaceAll("_", "\\\\_")
                                        +": " +a.getEvent(i).getMessage().get().replaceAll("_", "\\\\_")
                                        +"\n";
                            }
                            a.clearMessages();
                            channel.sendMessage(out).queue();
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            Message msg = event.getMessage();

            if (msg.getContentRaw().startsWith("!register")) {
                String channel_name = msg.getContentRaw().split(" ")[1];
                twitchClient.getChat().joinChannel(channel_name);
                MessageChannel channel = event.getChannel();
                StructWhole temp = new StructWhole(channel_name, channel);
                all_channels.add(temp);
            }
        }
    }
}