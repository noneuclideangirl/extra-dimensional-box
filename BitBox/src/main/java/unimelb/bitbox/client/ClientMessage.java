package unimelb.bitbox.client;

import org.apache.commons.cli.CommandLine;
import unimelb.bitbox.util.Document;
import unimelb.bitbox.util.HostPort;

/**
 * A message that can be sent by the Client.
 */
public class ClientMessage {
    private final Document document = new Document();

    /**
     * Given a set of command line options, produces the appropriate message to send.
     * @param opts the command line options
     * @return the generated message
     * @throws IllegalArgumentException in case the options are incorrectly formatted
     */
    public static ClientMessage generateMessage(CommandLine opts)
        throws IllegalArgumentException {
        String command = opts.getOptionValue("c");
        if (command == null) {
            throw new IllegalArgumentException("missing command line option: -c");
        }

        switch (command) {
            case "list_peers":
                return new ListPeersMessage();
            case "connect_peer":
                return new ConnectPeerMessage(opts.getOptionValue("p"));
            case "disconnect_peer":
                return new DisconnectPeerMessage(opts.getOptionValue("p"));
            default:
                throw new IllegalArgumentException("invalid command: " + command);
        }
    }

    /**
     * Constructor. Intended for use only by subclasses.
     * @param command the command to send
     */
    protected ClientMessage(String command) {
        document.append("command", command);
    }

    /**
     * Adds host and port information, where appropriate.
     * @param hostPort the object to extract the information from
     */
    protected void appendHostPort(HostPort hostPort) {
        document.append("host", hostPort.hostname);
        document.append("port", hostPort.port);
    }

    /**
     * Encodes the message as JSON.
     * @return the encoded message
     */
    String encoded() {
        return document.toJson();
    }
}

class ListPeersMessage extends ClientMessage {
    ListPeersMessage() {
        super("LIST_PEERS_REQUEST");
    }
}

class PeerMessage extends ClientMessage {
    public PeerMessage(String command, String peerAddress)
            throws IllegalArgumentException {
        super(command);

        if (peerAddress == null) {
            throw new IllegalArgumentException("missing command line option: -p");
        }
        HostPort hostPort = new HostPort(peerAddress);
        appendHostPort(hostPort);
    }
}

class ConnectPeerMessage extends PeerMessage {
    public ConnectPeerMessage(String peerAddress) throws IllegalArgumentException {
        super("CONNECT_PEER_REQUEST", peerAddress);
    }
}

class DisconnectPeerMessage extends PeerMessage {
    public DisconnectPeerMessage(String peerAddress) throws IllegalArgumentException {
        super("DISCONNECT_PEER_REQUEST", peerAddress);
    }
}