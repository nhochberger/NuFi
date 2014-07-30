package model.targetdetection;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;

import java.util.List;

import controller.configuration.NuFiConfiguration;

public class ImagejTargetFinder extends SessionBasedObject implements TargetFinder {

	public ImagejTargetFinder(final BasicSession session, final NuFiConfiguration configuration) {
		super(session);
	}

	@Override
	public void findTargets() {

		// TODO Auto-generated method stub
	}

	@Override
	public List<TargetPoint> getTargets() {
		// TODO Auto-generated method stub
		return null;
	}
}
