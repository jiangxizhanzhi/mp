cd %cd%
mvn clean package
mvn
java -jar classfinal-fatjar-1.2.1.jar -file mp/target/mp-template-0.0.2-SNAPSHOT.jar -packages cn.cyberpeace.mp -pwd GwFZdWXw9qEDdHQB -Y