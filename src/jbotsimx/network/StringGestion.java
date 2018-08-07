package jbotsimx.network;

import jbotsim.Node;
import jbotsim.Topology;
import jbotsimx.ui.JViewer;

import java.util.ConcurrentModificationException;

public class StringGestion {
    volatile Topology topology;
    int id = 0;
    double x, y, z = 0;

    public StringGestion(Topology topology) {
        this.topology = topology;
    }

    public static void parseIntIP(String serverIp, IP ip) {
        try {
            ip.ip1 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
            serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

            ip.ip2 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
            serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

            ip.ip3 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
            serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

            ip.ip4 = Integer.parseInt(serverIp);
        } catch (Exception e) {
            System.out.println("USAGE : \n IP :  x.x.x.x port : x");
        }
    }

    public void traitementMessage(String message) {
        topology.pause();
        try {

            String[] lines = message.split("\r\n|\r|\n");
            if (message.contains("move")) {
                for (String line : lines) {
                    getProperties(line);
                    Node n = topology.findNodeById(id);
                    if (n != null) {
                        moveNode(id, x, y, z);
                    } else {
                        addNode(id, x, y, z);
                    }
                }
            } else if (message.contains("add")) {
                for (String line : lines) {
                    getProperties(line);
                    addNode(id, x, y, z);
                }
            } else if (message.contains("del")) {
                getProperties(message);
                delNode(id);
            } else if (message.contains("color")) {
                colorNode(message);
            } else if (message.contains("size")) {
                sizeNode(message);
            } else if (message.contains("cR") || message.contains("sR")) {
                topology.setCommunicationRange(Double.parseDouble(message.substring(message.indexOf(":") + 1, message.indexOf(";")).trim()));
                topology.setSensingRange(Double.parseDouble(message.substring(message.indexOf("sR :") + 4, message.indexOf("]")).trim()));
            }
        } catch (ConcurrentModificationException | NullPointerException ignored) {

        }
        topology.resume();
    }

    private void colorNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int color = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());

        topology.findNodeById(id).setIntColor(color);
        System.out.println(color);
    }

    private void sizeNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int size = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());
        topology.findNodeById(id).setSize(size);
    }


    private void getProperties(String message) {
        try {
            if (message.contains("[") && message.contains("id") && message.contains("x") && message.contains("y") && message.contains("z") && message.contains("]")
                    && !message.contains("sR") && !message.contains("cR") && !message.contains("color") && !message.contains("size")) {
                id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(", x")).trim());
                x = Double.parseDouble(message.substring(message.indexOf("x") + 3, message.indexOf(", y")).trim());
                y = Double.parseDouble(message.substring(message.indexOf("y") + 3, message.indexOf(", z")).trim());
                z = Double.parseDouble(message.substring(message.indexOf("z") + 3, message.indexOf("]")).trim());
            }
        } catch (StringIndexOutOfBoundsException ignored) {

        }
    }

    private void addNode(int id, double x, double y, double z) {
        if (topology.findNodeById(id) == null) {
            Node node = new Node();
            node.setID(id);
            node.setLocation(x, y, z);
            topology.addNode(node);
        }
    }

    private void delNode(int id) {
        if (topology.findNodeById(id) != null) {
            topology.removeNode(topology.findNodeById(id));
        }
    }

    private void moveNode(int id, double x, double y, double z) {

        topology.findNodeById(id).setLocation(x, y, z);

    }
}
