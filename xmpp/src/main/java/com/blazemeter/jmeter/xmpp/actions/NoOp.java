package com.blazemeter.jmeter.xmpp.actions;


import com.blazemeter.jmeter.xmpp.JMeterXMPPSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NoOp extends AbstractXMPPAction implements StanzaListener {
    private static final Logger log = LoggingManager.getLoggerForClass();
    private Queue<Stanza> incomingPackets = new LinkedBlockingQueue<>();

    @Override
    public String getLabel() {
        return "Collect Incoming Packets";
    }

    @Override
    public void addUI(JComponent panel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
        panel.add(new JLabel("Generates no sample if there was no incoming packets."));
    }

    @Override
    public SampleResult perform(JMeterXMPPSampler sampler, SampleResult res) throws Exception {
        long counter = 0;
        for (Stanza packet : incomingPackets) {
            incomingPackets.remove(packet);
            SampleResult subRes = new SampleResult();
            subRes.setSuccessful(true);
            subRes.setResponseCode("200");
            subRes.setResponseMessage("OK");
            subRes.setSampleLabel(packet.getClass().getSimpleName().isEmpty() ? packet.getClass().getName() : packet.getClass().getSimpleName());
            subRes.setResponseData(packet.toXML().toString().getBytes());

            if ((packet instanceof Presence) && (((Presence) packet).getType() == Presence.Type.error)) {
                subRes.setSuccessful(false);
                subRes.setResponseCode("500");
                subRes.setResponseMessage(packet.getError().toString());
            } else if ((packet instanceof Message) && (((Message) packet).getType() == Message.Type.error)) {
                subRes.setSuccessful(false);
                subRes.setResponseCode("500");
                subRes.setResponseMessage(packet.getError().toString());
            } else if ((packet instanceof IQ) && (((IQ) packet).getType() == IQ.Type.error)) {
                subRes.setSuccessful(false);
                subRes.setResponseCode("500");
                subRes.setResponseMessage(packet.getError().toString());
            }

            res.addSubResult(subRes);
            counter++;
        }
        res.setResponseData(("Received packets: " + counter).getBytes());
        return counter > 0 ? res : null;
    }

    @Override
    public void clearGui() {

    }

    @Override
    public void setSamplerProperties(JMeterXMPPSampler sampler) {

    }

    @Override
    public void setGuiFieldsFromSampler(JMeterXMPPSampler sampler) {

    }

    @Override
    public void processStanza(Stanza stanza) throws SmackException.NotConnectedException {
        log.debug("Adding pending stanza: " + stanza.toXML());
        //log.debug("Extensions: " + Arrays.toString(stanza.getExtensions().toArray()));
        incomingPackets.add(stanza);
    }

    @Override
    public StanzaFilter getStanzaFilter() {
        return new OrFilter(StanzaTypeFilter.MESSAGE, StanzaTypeFilter.PRESENCE, new StanzaTypeFilter(IQ.class));
    }
}
