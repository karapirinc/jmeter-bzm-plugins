package com.blazemeter.jmeter.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.stringprep.XmppStringprepException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JMeterXMPPSamplerMock extends JMeterXMPPSampler {

    public XMPPConnectionMock conn = new XMPPConnectionMock();

    public JMeterXMPPSamplerMock() throws XmppStringprepException, SmackException, XMPPException, InterruptedException {
    }

    @Override
    public XMPPConnection getXMPPConnection() throws KeyManagementException, NoSuchAlgorithmException, SmackException {
        return conn;
    }
}
