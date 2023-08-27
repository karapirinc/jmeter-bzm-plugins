package com.blazemeter.jmeter.xmpp;


import com.blazemeter.jmeter.xmpp.actions.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.bosh.BOSHConfiguration;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JMeterXMPPConnection extends JMeterXMPPConnectionBase {
    private static final Logger log = LoggingManager.getLoggerForClass();
    private static final BlockingQueue<XMPPConnection> connectionRegistry = new LinkedBlockingQueue<>();
    private XMPPConnection conn;
    private Map<String, AbstractXMPPAction> actions = getAvailableActions();

    public JMeterXMPPConnection() {
        super();
    }


    @Override
    public void testEnded(String host) {
        log.debug("Test ended: " + host);
        for (XMPPConnection conn : connectionRegistry) {
            connectionRegistry.remove(conn);
            if (conn.isConnected()) {
                log.debug("Disconnecting: " + conn.getStreamId());
                if (conn instanceof AbstractXMPPConnection) {
                    ((AbstractXMPPConnection) conn).disconnect();
                }
            }
        }
    }

    /**
     * Creates new connection or returns previously created
     *
     * @return XMPPConnection
     */
    public XMPPConnection getConnection() throws NoSuchAlgorithmException, KeyManagementException, SmackException, InterruptedException, XmppStringprepException, UnknownHostException {
        if (conn == null) {
            String address = getAddress();
            String serv_name = getServiceName();
            if (serv_name.isEmpty()) serv_name = address;
            int port = Integer.parseInt(getPort());

            log.debug("Creating connection: " + address + ":" + port + "/" + serv_name);

            XMPPConnection newConn;
            if (Type.valueOf(getConnectionType()) == Type.BOSH) {
                BOSHConfiguration conf = BOSHConfiguration.builder()
                        .setUseHttps(isBOSHSSL()).setHost(address).setPort(port).setFile(getBOSHURL()).setXmppDomain(serv_name)
                        .setCustomSSLContext(getSSLContext()).setSendPresence(false).build();
                newConn = new XMPPBOSHConnection(conf);
            } else {
                XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder().setHostAddress(InetAddress.getByName(address)).setPort(port).setXmppDomain(serv_name).setSendPresence(false).setCustomSSLContext(getSSLContext()).build();

                newConn = new XMPPTCPConnection(conf);
            }

            connectionRegistry.put(newConn);
            setUpConnection(newConn);
        }

        return conn;
    }

    private void setUpConnection(XMPPConnection newConn) {
        conn = newConn;
        conn.setReplyTimeout(Integer.parseInt(getPacketReplyTimeout()));
        conn.setFromMode(getFromMode());

        if (log.isDebugEnabled()) {
            conn.addConnectionListener(new Loggers.LogConn(conn));
            conn.addStanzaListener(new Loggers.LogRecv(conn), new AndFilter());
            conn.addStanzaSendingListener(new Loggers.LogSent(conn), new AndFilter());
        }

        for (AbstractXMPPAction action : actions.values()) {
            if (action instanceof StanzaListener) {
                conn.addStanzaListener((StanzaListener) action, action.getStanzaFilter());
            }
            if (action instanceof ConnectionListener) {
                conn.addConnectionListener((ConnectionListener) action);
            }
        }
    }

    public static Map<String, AbstractXMPPAction> getAvailableActions() {
        Map<String, AbstractXMPPAction> actions = new TreeMap<>(new OrderComparator());

        try {
            for (String cls : JMeterUtils.findClassesThatExtend(AbstractXMPPAction.class)) {
                actions.put(cls, (AbstractXMPPAction) Class.forName(cls).newInstance());
            }
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("Error loading actions", e);
        }

        return actions;
    }

    public Map<String, AbstractXMPPAction> getActions() {
        return actions;
    }

    @Override
    public void resetConnection() {
        connectionRegistry.remove(conn);
        conn = null;
    }

    private static class OrderComparator implements Comparator<String>, Serializable {
        private static final List<String> fixed = Arrays.asList(
                Connect.class.getCanonicalName(),
                Login.class.getCanonicalName(),
                RosterAction.class.getCanonicalName(),
                SendPresence.class.getCanonicalName(),
                SendMessage.class.getCanonicalName(),
                RawXML.class.getCanonicalName(),
                NoOp.class.getCanonicalName(),
                Disconnect.class.getCanonicalName()
        );

        @Override
        public int compare(String s1, String s2) {
            if (s1.equals(s2)) return 0;

            int i1 = fixed.indexOf(s1);
            int i2 = fixed.indexOf(s2);

            if (i1 < 0 && i2 < 0)
                return s1.compareTo(s2);
            else if (i1 < 0)
                return 1;
            else if (i2 < 0)
                return -1;
            else
                return i1 > i2 ? 1 : -1;
        }
    }
}
