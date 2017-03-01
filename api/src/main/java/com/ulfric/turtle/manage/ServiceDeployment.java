package com.ulfric.turtle.manage;

import com.ulfric.commons.artifact.Artifact;

public interface ServiceDeployment {

	void load(Artifact artifact);

	void unload(Artifact artifact);

}
