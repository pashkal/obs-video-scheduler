#ifndef ObsThriftServerHandler_H
#define ObsThriftServerHnandler_H

#include "ObsThriftServer.h"

class ObsThriftServerHandler : virtual public ObsThriftServerIf {
public:
    ObsThriftServerHandler();

    void launchVideo(const std::string& path, const int32_t layer, const std::string& sceneName, const std::string& sourceName, const SourceDimensions& dimensions);

    void removeSource(const std::string& sceneName, const std::string& sourceName);

    void muteSource(const std::string& sourceName);

    void unmuteSource(const std::string& sourceName);
};

#endif