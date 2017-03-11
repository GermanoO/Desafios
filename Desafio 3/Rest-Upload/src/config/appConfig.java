package config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

@ApplicationPath("/rest")
public class appConfig extends Application{
	
	@Override
	public Set<Class<?>> getClasses() {
	
		Set<Class<?>> resources = new HashSet<>();
		resources.add(MultiPartFeature.class);
		
		return resources;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put("jersey.config.server.provider.packages", "rest");
		return props;
	}

}
