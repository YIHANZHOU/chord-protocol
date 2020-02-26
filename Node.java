import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.security.*;
import java.math.BigInteger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;


class Node {
  // stuff used for open RPC server
  public static NodeHandler handler;
  public static NodeThrift.Processor processor;
  // Data structure maintained by this node
  static ArrayList<FingerEntry> fingerTable = new ArrayList<FingerEntry>();  // finger table
  static NodeInfo predecessor;  // pointer to predecessor

  public static void main(String [] args) {
    // Take command line parameters
    if(args.length != 3) {
      System.out.println("Parameters are not correct: in the format [port] [superIp] [superPort]");
      return;
    }
    // SuperNode address
    String superIp = args[1];
    int superPort = Integer.parseInt(args[2]);
    // Address of this Node server
    String ip = getHostIp();
    int port = Integer.parseInt(args[0]);
    // Using ip and port info to get the hash value (ID) of this node
    String id = getId(ip, port);
    System.out.println("======================================================");
    System.out.println("This node: "+id);  // DEBUG
    System.out.println("======================================================");
    // first contact to superNode to start node join process (join)
    SuperNodeThrift.Client superNode = connectSuperNode(superIp, superPort);
    JoinReturn joinReturn = null;
    try {
      joinReturn = superNode.join(ip, port);  // call join() of SuperNode
    } catch(TException e) {
      System.out.println("Call join() fail");
      System.exit(0);
    }

    // Node join process
    if(joinReturn.type == 2) {
      // this node is the first node in the system
      for (int i=1; i<=128; i++) {  // add all 128 fingerTable entries
        BigInteger startNum = (hexToBig(id).add((BigInteger.valueOf(2)).pow(i-1))).mod((BigInteger.valueOf(2)).pow(128));
        String start = bigToHex(startNum);
        NodeInfo nodeInfo = new NodeInfo(id, ip, port);
        FingerEntry entry = new FingerEntry(start, nodeInfo);
        fingerTable.add(entry);
      }
      // update the predecessor
      predecessor = new NodeInfo(id, ip, port);
      System.out.println("Initiate finger table done");
      startNodeServer(id, ip, port);
    } else if (joinReturn.type == 3) {
      // cannot join right now
      System.out.println("SuperNode is busy right now. Please try later");
      return;
    } else if (joinReturn.type == 4) {
      System.out.println("Duplicate port!! check your parameters!");
      return;
    } else if (joinReturn.type == 5) {
      System.out.println("System already reaches the max node");
    } else {  // type == 1: success
      // there is already existing node in the system
      NodeInfo randomNode = joinReturn.nodeInfo;
      // based on the return node, build its finger table
      init_finger_table(randomNode, new NodeInfo(id, ip, port));
      System.out.println("Initiate finger table done");
      // then start the server function to accept concurrent query from the client
      // this should be before the postJoin to make sure everything about this node is ready
      startNodeServer(id, ip, port);
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }

      // update the finger tables in other nodes
      update_others(new NodeInfo(id, ip, port));
      System.out.println("Update other nodes done");
    }
    System.out.println("Node Server is running on port: "+port);
    // showFingerTable(fingerTable);  // DEBUG

