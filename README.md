# OBS Video Scheduler

Library and web application for managing pre-recorded videos playbacks in [Open Broadcaster Software](https://obsproject.com/) broadcasts.

## obs-thrift-api

OBS plugin exposing certain OBS functionality via [Thrift](https://thrift.apache.org/) - cross-language services development framework.

Functionality supported currently:
- adding media source to OBS scene
- removing media source from OBS
- muting/unmuting sources

For full API details see [thrift file](obs_thrift_server.thrift).

## obs-video-scheduler

Tomcat web application that enables scheduling of pre-recorded videos playbacks during OBS broadcast.

More details TBD.

## License
TBD

## Installation instructions
TBD

## Acknowledgements
Project was created for [ICPC](https://icpc.baylor.edu/), a largest worlwide college student programming competition.
It's been used during [ICPC Live](http://live.icpc.global/) broadcasts since 2016.

obs-video-scheduler is built based on [TimeSlider Plugin for jQuery](https://github.com/v-v-vishnevskiy/timeslider) distributed under The MIT License.

## Contacts
Reach out to me at krotkov.pavel@gmail.com
