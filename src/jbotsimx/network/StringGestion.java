package jbotsimx.network;

import jbotsim.Node;
import jbotsimx.ui.JViewer;

public class StringGestion {
    JViewer jViewer;
    int id = 0;
    double x,y,z = 0;
    public StringGestion(JViewer jViewer){
        this.jViewer = jViewer;
    }
    public static void parseIntIP(String serverIp, int ip1, int ip2, int ip3, int ip4) {
        ip1 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip2 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip3 = Integer.parseInt(serverIp.substring(0, serverIp.indexOf(".")));
        serverIp = serverIp.substring(serverIp.indexOf(".") + 1, serverIp.length());

        ip4 = Integer.parseInt(serverIp);
    }

    public void traitementMessage(String message) {
        String[] lines = message.split("\r\n|\r|\n");
        if (message.contains("move")) {
            for (String line : lines) {
                getProperties(line);
                Node n = jViewer.getJTopology().getTopology().findNodeById(id);
                if (n != null) {
                    moveNode(id, x, y, z);
                } else {
                    addNode(id, x, y, z);
                }
                //getProperties(message);
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
            jViewer.getJTopology().getTopology().setCommunicationRange(Double.parseDouble(message.substring(message.indexOf(":") + 1, message.indexOf(";")).trim()));
            jViewer.getJTopology().getTopology().setSensingRange(Double.parseDouble(message.substring(message.indexOf("sR :") + 4, message.indexOf("]")).trim()));
        }
    }

    private void colorNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int color = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());

        jViewer.getJTopology().getTopology().findNodeById(id).setIntColor(color);
        System.out.println(color);
    }

    private void sizeNode(String message) {
        id = Integer.parseInt(message.substring(message.indexOf("id") + 5, message.indexOf(",")).trim());
        int size = Integer.parseInt(message.substring(message.indexOf(","), message.indexOf("]")).trim());
        jViewer.getJTopology().getTopology().findNodeById(id).setSize(size);
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
        if (jViewer.getJTopology().getTopology().findNodeById(id) == null) {
            Node node = new Node();
            node.setID(id);
            node.setLocation(x, y, z);
            jViewer.getJTopology().getTopology().addNode(node);
        }
    }

    private void delNode(int id) {
        if (jViewer.getJTopology().getTopology().findNodeById(id) != null) {
            jViewer.getJTopology().getTopology().removeNode(jViewer.getJTopology().getTopology().findNodeById(id));
        }
    }

    private void moveNode(int id, double x, double y, double z) {
        jViewer.getJTopology().getTopology().findNodeById(id).setLocation(x, y, z);
    }
}
