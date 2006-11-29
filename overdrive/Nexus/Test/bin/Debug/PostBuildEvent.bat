@echo off
C:\projects\Nexus\struts-overdrive\sandbox\overdrive\Nexus\postbuild.bat C:\projects\Nexus\struts-overdrive\sandbox\overdrive\Nexus\Test\bin\Debug\  Nexus.Test  Nexus  C:\projects\Nexus\struts-overdrive\sandbox\overdrive\Nexus\
if errorlevel 1 goto CSharpReportError
goto CSharpEnd
:CSharpReportError
echo Project error: A tool returned an error code from the build event
exit 1
:CSharpEnd