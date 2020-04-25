#include "ObsThriftServerHandler.h"

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/server/TSimpleServer.h>
#include <thrift/transport/TServerSocket.h>
#include <thrift/transport/TBufferTransports.h>

#include "libobs/obs-module.h"

struct RemoveSourceData {
    const char* sourceName;
};

struct AddSourceData {
    obs_source_t* source;
    int layer;
    const obs_transform_info *transformInfo;
};

ObsThriftServerHandler::ObsThriftServerHandler() {
}

    
void ObsThriftServerHandler::launchVideo(
    const std::string& path, 
    const int32_t layer, 
    const std::string& sceneName, 
    const std::string& sourceName, 
    const SourceDimensions& dimensions
) {
    auto addSource = [](void* _data, obs_scene_t* scene) {
        AddSourceData* data = (AddSourceData*)_data;
        obs_sceneitem_t* sceneItem = obs_scene_add(scene, data->source);
        obs_sceneitem_set_visible(sceneItem, true);
        for (int i = 0; i < data->layer; i++) {
            obs_sceneitem_set_order(sceneItem, OBS_ORDER_MOVE_DOWN);
        }
        obs_sceneitem_set_info(sceneItem, data->transformInfo);
    };

    AddSourceData data;
    data.layer = layer;
    
    obs_data_t* sourceData = obs_data_create();
    obs_data_set_string(sourceData, "local_file", path.c_str());
    obs_data_set_bool(sourceData, "clear_on_media_end", true);
    obs_source_t* newSource = obs_source_create("ffmpeg_source", sourceName.c_str(), sourceData, nullptr);
    obs_source_set_monitoring_type(newSource, OBS_MONITORING_TYPE_MONITOR_AND_OUTPUT);
    data.source = newSource;

    obs_video_info ovi;
    obs_get_video_info(&ovi);
    obs_transform_info transformInfo;
    transformInfo.rot = 0.0f;
    transformInfo.bounds_type = OBS_BOUNDS_STRETCH;
    transformInfo.bounds_alignment = OBS_ALIGN_CENTER;
    transformInfo.alignment = OBS_ALIGN_LEFT | OBS_ALIGN_TOP;
    vec2_set(&transformInfo.scale, 1.0f, 1.0f);
    vec2_set(&transformInfo.pos, dimensions.leftMargin, dimensions.topMargin);
    vec2_set(&transformInfo.bounds, ovi.base_width * dimensions.relativeWidth, ovi.base_height * dimensions.relativeHeight);
    data.transformInfo = &transformInfo;
    
    obs_source_t* sceneSource = obs_get_source_by_name(sceneName.c_str());
    obs_scene_t* scene = obs_scene_from_source(sceneSource);
    obs_scene_atomic_update(scene, addSource, &data);

    obs_source_release(sceneSource);
    obs_source_release(newSource);
}

void ObsThriftServerHandler::removeSource(const std::string& sceneName, const std::string& sourceName) {
   auto removeSource = [](void* _data, obs_scene_t* scene) {
        RemoveSourceData* data = (RemoveSourceData*)_data;
        obs_sceneitem_t* item = obs_scene_find_source(scene, data->sourceName);
        obs_sceneitem_remove(item);
        obs_sceneitem_release(item);
    };
    
    obs_source_t* scene_source = obs_get_source_by_name(sceneName.c_str());
    obs_scene_t* scene = obs_scene_from_source(scene_source);
    RemoveSourceData data;
    data.sourceName = sourceName.c_str();
    obs_scene_atomic_update(scene, removeSource, &data);
    obs_source_release(scene_source);
}

void ObsThriftServerHandler::muteSource(const std::string& sourceName) {
    obs_source_t* source = obs_get_source_by_name(sourceName.c_str());
    obs_source_set_muted(source, true);
    obs_source_release(source);
}

void ObsThriftServerHandler::unmuteSource(const std::string& sourceName) {
    obs_source_t* source = obs_get_source_by_name(sourceName.c_str());
    obs_source_set_muted(source, false);
    obs_source_release(source);
}

void ObsThriftServerHandler::heartbeat() {

}
