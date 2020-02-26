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
import java.net.*;
import java.security.*;
import java.math.BigInteger;

public class NodeHandler implements NodeThrift.Iface {
  // those 2 structures stored in Class node; here are just 2 pointers pointing to them
  ArrayList<FingerEntry> fingerTable;
  NodeInfo predecessor;
  NodeInfo selfInfo;
  // Note: do not need lock here because at one time, only one node is joining the system where there will be no concurrent update

  // partial DHT data structure maintained by node
  HashMap<String, String> DHT;  // pair: <titleHash, genre>
  // sync operations on DHT
  private static final Object lock = new Object();

  // constructor
  public NodeHandler(ArrayList<FingerEntry> fingerTable, NodeInfo predecessor, NodeInfo selfInfo) {
    this.fingerTable = fingerTable;
    this.selfInfo = selfInfo;
    this.predecessor = predecessor;
    this.selfInfo = selfInfo;
    DHT = new HashMap<String, String>();
  }

  @Override
  public List<String> setPair(String title, String genre) {
    String titleHash = titleToHash(title);
    //
    NodeWithTrack nodeWithTrack = find_successor(titleHash);
    // build connection to this node and store the pair there
    NodeThrift.Client temp = connectNode(nodeWithTrack.nodeInfo.ip,nodeWithTrack.nodeInfo.port);
    try {
      temp.storeValue(titleHash, genre);
    } catch(TException e) {
      System.out.println("Call storeValue(titleHash, genre) fail");
      System.exit(0);
    }
    return nodeWithTrack.trackInfo;
  }
  @Override
  // given title, return genre of this title; if not exist, return null
  public ResultGet getPair(String title) {
    String titleHash = titleToHash(title);
    NodeWithTrack nodeWithTrack = find_successor(titleHash);
    // build connection to this node and store the pair there
    NodeThrift.Client temp = connectNode(nodeWithTrack.nodeInfo.ip,nodeWithTrack.nodeInfo.port);
    FetchReturn result = null;
    try {
      result = temp.fetchValue(titleHash);
    } catch(TException e) {
      System.out.println("Call fetchValue(titleHash) fail");
      System.exit(0);
    }
    if (result.type == 2) {  // if the title doesn't exist
      return new ResultGet(result.genre,nodeWithTrack.trackInfo,2);
    } else {
      return new ResultGet(result.genre,nodeWithTrack.trackInfo,1);
    }
  }
  @Override
  public void storeValue(String titleHash, String genre) {
    synchronized(lock) {
      DHT.put(titleHash, genre);  // add new pair; if exist -> replace
    }
    System.out.println("Add new pair: <"+titleHash+","+genre+">");
  }
  @Override
  public FetchReturn fetchValue(String titleHash) {
    String result = null;
    synchronized(lock) {
      result = DHT.get(titleHash);  // add new pair; if exist -> replace
    }
    if (result == null) {
      return new FetchReturn(2,"");
    } else {
      return new FetchReturn(1,result);
    }
  }

  @Override
  public NodeInfo getSuccessor() {  // return successor of itself
    // 1st entry is the successor
    return fingerTable.get(0).getNodeInfo();
  }
  @Override
  public NodeInfo getPredecessor() {  // return predecessor of itself
    return predecessor;
  }
  @Override
  public void setPredecessor(NodeInfo pre) {  // change predecessor
    predecessor.id = pre.id;
    predecessor.ip = pre.ip;
    predecessor.port = pre.port;
  }

  @Override
  public NodeWithTrack find_successor(String id) {
    NodeWithTrack temp = find_predecessor(id);
    // build connection to temp node and get its successor
    NodeThrift.Client tempNode = connectNode(temp.nodeInfo.ip, temp.nodeInfo.port);
    NodeInfo successor = null;
    try {
      successor = tempNode.getSuccessor();
    } catch(TException e) {
      System.out.println("Call getSuccessor() fail");
      System.exit(0);
    }
    temp.trackInfo.add(successor.id);
    return new NodeWithTrack(new NodeInfo(successor.id, successor.ip, successor.port), temp.trackInfo);
  }

