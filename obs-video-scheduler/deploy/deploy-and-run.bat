pushd apache-tomcat-9.0.33\webapps\ROOT

pushd WEB-INF\classes
pushd scheduler
del *.class
copy d:\git\obs-video-scheduler\obs-video-scheduler\target\classes\scheduler\*.class .
popd
pushd services
del *.class
copy d:\git\obs-video-scheduler\obs-video-scheduler\target\classes\services\*.class .
popd
pushd servlets
del *.class
copy d:\git\obs-video-scheduler\obs-video-scheduler\target\classes\servlets\*.class .
popd
pushd util
del *.class
copy d:\git\obs-video-scheduler\obs-video-scheduler\target\classes\util\*.class .
popd
popd

pushd css
del *.css
copy D:\git\obs-video-scheduler\obs-video-scheduler\WebContent\*.css .
popd


del index.html
copy D:\git\obs-video-scheduler\obs-video-scheduler\WebContent\index.html .

del settings.jsp
copy D:\git\obs-video-scheduler\obs-video-scheduler\WebContent\settings.jsp .

del OBSStatus.jsp
copy D:\git\obs-video-scheduler\obs-video-scheduler\WebContent\OBSStatus.jsp .

popd
run.bat
