@echo off
E:\projects\Apache\struts-current\sandbox\overdrive\Nexus\postbuild.bat E:\projects\Apache\struts-current\sandbox\overdrive\Nexus\Test\bin\Debug\  Nexus.Test  Nexus  E:\projects\Apache\struts-current\sandbox\overdrive\Nexus\
if errorlevel 1 goto CSharpReportError
goto CSharpEnd
:CSharpReportError
echo Project error: A tool returned an error code from the build event
exit 1
:CSharpEnd