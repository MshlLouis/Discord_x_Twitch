import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainFile extends ListenerAdapter {

    static String accessToken;
    static OAuth2Credential credential;
    static TwitchClient twitchClient;
    static JDABuilder builder;
    static ArrayList<StructWhole> all_channels = new ArrayList<>();
    static DateTimeFormatter timeFormatDate = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

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
                        String temp =  "0000.00.00 00:00:00"
                                +" " +event.getUser().getId()
                                +" " +event.getUserName().replaceAll("_", "\\\\_")
                                +": " +event.getMessage().get().replaceAll("_", "\\\\_")
                                +"\n";
                        int msgLen = temp.length();

                        if(a.getCharCounter() + msgLen > 1980) {
                            channel = a.getChannel();
                            String out = "";

                            for (int i = 0; i<a.getMessageSize(); i++) {
                                out += a.getDate(i)
                                        +" " +a.getEvent(i).getUser().getId()
                                        +" " +a.getEvent(i).getUserName().replaceAll("_", "\\\\_")
                                        +": " +a.getEvent(i).getMessage().get().replaceAll("_", "\\\\_")
                                        +"\n";
                            }
                            a.clearMessages();
                            a.resetCharCounter();
                            channel.sendMessage(out).queue();
                        }
                        String date = timeFormatDate.format(LocalDateTime.now());
                        a.addMessage(event, date);
                        a.addCharCounter(msgLen);

                        System.out.println(a.getChannelname() +" " +a.getMessageSize());
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
                String channelname = msg.getContentRaw().split(" ")[1];
                twitchClient.getChat().joinChannel(channelname);
                MessageChannel channel = event.getChannel();
                StructWhole temp = new StructWhole(channelname, channel);
                all_channels.add(temp);

                try (FileOutputStream fos = new FileOutputStream("registered_channels.txt", true);
                     OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                     BufferedWriter bw = new BufferedWriter(osw)) {

                    bw.write(channel + "\n");
                    bw.flush();
                } catch (IOException ignored) {

                }
            }
            if(msg.getContentRaw().equals("!start")) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage("Bot started at: " +timeFormatDate.format(LocalDateTime.now())).queue();
                try {
                    BufferedReader br = new BufferedReader(new FileReader("registered_channels.txt"));
                    String line;

                    Guild guild = event.getGuild();
                    List<TextChannel> guildChannels = guild.getTextChannels();

                    while ((line = br.readLine()) != null) {
                        if(guildChannels.toString().contains(line)) {
                            int index = 0;
                            for (int i = 0; i<guildChannels.size(); i++) {
                                if(guildChannels.get(i).toString().equals(line)) {
                                    index = i;
                                    break;
                                }
                            }
                            StructWhole temp = new StructWhole(guildChannels.get(index).getName(), guildChannels.get(index));
                            all_channels.add(temp);
                            twitchClient.getChat().joinChannel(guildChannels.get(index).getName());
                        }
                        else {
                            System.out.println("No channel found for " +line);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            if (msg.getContentRaw().startsWith("!getSize")) {
                event.getMessage().delete().queue();
                String channelname = msg.getContentRaw().split(" ")[1];

                for (StructWhole a : all_channels) {
                    if (a.getChannelname().equals(channelname)) {
                        MessageChannel channel = event.getChannel();
                        channel.sendMessage("Current Message Size for Channel \"" +channelname +"\": " +a.getMessageSize()).queue();
                    }
                }
            }
        }
    }
}