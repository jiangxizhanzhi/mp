@echo off
for /F %%i in ('hostname') do ( set hostName=%%i)

echo hostName:%hostName%