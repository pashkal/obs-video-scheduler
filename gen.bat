thrift.exe --gen cpp:no_skeleton -out obs-thrift-api/obs-thrift-api/src/ obs_thrift_server.thrift
thrift.exe --gen py -out obs-thrift-api/tests/ obs_thrift_server.thrift
thrift.exe --gen java -out java/ obs_thrift_server.thrift
