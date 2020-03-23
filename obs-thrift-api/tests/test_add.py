import sys
sys.path.append("..\\thrift\\lib\\py\\build\\lib.win-amd64-3.8\\")

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

from obsthriftserver import *
from obsthriftserver.ttypes import SourceDimensions;

try:
  transport = TSocket.TSocket('localhost', 9090)
  transport = TTransport.TBufferedTransport(transport)
  protocol = TBinaryProtocol.TBinaryProtocol(transport)
  client = ObsThriftServer.Client(protocol)

  transport.open()
  client.launchVideo('D:\\videos\\End Credits.mp4', 2, "Scene 1", "Layer 2", SourceDimensions(0, 0, 1.0, 1.0));
  transport.close()

except Exception as tx:
  print(tx)