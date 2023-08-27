package com.blazemeter.jmeter.xmpp;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;

public class Loggers {
    private static final Logger log = LoggingManager.getLoggerForClass();

    public static class LogRecv implements StanzaListener {
        private final XMPPConnection conn;

        public LogRecv(XMPPConnection conn) {
            this.conn = conn;
        }

        @Override
        public void processStanza(Stanza stanza) throws SmackException.NotConnectedException {
            try {
                log.debug("Stanza recv [" + conn.getStreamId() + "]: " + stanza.toXML());
            } catch (IllegalArgumentException e) {
                log.debug("Failed to log packet", e);
                log.debug("Stanza recv [" + conn.getStreamId() + "]: " + stanza.getError());
            }
        }
    }

    public static class LogSent implements StanzaListener {
        private final XMPPConnection conn;

        public LogSent(XMPPConnection conn) {
            this.conn = conn;
        }

        @Override
        public void processStanza(Stanza stanza) throws SmackException.NotConnectedException {
            log.debug("Stanza sent [" + conn.getStreamId() + "]: " + stanza.toXML());
        }
    }

    public static class LogConn implements ConnectionListener {
        private final XMPPConnection conn;

        public LogConn(XMPPConnection conn) {
            this.conn = conn;
        }

        @Override
        public void connected(XMPPConnection connection) {
            log.debug("Connected: " + connection.getStreamId());
        }

        @Override
        public void connectionClosed() {
            log.debug("Connection closed: " + conn.getStreamId());
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            log.error("Connection closed with error: " + conn.getStreamId(), e);
        }

    }
}
