import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

public class SuperNode {
  public static SuperNodeHandler handler;
  public static SuperNodeThrift.Processor processor;

  public static void main(String [] args) {
    // take the paramenters from command line
    if(args.length != 2) {
      System.out.println("Parameters are not correct: in the format [port] [max]");
      return;
    }
    int port = Integer.parseInt(args[0]);
    int max = Integer.parseInt(args[1]);
    try {
      handler = new SuperNodeHandler(max);
      processor = new SuperNodeThrift.Processor(handler);
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

  public static void simple(SuperNodeThrift.Processor processor, int port) {
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
      System.out.println("SuperNode is running on port: "+port);
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
