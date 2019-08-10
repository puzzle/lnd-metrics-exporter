package ch.puzzle.lnd.metricsexporter.scrapers.channel;

public class ChannelInexistentException extends Exception{

    private final static String MESSAGE = "Channel does not exist.";

    public ChannelInexistentException() {
        super(MESSAGE);
    }
}
