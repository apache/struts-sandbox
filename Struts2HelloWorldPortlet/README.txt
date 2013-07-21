This is an example of running a Struts 2 portlet using the pluto embedded maven plugin with Jetty servlet container.

To run the application use mvn jetty:run

After Jetty starts up go to http://localhost:8080/struts2helloworldportlet/pluto/index.jsp

The key parts to get the application to run using the embedded pluto plugin are:

1.  include this dependency in your pom.xml

	<dependency>
		<groupId>com.bekk.boss</groupId>
		<artifactId>maven-jetty-pluto-embedded</artifactId>
		<version>1.0.1</version>
	</dependency>
	
2.  include these plugins in your plugins section in pom.xml
   (note the value of org.apache.pluto.embedded.portletIds is the portlet id value from portlet.xml)

		<plugin>
                <groupId>org.apache.pluto</groupId>
                <artifactId>maven-pluto-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>assemble</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.mortbay.jetty</groupId>
                <artifactId>maven-jetty-plugin</artifactId>
                <version>6.1.23</version>
                <configuration>
                    <webXml>${project.build.directory}/pluto-resources/web.xml</webXml>
                    <webDefaultXml>src/main/webapp/WEB-INF/jetty-pluto-web-default.xml</webDefaultXml>
                    <systemProperties>
                        <systemProperty>
                            <name>org.apache.pluto.embedded.portletIds</name>
                            <value>struts2helloworldportlet</value>
                        </systemProperty>
                    </systemProperties>
                </configuration>
            </plugin>

3.  Include jetty-pluto-web-default.xml in WEB-INF

