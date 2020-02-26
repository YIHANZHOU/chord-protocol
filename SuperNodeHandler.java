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

public class SuperNodeHandler implements SuperNodeThrift.Iface {

  // This is the node list the super node maintaines
  private ArrayList<NodeInfo> nodeList;
  // max number of node
  private int max;
  // One time, only one node can perform the join process
  Boolean busy = false;
  // lock for changing the busy value
  private static final Object lock = new Object();
  // If the superNode is ready for client request
  Boolean ready = false;
  // lock for changing the ready value
  private static final Object lockReady = new Object();

  // Constructor
  public SuperNodeHandler(int max) {
    // initially, the node list is empty
    nodeList = new ArrayList<NodeInfo>();
    this.max = max;
  }

  @Override
  public JoinReturn join(String ip,int port) {
    JoinReturn result = new JoinReturn();
    // test if this node already in the system
    for (int i=0; i<nodeList.size(); i++) {
      if (ip.equals(nodeList.get(i).ip) && port==nodeList.get(i).port) {
        System.out.println("Duplicate node join request. Do nothing");
        result.type = 4;
        return result;
      }
    }
    // first test if the superNode is already taken for node join
    synchronized(lock) {
      if (busy) {
        if (nodeList.size() == max) {
          result.type = 5;  // already 
        } else {
          result.type = 3;
        }
        return result;
      } else {
        busy = true;  // set the busy to be true so that other calls to join() will return
      }
    }
    // test if the node list is empty
    if (nodeList.size() == 0) {
      result.type = 2;
      return result;
    } else {
      // randomly pick one node in the node list and send it back to the node
      int randomIndex = randomInt(nodeList.size());
      result.type = 1;
      NodeInfo temp = nodeList.get(randomIndex);
      NodeInfo nodeInfo = new NodeInfo(temp.id, temp.ip, temp.port);
      result.nodeInfo = nodeInfo;
      return result;
    }
  }

  @Override
  public void postJoin(NodeInfo nodeInfo) {
    // one node has finished join process -> add this node into the node list
    NodeInfo newNode = new NodeInfo(nodeInfo.id, nodeInfo.ip, nodeInfo.port);
    nodeList.add(newNode);
    // test if the exsiting node reach the max number of node
    if (nodeList.size() == max) {
      synchronized(lockReady) {
        // system now is ready to accept client request
        ready = true;
      }
    } else {  // need more nodes to join
      // now release the join lock -> more nodes can join to the system
      synchronized(lock) {
        busy = false;
      }
    }
    // print out system info for DEBUG
    System.out.println("New Node "+newNode.ip+":"+newNode.port+" has joined the system");
    for (int i=0; i<nodeList.size(); i++) {
      System.out.println((i+1)+": "+nodeList.get(i).ip+":"+nodeList.get(i).port+" id:"+nodeList.get(i).id);
    }
  }

  @Override
  // client first contact super node to get a random node info
  public NodeInfo nodeInfo() {
    synchronized(lockReady) {
      if (!ready) {  // node join not finish
        return new NodeInfo("NOTREADY", "0", 0);
      }
    }
    int randomIndex = randomInt(nodeList.size());
    NodeInfo temp = nodeList.get(randomIndex);
    NodeInfo node = new NodeInfo(temp.id, temp.ip, temp.port);
    return node;
  }

  // helper function
  // Get the random number within the given range
  private int randomInt(int range) {  // range is the max value that cannot selected
    double randomDouble = Math.random();  // [0.0-1)
    randomDouble = randomDouble*range;
    return (int)randomDouble;
  }

}
