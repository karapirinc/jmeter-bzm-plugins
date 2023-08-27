package com.blazemeter.jmeter.xmpp;

import junit.framework.TestCase;
import org.jivesoftware.smack.SmackException;
import org.jxmpp.stringprep.XmppStringprepException;

import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class JMeterXMPPConnectionTest extends TestCase {

    public void testTestStarted() throws NoSuchAlgorithmException, KeyManagementException, SmackException, InterruptedException, UnknownHostException, XmppStringprepException {
        JMeterXMPPConnection obj = new JMeterXMPPConnection();
        obj.setAddress("localhost");
        obj.testStarted("");
        obj.getConnection();
        obj.testEnded("");
    }
}