  @Override
  public NodeWithTrack find_predecessor(String id) {
    // start with self
    NodeInfo temp = selfInfo;
    NodeInfo suc = getSuccessor();
    // srtucture used to track nodes
    List<String> track = new ArrayList<String>();
    track.add(temp.id);
    while( (!temp.id.equals(suc.id)) && (((compareHex(temp.id, suc.id)<=0) && (!((compareHex(id,temp.id)>0)&&(compareHex(id,suc.id)<=0)))) ||  ((compareHex(temp.id,suc.id)>0) && ((compareHex(id,suc.id)>0)&&(compareHex(id,temp.id)<=0)))) ) {
      // build the connection to this node and update the temp
      // System.out.println("asdf");
      NodeThrift.Client tempNode = connectNode(temp.ip,temp.port);
      try {
        temp = tempNode.closest_preceding_finger(id);
      } catch(TException e) {
        System.out.println("Call closest_preceding_finger() fail");
        System.exit(0);
      }
      // add this node to the track
      track.add(temp.id);
      // now connect to the updated temp and get its successor
      tempNode = connectNode(temp.ip, temp.port);
      try {
        suc = tempNode.getSuccessor();
      } catch(TException e) {
        System.out.println("Call getSuccessor() fail");
        System.exit(0);
      }
    }
    return new NodeWithTrack(new NodeInfo(temp.id, temp.ip, temp.port), track);
  }

  @Override
  // Based on finger table, find the own closest preceding entry (not global closest)
  public NodeInfo closest_preceding_finger(String id) {
    for (int i=127; i>=0; i--) {
      NodeInfo fe = fingerTable.get(i).getNodeInfo();
      if ((( compareHex(selfInfo.id, id)<=0 )&&( (compareHex(fe.id, selfInfo.id)>0)&&(compareHex(fe.id, id)<0) )) || (( compareHex(selfInfo.id, id)>0 )&&( (compareHex(fe.id, selfInfo.id)>0)||(compareHex(fe.id, id)<0) ))) {
        return new NodeInfo(fe.id, fe.ip, fe.port);
      }
    }
    return new NodeInfo(selfInfo.id, selfInfo.ip, selfInfo.port);
  }

  @Override
  public void update_finger_table(NodeInfo s, int i) {
    // if it jumps to itself, just stop
    if (compareHex(s.id,selfInfo.id)==0) {
      return;
    }
    String en = fingerTable.get(i-1).getNodeInfo().id;
    if ( (compareHex(selfInfo.id,en)==0) || ((( compareHex(selfInfo.id,en)<=0 )&&( (compareHex(s.id,selfInfo.id)>=0)&&(compareHex(s.id,en)<0) ))||(( compareHex(selfInfo.id,en)>0 )&&( (compareHex(s.id,selfInfo.id)>=0)||(compareHex(s.id,en)<0) )))) {
      // System.out.println("asdfasdfdsa");
      // update the entry
      fingerTable.get(i-1).setNodeInfo(new NodeInfo(s.id,s.ip,s.port));

      // showFingerTable(fingerTable);  // DEBUG
      // build the connection to predecessor and recursively call itself
      NodeThrift.Client pre = connectNode(predecessor.ip, predecessor.port);
      try {
        pre.update_finger_table(s,i);
      } catch(TException e) {
        System.out.println("Call getSuccessor() fail");
        System.exit(0);
      }
    }
  }


  // helper function
  // compare 2 hex number
  public int compareHex(String s1, String s2) {
    BigInteger b1 = hexToBig(s1);
    BigInteger b2 = hexToBig(s2);
    // s1==s2:0; s1>s2:1; s1<s2:-1
    return b1.compareTo(b2);
  }
  // convert BigInteger to 32 bit hex value
  public String bigToHex(BigInteger big) {
    String hashtext = big.toString(16);
    while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
    }
    return hashtext;
  }
  // convert hex value to BigInteger
  public BigInteger hexToBig(String hex) {
    return new BigInteger(hex, 16);
  }
  // based on the Ip and Port, build the connection to this node RPC server
  public NodeThrift.Client connectNode(String ip, int port) {
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
  // print fingerTable
  public void showFingerTable(ArrayList<FingerEntry> table) {
    for (int i=0; i<table.size(); i++) {
      FingerEntry en = table.get(i);
      System.out.println((i+1)+": "+en.getNodeInfo().ip+":"+en.getNodeInfo().port+"; NodeId:"+en.getNodeInfo().id+"; Start:"+en.getStart());
    }
  }
  // given title string, get title hash value
  public String titleToHash(String title) {
    // use MD5 hash function to get the ID of this Node
    String hashtext = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(title.getBytes());
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
}
