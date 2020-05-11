# OBS Video Scheduler

Library and web application for managing pre-recorded videos playbacks in [Open Broadcaster Software](https://obsproject.com/) broadcasts.

## obs-video-scheduler

Tomcat web application that enables scheduling of pre-recorded videos playbacks during OBS broadcast and creating a video plan ahead of the broadcast. Scheduled videos will automatically start in the correct layer.

Currently two interfaces are provided:
- web interface for schedule management and settings (http://localhost:8080)
- web interface with upcoming video announcements (http://localhost:8080/comm) that can be used by commentators or broadcast director

## obs-thrift-api

OBS plugin for integrating the web application with OBS.

Plugin exposes certain OBS functionality via [Thrift](https://thrift.apache.org/) - cross-language services development framework.

Functionality supported currently:
- adding media source to OBS scene
- removing media source from OBS
- muting/unmuting sources

For full API details see [thrift file](obs_thrift_server.thrift).

## Installation and usage
Currently the only supported platform is Windows and 64-bit OBS. It's likely that it can be easily ported to other platforms.

See detailed installation and usage instructions [here](docs/INSTALL.md).

## License
TBD

## Acknowledgements
Project was created for [ICPC](https://icpc.baylor.edu/), the largest worlwide college student programming competition.
It's been used during [ICPC Live](http://live.icpc.global/) broadcasts since 2016.

obs-video-scheduler is built based on [TimeSlider Plugin for jQuery](https://github.com/v-v-vishnevskiy/timeslider) distributed under The MIT License.

## Contacts
Reach out to me at krotkov.pavel@gmail.com
