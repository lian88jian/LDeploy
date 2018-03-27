package per.lian.utils;

import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtil {
	
	public static InputStream getInputStream(String path) throws Exception {
		
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(path);
		return resource.getInputStream();
	}
}
