@echo off
C:\projects\Apache\struts-overdrive\sandbox\overdrive\Nexus\postbuild.bat C:\projects\Apache\struts-overdrive\sandbox\overdrive\Nexus\Test\bin\Debug\  Nexus.Test  Nexus  C:\projects\Apache\struts-overdrive\sandbox\overdrive\Nexus\
if errorlevel 1 goto CSharpReportError
goto CSharpEnd
:CSharpReportError
echo Project error: A tool returned an error code from the build event
exit 1
:CSharpEnd