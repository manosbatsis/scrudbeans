package com.github.manosbatsis.scrudbeans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scrudbeans")
public class ScrudBeansProperties {

	/** The base path for the generated REST endpoints */
	private String basePath = "/api/rest";

	private String foo;

	private String defaultParentPath = "";

	/** The packages to scan for model resources resulting in component generation */
	private String packages;

	@Override
	public String toString() {
		return "ScrudBeansProperties{" +
				"basePath='" + basePath + '\'' +
				", defaultParentPath='" + defaultParentPath + '\'' +
				", packages='" + packages + '\'' +
				", foo='" + foo + '\'' +
				'}';
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public String getDefaultParentPath() {
		return defaultParentPath;
	}

	public void setDefaultParentPath(String defaultParentPath) {
		this.defaultParentPath = defaultParentPath;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public Set<String> getPackagesToScanAsSet() {
        Set<String> nameSet = new HashSet<String>();
        if (StringUtils.isNotBlank(packages)) {
            List<String> names = Arrays.asList(packages.replaceAll(",", " ").split(" "));
            if (!names.isEmpty()) {
                for (String name : names) {
                    if (StringUtils.isNotBlank(name)) {
                        nameSet.add(name);
                    }
                }
            }
        }
        return nameSet;

	}
}