set PASSWORD=GwFZdWXw9qEDdHQB
set WORK_DIR=%cd%

call %WORK_DIR%\gradlew.bat :mp:bootJar
java -jar "%WORK_DIR%\classfinal-fatjar-1.2.1.jar" -file "%WORK_DIR%\mp\build\libs\mp-0.0.1.jar" -libjars mp-aggregation-0.0.1-plain.jar,mp-auth-0.0.1-plain.jar,mp-auth-base-0.0.1-plain.jar,mp-base-0.0.1-plain.jar,mp-common-0.0.1-plain.jar,mp-cscene-0.0.1-plain.jar,mp-devops-cluster-0.0.1-plain.jar,mp-log-0.0.1-plain.jar,mp-flow-0.0.1-plain.jar,mp-front-admin-0.0.1-plain.jar,mp-front-admin-business-0.0.1-plain.jar,mp-front-business-0.0.1-plain.jar,mp-scene-0.0.1-plain.jar,mp-scene-plugin-cloud-0.0.1-plain.jar,mp-scene-plugin-collection-0.0.1-plain.jar,mp-scene-plugin-flow-0.0.1-plain.jar,mp-scene-plugin-gns3-0.0.1-plain.jar,mp-situation-aware-0.0.1-plain.jar -packages cn.cyberpeace.mp -pwd %PASSWORD% -Y
copy /y "%WORK_DIR%\mp\build\libs\mp-0.0.1-encrypted.jar" "%WORK_DIR%\mp.jar"
