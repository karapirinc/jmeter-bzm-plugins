package com.blazemeter.jmeter.xmpp;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.c2s.ModularXmppClientToServerConnectionConfiguration;
import org.jivesoftware.smack.packet.Nonza;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class XMPPConnectionMock extends AbstractXMPPConnection {
    private static final Logger log = LoggingManager.getLoggerForClass();

    public XMPPConnectionMock() throws XmppStringprepException, SmackException, XMPPException, InterruptedException {
        super(ModularXmppClientToServerConnectionConfiguration.builder()
                .setXmppDomain("unit@Test")
                .setHost("unit@Test")
                .setResource(Resourcepart.from("test@unitTest"))
                .setUsernameAndPassword("test@unitTest", "pass")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build());
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
    protected void connectInternal() throws SmackException, IOException, XMPPException, InterruptedException {
        log.debug("Connecting");
        this.connected = true;
        this.user= JidCreate.entityFullFrom("test@unitTest/testResource");
    }


    @Override
    protected void loginInternal(String username, String password, Resourcepart resource) throws XmppStringprepException {
        log.debug("Emul login");
        this.user= JidCreate.entityFullFrom(username+"/"+resource.toString());
        this.authenticated=true;
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
