package com.blazemeter.jmeter.xmpp.actions;


import com.blazemeter.jmeter.xmpp.JMeterXMPPSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;

import javax.swing.*;
import java.awt.*;

public class RawXML extends AbstractXMPPAction {
    public static final java.lang.String XML = "xml";
    private JTextArea msgBody;

    @Override
    public String getLabel() {
        return "Send Raw XML";
    }

    @Override
    public SampleResult perform(final JMeterXMPPSampler sampler, SampleResult res) throws Exception {
        final String xml = sampler.getPropertyAsString(XML);
        res.setSamplerData(xml);
        sampler.getXMPPConnection().sendStanza(new Stanza() {
            @Override
            public String getElementName() {
                return "TODO";
            }

            @Override
            public String toString() {
                return xml;
            }

            @Override
            public CharSequence toXML(XmlEnvironment xmlEnvironment) {
                //TODO
                return null;
            }

            @Override
            public CharSequence toXML() {
                return xml;
            }
        });
        return res;
    }

    @Override
    public void addUI(JComponent panel, GridBagConstraints labelConstraints, GridBagConstraints editConstraints) {
        addToPanel(panel, labelConstraints, 0, 2, new JLabel("XML to Send: ", JLabel.RIGHT));
        addToPanel(panel, editConstraints, 1, 2, msgBody = new JTextArea(5, 20));
    }

    @Override
    public void clearGui() {
        msgBody.setText("");
    }

    @Override
    public void setSamplerProperties(JMeterXMPPSampler sampler) {
        sampler.setProperty(XML, msgBody.getText());
    }

    @Override
    public void setGuiFieldsFromSampler(JMeterXMPPSampler sampler) {
        msgBody.setText(sampler.getPropertyAsString(XML));
    }
}
