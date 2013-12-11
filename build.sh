mvn clean
mvn package -U -Dmaven.test.skip=true assembly:assembly
rm -rf ~/dev/tomcat7/webapps/ROOT/*
unzip web-web/target/web.war -d ~/dev/tomcat7/webapps/ROOT/
