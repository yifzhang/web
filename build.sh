mvn clean
mvn package -U -Dmaven.test.skip=true assembly:assembly
rm -rf ~/dev/tomcat7/webapps/ROOT/*
tar -zxvf target/web.tar.gz -C ~/dev/tomcat7/webapps/
mv ~/dev/tomcat7/webapps/web.war ~/dev/tomcat7/webapps/web
