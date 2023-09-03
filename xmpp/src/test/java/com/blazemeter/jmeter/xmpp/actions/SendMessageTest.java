package com.blazemeter.jmeter.xmpp.actions;

import com.blazemeter.jmeter.xmpp.JMeterXMPPSampler;
import com.blazemeter.jmeter.xmpp.JMeterXMPPSamplerMock;
import com.blazemeter.jmeter.xmpp.XMPPConnectionMock;
import org.apache.jmeter.samplers.SampleResult;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;


public class SendMessageTest {
    @Test
    public void perform() throws Exception {
        JMeterXMPPSampler sampler = new JMeterXMPPSamplerMock();
        sampler.getXMPPConnection().setFromMode(XMPPConnection.FromMode.USER);
        sampler.setProperty(SendMessage.RECIPIENT, "test@test.com");
        sampler.setProperty(SendMessage.WAIT_RESPONSE, true);

        SendMessage action = new SendMessage();
        action.connected(sampler.getXMPPConnection());

        Message resp = new Message();
        resp.setFrom(JidCreate.bareFrom("test@test.com"));
        resp.setBody(SendMessage.RESPONSE_MARKER);
        action.processStanza(resp);
        SampleResult res = new SampleResult();
        action.perform(sampler, res);
        Assert.assertTrue(res.getResponseDataAsString().contains(SendMessage.RESPONSE_MARKER));
        Assert.assertTrue(res.getSamplerData().contains("from"));
    }

}