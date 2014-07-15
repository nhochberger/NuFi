package model;

import hochberger.utilities.application.session.BasicSession;

public class ParameterExtractor {

	private final String[] args;

	private ParameterExtractor(final String[] args) {
		super();
		this.args = args;
	}

	public static ParameterExtractor extractFrom(final String[] args) {
		return new ParameterExtractor(args);
	}

	public void to(final BasicSession session) {
		session.setSessionVariable("destination", this.args[0]);
	}
}
