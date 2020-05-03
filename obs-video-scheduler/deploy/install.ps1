Write-Host "Checking java installation..."

if ((-not (Test-Path env:JRE_HOME)) -and (-not (Test-Path env:JAVA_HOME))) {
  Write-Host "There's no JAVA_HOME or JRE_HOME set!"
  Write-Host "Install JDK or JRE and set JAVA_HOME or JRE_HOME appropriately"
  exit 1
}

if (Test-Path env:JRE_HOME) {
  Write-Host "Found JRE_HOME at $env:JRE_HOME"
  if (-not (Test-Path "$env:JRE_HOME\bin\java.exe")) {
    Write-Host "There's no bin\java.exe at $env:JRE_HOME"
    Write-Host "Fix your java installation"
    exit 1
  }
  Write-Host "Found java.exe at $env:JRE_HOME"
  Invoke-Expression "$env:JRE_HOME\bin\java.exe --version"
}

Write-Host "Checking ffmpeg installation..."
try {
  Invoke-Expression "ffmpeg -version"
} catch {
  Write-Host "There's no ffmpeg"
  Write-Host "Download ffmpeg and add it to your PATH"
  exit 1
}

Write-Host "Downloading tomcat..."
Invoke-WebRequest -Uri "https://downloads.apache.org/tomcat/tomcat-9/v9.0.34/bin/apache-tomcat-9.0.34-windows-x64.zip" -Outfile "tomcat.zip"
Write-Host "Unpacking tomcat..."
Expand-Archive tomcat.zip
Remove-Item tomcat.zip
