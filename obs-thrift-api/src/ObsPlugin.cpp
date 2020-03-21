#include "libobs/obs-module.h"
#include "ObsThriftServerHandler.h"

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/server/TSimpleServer.h>
#include <thrift/transport/TServerSocket.h>
#include <thrift/transport/TBufferTransports.h>

#include <boost/thread.hpp>

using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;

OBS_DECLARE_MODULE()
OBS_MODULE_USE_DEFAULT_LOCALE("obs-thrift-api", "en-US")

MODULE_EXPORT const char* obs_module_description(void)
{
	return "Thrift API for managing videos";
}

bool obs_module_load(void)
{
    auto run_server = []() {
        int port = 9090;
        ::std::shared_ptr<ObsThriftServerHandler> handler(new ObsThriftServerHandler());
        ::std::shared_ptr<TProcessor> processor(new ObsThriftServerProcessor(handler));
        ::std::shared_ptr<TServerTransport> serverTransport(new TServerSocket(port));
        ::std::shared_ptr<TTransportFactory> transportFactory(new TBufferedTransportFactory());
        ::std::shared_ptr<TProtocolFactory> protocolFactory(new TBinaryProtocolFactory());

        TSimpleServer* server = new TSimpleServer(processor, serverTransport, transportFactory, protocolFactory);

        server->serve();
    };
    boost::thread t{ run_server };

    return true;
}