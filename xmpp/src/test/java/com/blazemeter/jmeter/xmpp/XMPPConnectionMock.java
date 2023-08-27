package com.blazemeter.jmeter.xmpp;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.parts.Resourcepart;

public class XMPPConnectionMock extends AbstractXMPPConnection {
    private static final Logger log = LoggingManager.getLoggerForClass();

    public boolean isConnected = true;
    public boolean isAuthenticated = true;

    public XMPPConnectionMock() {
        super(XMPPTCPConnectionConfiguration.builder().setHost("unitTest").build());
    }

    @Override
    public void instantShutdown() {
        log.debug("instantShutdown");
    }

    @Override
    public boolean isSecureConnection() {
        return false;
    }

    @Override
    protected void sendStanzaInternal(Stanza packet) {
        log.debug("Emul sending packet: " + packet.toXML());

    }

    @Override
    public void sendNonza(Nonza element) {
        log.debug("Emul sending packet: " + element.toXML());

    }

    @Override
    public boolean isUsingCompression() {
        return false;
    }

    @Override
    protected void connectInternal() {
        log.debug("Emul connect");
    }

    @Override
    protected void loginInternal(String username, String password, Resourcepart resource) {
        log.debug("Emul login");
    }

    @Override
    protected void shutdown() {
        log.debug("Emul Shutdown");
    }

    @Override
    public void processStanza(Stanza stanza) throws InterruptedException {
        super.processStanza(stanza);
    }

}
