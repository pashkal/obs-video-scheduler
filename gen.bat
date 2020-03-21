thrift.exe --gen cpp:no_skeleton -out obs-thrift-api/src/ obs_thrift_server.thrift
thrift.exe --gen py -out tests/ obs_thrift_server.thrift
