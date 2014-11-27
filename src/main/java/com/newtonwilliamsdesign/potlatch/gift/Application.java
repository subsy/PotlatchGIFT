package com.newtonwilliamsdesign.potlatch.gift;

/***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************
        G I F T
        A Multi-user Web Application and Android Client Application
        for sharing of image gifts.

        Copyright (C) 2014 Newton Williams Design.

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU Affero General Public License as
        published by the Free Software Foundation, either version 3 of the
        License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************/

import java.io.File;
import javax.servlet.MultipartConfigElement;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.newtonwilliamsdesign.potlatch.gift.config.OAuth2ServerConfig;
import com.newtonwilliamsdesign.potlatch.gift.config.SecurityConfiguration;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftRepository;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftServiceUserRepository;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftUserPrefsRepository;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses = {GiftRepository.class, GiftServiceUserRepository.class, GiftUserPrefsRepository.class} )
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.newtonwilliamsdesign.potlatch.gift.mvc", 
							   "com.newtonwilliamsdesign.potlatch.gift.oauth", 
							   "com.newtonwilliamsdesign.potlatch.gift.repository"} )
@Import({SecurityConfiguration.class, OAuth2ServerConfig.class, })
public class Application {
	
	private static final String MAX_REQUEST_SIZE = "4MB";

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	    tomcat.addAdditionalTomcatConnectors(createSslConnector());
	    return tomcat;
	}
	
	final String absoluteKeystoreFile = new File("src/main/resources/private/server.jks").getAbsolutePath();

	private Connector createSslConnector() {
	    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	    Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

	        connector.setScheme("https");
	        connector.setSecure(true);
	        connector.setPort(8443);
	        protocol.setSSLEnabled(true);
	        protocol.setKeystoreFile(absoluteKeystoreFile);
	        protocol.setKeystorePass("password");
	        protocol.setTruststoreFile(absoluteKeystoreFile);
	        protocol.setTruststorePass("password");
	        protocol.setKeystoreType("JKS");
	        protocol.setKeyAlias("servercert");
	        return connector;

	}

	
	// This configuration element adds the ability to accept multipart
		// requests to the web container.
		@Bean
	    public MultipartConfigElement multipartConfigElement() {
			// Setup the application container to be accept multipart requests
			final MultipartConfigFactory factory = new MultipartConfigFactory();
			// Place upper bounds on the size of the requests to ensure that
			// clients don't abuse the web container by sending huge requests
			factory.setMaxFileSize(MAX_REQUEST_SIZE);
			factory.setMaxRequestSize(MAX_REQUEST_SIZE);
			// Return the configuration to setup multipart in the container
			return factory.createMultipartConfig();
		}
		
		
		// Tell Spring to launch our app!
		public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
		}
	
}
