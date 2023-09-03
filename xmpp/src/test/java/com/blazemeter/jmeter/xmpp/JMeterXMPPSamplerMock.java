package com.blazemeter.jmeter.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JMeterXMPPSamplerMock extends JMeterXMPPSampler {

    public XMPPConnectionMock conn = new XMPPConnectionMock();

    public JMeterXMPPSamplerMock() throws IOException, SmackException, XMPPException, InterruptedException {
        conn.connect();
    }

    @Override
    public XMPPConnection getXMPPConnection() throws KeyManagementException, NoSuchAlgorithmException, SmackException {
        return conn;
    }
}
