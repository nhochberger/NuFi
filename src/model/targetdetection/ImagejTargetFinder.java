package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.util.LinkedList;
import java.util.List;

import controller.configuration.NuFiConfiguration;

public class ImagejTargetFinder extends SessionBasedObject implements TargetFinder {

	private final NuFiConfiguration configuration;
	private final List<TargetPoint> targets;

	public ImagejTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
		this.configuration = configuration;
		this.targets = new LinkedList<>();
	}

	@Override
	public void findTargets() {
	}

	@Override
	public List<TargetPoint> getTargets() {
		return this.targets;
	}
}
