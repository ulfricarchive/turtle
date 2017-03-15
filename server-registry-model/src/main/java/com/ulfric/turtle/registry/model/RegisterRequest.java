package com.ulfric.turtle.registry.model;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.turtle.model.Request;

public interface RegisterRequest extends Request {

	Artifact getArtifact();

	void setArtifact(Artifact artifact);

}
