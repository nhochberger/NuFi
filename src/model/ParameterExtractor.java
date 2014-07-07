package model;

import hochberger.utilities.application.session.BasicSession;

public class ParameterExtractor {

	private String[] args;

	private ParameterExtractor(String[] args) {
		super();
		this.args = args;
	}

	public static ParameterExtractor extractFrom(String[] args) {
		return new ParameterExtractor(args);
	}

	public void to(BasicSession session) {
		session.setSessionVariable("destination", args[0]);
	}
}
