package model;

import java.io.File;

public class NuFiImage {

	private final File channel1;
	private final File channel2;
	private final File channel3;

	public NuFiImage(final File channel1, final File channel2, final File channel3) {
		super();
		this.channel1 = channel1;
		this.channel2 = channel2;
		this.channel3 = channel3;
	}

	public File getChannel1() {
		return this.channel1;
	}

	public File getChannel2() {
		return this.channel2;
	}

	public File getChannel3() {
		return this.channel3;
	}
}
