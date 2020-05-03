namespace py obsthriftserver
namespace java scheduler

struct SourceDimensions {
    1: i32 leftMargin = 0;
    2: i32 topMargin = 0;
    3: double relativeWidth = 1.0;
    4: double relativeHeight = 1.0;
}

service ObsThriftServer {
    void launchVideo(
        1:string path, 
        2:i32 layer, 
        3:string sceneName, 
        4:string sourceName, 
        5:SourceDimensions dimensions, 
        6:bool clearOnMediaEnd,
    ),
    void removeSource(1:string sceneName, 2:string sourceName),

    void muteSource(1:string sourceName),
    void unmuteSource(1:string sourceName),

    void heartbeat(),
}

