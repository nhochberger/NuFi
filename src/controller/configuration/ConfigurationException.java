package controller.configuration;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = -4487918431701008904L;

	public ConfigurationException(final String message) {
		super(message);
	}

	public static class InvalidConfigurationException extends ConfigurationException {

		private static final long serialVersionUID = 3651037404940742635L;

		public InvalidConfigurationException(final String missingKeys) {
			super("There are missing keys in the configuration: " + missingKeys);
		}
	}

	public static class MissingSourceFolderException extends ConfigurationException {

		private static final long serialVersionUID = 5203928823071388289L;

		public MissingSourceFolderException(final String description) {
			super("Source folder error: " + description);
		}
	}

	public static class MissingChannelFilesException extends ConfigurationException {

		private static final long serialVersionUID = 5203928823071388289L;

		public MissingChannelFilesException(final String description) {
			super("Channel files error: " + description);
		}
	}
}
