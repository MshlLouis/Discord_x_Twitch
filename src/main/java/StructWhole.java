import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StructWhole {

    String channelname;
    MessageChannel channel;
    ArrayList<StructEvent> messages = new ArrayList<>();
    public static DateTimeFormatter timeFormatDate = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    public StructWhole(String channelname, MessageChannel channel) {
        this.channelname = channelname;
        this.channel =  channel;
    }

    public void addMessage(IRCMessageEvent event) {
        StructEvent temp = new StructEvent(event, timeFormatDate.format(LocalDateTime.now()));
        this.messages.add(temp);
    }

    public String getChannelname() {
        return this.channelname;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }


    public IRCMessageEvent getEvent(int index) {
        return this.messages.get(index).event;
    }

    public int getMessageSize() {
        return this.messages.size();
    }

    public String getDate(int index) {
        return this.messages.get(index).date;
    }

    public void clearMessages() {
        this.messages.clear();
    }
}
