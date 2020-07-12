# This file is intended for web application development workflow
# Run install.ps1 once to deploy initial version of scheduler in this folder

# Use deploy-and-run.bat to copy over changed *.class files and other web-app 
# related files when doing development

pushd apache-tomcat-9.0.33\webapps\ROOT

pushd WEB-INF\classes
pushd scheduler
del *.class
copy ..\..\..\..\..\..\..\target\classes\scheduler\*.class .
popd
pushd services
del *.class
copy ..\..\..\..\..\..\..\target\classes\services\*.class .
popd
pushd servlets
del *.class
copy ..\..\..\..\..\..\..\target\classes\servlets\*.class .
popd
pushd util
del *.class
copy ..\..\..\..\..\..\..\target\classes\util\*.class .
popd
popd

pushd css
del *.css
copy ..\..\..\..\..\WebContent\css\*.css .
popd

pushd comm
del *.html
copy ..\..\..\..\..\WebContent\comm\*.html .
popd

pushd js
del *.js
copy ..\..\..\..\..\WebContent\js\*.js .
popd


del index.html
copy ..\..\..\..\WebContent\index.html .

del *.jsp
copy ..\..\..\..\WebContent\*.jsp .
popd

popd
run.bat
