import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class StructEvent {

    IRCMessageEvent event;
    String date;

    public StructEvent(IRCMessageEvent event, String date) {
        this.event = event;
        this.date = date;
    }
}
