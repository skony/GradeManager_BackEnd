import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import models.ObjectIdJaxbAdapter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Morphia;

import resources.CustomHeaders;
import resources.DateParamConverterProvider;
import resources.JacksonObjectMapperProvider;
import services.RestService;

public class Main {
	
	public static void main(String[] args) {
		
		URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
	    ResourceConfig config = new ResourceConfig(RestService.class, CustomHeaders.class);
		config.register(new DateParamConverterProvider("yyyy-MM-dd"));
		config.register(new JacksonObjectMapperProvider());
	    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
	    
	    try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
