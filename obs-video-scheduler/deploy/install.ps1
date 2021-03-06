Write-Host "Checking java installation..."

if ((-not (Test-Path env:JRE_HOME)) -and (-not (Test-Path env:JAVA_HOME))) {
  Write-Host "There's no JRE_HOME or JAVA_HOME set!"
  Write-Host "Install JDK or JRE and set JAVA_HOME or JRE_HOME appropriately"
  exit 1
}

if (Test-Path env:JRE_HOME) {
  Write-Host "Found JRE_HOME at $env:JRE_HOME"
  if (-not (Test-Path "$env:JRE_HOME\bin\java.exe")) {
    Write-Host "There's no $env:JRE_HOME\bin\java.exe"
    Write-Host "Fix your java installation"
    exit 1
  }
  Write-Host "Found  $env:JRE_HOME\bin\java.exe"
  Invoke-Expression "& '$env:JRE_HOME\bin\java.exe' --version"
} else {
  if (Test-Path env:JAVA_HOME) {
    Write-Host "JRE_HOME is not set, but found JAVA_HOME at $env:JAVA_HOME"
    if (-not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
      Write-Host "There's no $env:JAVA_HOME\bin\java.exe"
      Write-Host "Fix your java installation"
      exit 1
    }
    Write-Host "Found $env:JAVA_HOME\bin\java.exe"
    Invoke-Expression "& '$env:JAVA_HOME\bin\java.exe' --version"
  }
}

Write-Host "Checking ffmpeg installation..."
try {
  Invoke-Expression "ffmpeg -version"
} catch {
  Write-Host "There's no ffmpeg"
  Write-Host "Download ffmpeg and add it to your PATH"
  exit 1
}

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

Write-Host "Downloading tomcat..."
Invoke-WebRequest -Uri "https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.43/bin/apache-tomcat-9.0.43-windows-x64.zip" -Outfile "tomcat.zip"
Write-Host "Unpacking tomcat..."
Expand-Archive -DestinationPath "." "tomcat.zip"
Remove-Item tomcat.zip

Write-Host "Downloading scheduler..."
Invoke-WebRequest -Uri "https://github.com/pashkal/obs-video-scheduler/releases/download/0.1.3/obs-video-scheduler.zip" -Outfile "scheduler.zip"
Write-Host "Unpacking scheduler..."
Expand-Archive -DestinationPath "." "scheduler.zip"
Remove-Item scheduler.zip

Write-Host "Setting up web app..."
Remove-Item -Recurse apache-tomcat-9.0.43\webapps
New-Item apache-tomcat-9.0.43\webapps -ItemType directory
Move-Item -Path ROOT.war -Destination "apache-tomcat-9.0.43\webapps\ROOT.war"

Write-Host "Setting up schedules dir..."
New-Item data\schedules -ItemType directory
