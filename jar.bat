cd %cd%
call gradlew.bat :mp:bootJar
java -jar classfinal-fatjar-1.2.1.jar -file mp/build/libs/mp-0.0.1.jar -packages cn.cyberpeace.mp -pwd GwFZdWXw9qEDdHQB -Y