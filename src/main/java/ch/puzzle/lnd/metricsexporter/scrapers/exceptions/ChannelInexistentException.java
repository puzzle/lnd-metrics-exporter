package ch.puzzle.lnd.metricsexporter.scrapers.exceptions;

public class ChannelInexistentException extends Exception{

    private final static String MESSAGE = "Channel does not exist.";

    public ChannelInexistentException() {
        super(MESSAGE);
    }
}
