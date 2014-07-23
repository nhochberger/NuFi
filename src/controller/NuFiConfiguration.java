package controller;

import hochberger.utilities.properties.LoadProperties;

import java.io.IOException;
import java.util.Properties;

public class NuFiConfiguration {

	private final Properties properties;

	private NuFiConfiguration(final Properties properties) {
		super();
		this.properties = properties;
	}

	public static NuFiConfiguration createFrom(final String filePath) throws IOException {
		return new NuFiConfiguration(LoadProperties.fromExtern(filePath));
	}
}