    // at this point, this node has finished the join operation -> tell super node it's done -> call postJoin()
    try {
      superNode.postJoin(new NodeInfo(id, ip, port));
    } catch(TException e) {
      System.out.println("Call postJoin() fail");
      System.exit(0);
    }

  }

  public static void simple(NodeThrift.Processor processor, int port) {
    try {
      // Create Thrift server socket
      TServerTransport serverTransport = new TServerSocket(port);
      TTransportFactory factory = new TFramedTransport.Factory();

      //Set multi-thread server arguments
      TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
      args.processor(processor);
      args.transportFactory(factory);

      //Run SuperNode as multi-thread server
      TServer server = new TThreadPoolServer(args);
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // build the connection to the existing node and build the finger table
  public static void init_finger_table(NodeInfo temp, NodeInfo self) {
    System.out.println("Get random node: "+temp.id);  // DEBUG
    // finger[1].start
    String start = bigToHex((hexToBig(self.id).add((BigInteger.valueOf(2)).pow(0))).mod((BigInteger.valueOf(2)).pow(128)));
    // first connect the existing node to get the direct successor
    NodeThrift.Client tempNode = connectNode(temp.ip, temp.port);
    NodeWithTrack nodeWithTrack = null;
    try {
      nodeWithTrack = tempNode.find_successor(start);
    } catch(TException e) {
      System.out.println("Call find_successor fail");
      System.exit(0);
    }
    NodeInfo suc = nodeWithTrack.nodeInfo;
    // add the successor to the fingerTable
    fingerTable.add(new FingerEntry(start, new NodeInfo(suc.id, suc.ip, suc.port)));
    // connect to successor to get the predecessor
    NodeThrift.Client sucNode = connectNode(suc.ip, suc.port);
    NodeInfo t = null;
    try{
      t = sucNode.getPredecessor();
    } catch(TException e) {
      System.out.println("Call getPredecessor() fail");
      System.exit(0);
    }
    // update predecessor
    predecessor = new NodeInfo(t.id, t.ip, t.port);
    // change the successor's predecessor to the current node
    try {
      sucNode.setPredecessor(self);
    } catch(TException e) {
      System.out.println("Call setPredecessor() fail");
      System.exit(0);
    }

    // update the rest finger entries
    for (int i=0; i<127; i++) {
      String s = bigToHex((hexToBig(self.id).add((BigInteger.valueOf(2)).pow(i+1))).mod((BigInteger.valueOf(2)).pow(128)));
      String iId = fingerTable.get(i).getNodeInfo().id;
      // modified!!
      if ((( compareHex(self.id, iId)<=0 )&&( (compareHex(s,self.id)>0)&&(compareHex(s,iId)<=0) ))||(( compareHex(self.id, iId)>0 )&&( (compareHex(s,self.id)>0)||(compareHex(s,iId)<=0) ))) {
        NodeInfo t1 = fingerTable.get(i).getNodeInfo();
        fingerTable.add(new FingerEntry(s, new NodeInfo(t1.id, t1.ip, t1.port)));
      } else {
        try {
          nodeWithTrack = tempNode.find_successor(s);
        } catch(TException e) {
          System.out.println("Call find_successor() fail");
          System.exit(0);
        }
        suc = nodeWithTrack.nodeInfo;
        // if this suc is after this new joining node, then simply let it pointing to this new node (this enty:self -> self)
        if (compareHex(suc.id, fingerTable.get(0).getNodeInfo().id)==0) {
          fingerTable.add(new FingerEntry(s, new NodeInfo(self.id, self.ip, self.port)));
        } else {
          fingerTable.add(new FingerEntry(s, new NodeInfo(suc.id, suc.ip, suc.port)));
        }

        // fingerTable.add(new FingerEntry(s, new NodeInfo(suc.id, suc.ip, suc.port)));
      }
    }
  }

  // update the finger tables in other nodes (build connections with them)
  public static void update_others(NodeInfo self) {
    for (int i=1; i<=128; i++) {
      // !!gai!!
      String targetId = bigToHex((hexToBig(self.id).add(BigInteger.valueOf(2).pow(128)).subtract(BigInteger.valueOf(2).pow(i-1))).mod(BigInteger.valueOf(2).pow(128)));
      NodeWithTrack p = handler.find_predecessor(targetId);
      // build connection to p and call update_finger_table function
      NodeThrift.Client pNode = connectNode(p.nodeInfo.ip, p.nodeInfo.port);
      try{
        pNode.update_finger_table(self, i);
      } catch(TException e) {
        System.out.println("Call update_finger_table() fail");
        System.exit(0);
      }
    }
  }

  // helper functions
  // helper 0) compare 2 hex string
  public static int compareHex(String s1, String s2) {
    BigInteger b1 = hexToBig(s1);
    BigInteger b2 = hexToBig(s2);
    // s1==s2:0; s1>s2:1; s1<s2:-1
    return b1.compareTo(b2);
  }
  // helper 1) convert BigInteger to 32 bit hex value
  public static String bigToHex(BigInteger big) {
    String hashtext = big.toString(16);
    while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
    }
    return hashtext;
  }
  // helper 2) convert hex value to BigInteger
  public static BigInteger hexToBig(String hex) {
    return new BigInteger(hex, 16);
  }
  // helper 3) print out the finger table
  public static void showFingerTable(ArrayList<FingerEntry> table) {
    for (int i=0; i<table.size(); i++) {
      FingerEntry en = table.get(i);
      System.out.println((i+1)+": "+en.getNodeInfo().ip+":"+en.getNodeInfo().port+"; NodeId:"+en.getNodeInfo().id+"; Start:"+en.getStart());
    }
  }
  // helper 4) get ip address
  public static String getHostIp() {
    String ip = null;
    try {
      URL whatismyip = new URL("http://checkip.amazonaws.com");
      BufferedReader in = new BufferedReader(new InputStreamReader(
                      whatismyip.openStream()));
      ip = in.readLine(); //you get the IP as a String
    } catch(Exception e) {
      System.out.println("Get IP Error");
      System.exit(0);
    }
    return ip;
  }
  // helper 5) using ip and port info to calculate the ID (hash value)
  public static String getId(String ip, int port) {
    String text = ip+":"+port;
    // System.out.println(text);
    // use MD5 hash function to get the ID of this Node
    String hashtext = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(text.getBytes());
      // Convert byte array into signum representation
      BigInteger no = new BigInteger(1, messageDigest);
      // Convert message digest into hex value
      hashtext = bigToHex(no);
    }
    catch (Exception e) {
      System.out.println("Error hashing");
      System.exit(0);
    }
    return hashtext;
  }
  // helper 6) based on the Ip and Port, build the connection to this RPC server
  public static SuperNodeThrift.Client connectSuperNode(String ip, int port) {
    SuperNodeThrift.Client client = null;
    try {
      TTransport  transport = new TSocket(ip, port);
      TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
      client = new SuperNodeThrift.Client(protocol);
      //Try to connect
      transport.open();
    } catch(TException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return client;
  }
  // helper 7)
  // based on the Ip and Port, build the connection to this node RPC server
  public static NodeThrift.Client connectNode(String ip, int port) {
    NodeThrift.Client client = null;
    try {
      TTransport transport = new TSocket(ip, port);
      TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
      client = new NodeThrift.Client(protocol);
      //Try to connect
      transport.open();
    } catch(TException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return client;
  }
  // helper 8)
  // start node server
  public static void startNodeServer(String id, String ip, int port) {
    try {
      handler = new NodeHandler(fingerTable, predecessor, new NodeInfo(id, ip, port));
      processor = new NodeThrift.Processor(handler);
      Runnable simple = new Runnable() {
        public void run() {
            simple(processor, port);
        }
      };
      // start the service
      new Thread(simple).start();
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

}

// return value of join(); if empty then return null
// Key space has 128 bits: 128 entries in the finger table
class FingerEntry {
  String start;  // start key value of interval
  NodeInfo nodeInfo;  // ip and port of successor node
  public FingerEntry(String start, NodeInfo nodeInfo) {
    this.start = start;
    this.nodeInfo = nodeInfo;
  }
  public void setStart(String start) {this.start = start;}
  public void setNodeInfo(NodeInfo nodeInfo) {this.nodeInfo = nodeInfo;}
  public String getStart() {return start;}
  public NodeInfo getNodeInfo() {return nodeInfo;}
}

// class NodeInfo {
//   String id;  // key of the node; also the
//   String ip;
//   int port;
//   public NodeInfo(String id, String ip, int port) {
//     this.id = id;
//     this.ip = ip;
//     this.port = port;
//   }
//   public void setId(String id) {this.id = id;}
//   public void setIp(String ip) {this.ip = ip;}
//   public void setPort(int port) {this.port = port;}
//   public String getId() {return id;}
//   public String getIp() {return ip;}
//   public int getPort() {return port;}
// }
//
// class JoinReturn {  // structure to store the return value of the join() function
//   int type;  // 1:success; 2:list empty; 3:SuperNode taken
//   NodeInfo nodeInfo;
//   public JoinReturn(int type, NodeInfo nodeInfo) {
//     this.type = type;
//     this.nodeInfo = nodeInfo;
//   }
//   public void setType(int type) {this.type = type;}
//   public void setNodeInfo(NodeInfo nodeInfo) {this.nodeInfo = nodeInfo;}
//   public int getType() {return type;}
//   public NodeInfo getNodeInfo() {return nodeInfo;}
// }
