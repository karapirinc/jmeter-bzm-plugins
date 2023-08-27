package com.blazemeter.jmeter.xmpp.actions;

import com.blazemeter.jmeter.xmpp.JMeterXMPPSamplerMock;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.bookmarks.Bookmarks;
import org.jivesoftware.smackx.iqprivate.packet.PrivateData;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

public class GetBookmarksTest {
    private static final Logger log = LoggingManager.getLoggerForClass();


    public void testPerform() throws Exception {
        GetBookmarks obj = new GetBookmarks();
        final JMeterXMPPSamplerMock sampler = new JMeterXMPPSamplerMock();
        Thread thr = new Thread() {
            @Override
            public void run() {
                while (true) {
                    //if (!sampler.conn.getCollectors().isEmpty())
                    try {
                        sampler.conn.processStanza(new PrivateDataResult(new Bookmarks()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        thr.start();
        Thread.sleep(sampler.conn.getReplyTimeout() / 10);
        obj.perform(sampler, new

                SampleResult());

    }

    @Test
    public void testAddUI() throws Exception {
        GetBookmarks obj = new GetBookmarks();
        obj.addUI(new JPanel(), new GridBagConstraints(), new GridBagConstraints());
    }

    /**
     * An IQ packet to hold PrivateData GET results.
     */
    private static class PrivateDataResult extends IQ {

        public static final String NAMESPACE = "jabber:iq:private";
        public static final String ELEMENT_NAME = "private";
        private final PrivateData privateData;

        public PrivateDataResult(PrivateData privateData) {
            super(ELEMENT_NAME, NAMESPACE);
            this.setType(Type.get);
            this.privateData = privateData;
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.append(privateData.toXML());
            return xml;
        }
    }
}