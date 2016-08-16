package org.vadere.simulator.projects.dataprocessing_mtp;

import org.vadere.simulator.control.SimulationState;
import org.vadere.state.scenario.Pedestrian;

public abstract class PedestrianDensityProcessor extends Processor<TimestepPedestrianIdDataKey, Double> {
	private PedestrianPositionProcessor pedPosProc;
	private IPointDensityAlgorithm densAlg;

	protected void setAlgorithm(IPointDensityAlgorithm densAlg) {
		this.densAlg = densAlg;
		this.setHeader(this.densAlg.getName().toLowerCase() + "-density");
	}

	@Override
	public void doUpdate(final SimulationState state) {
		this.pedPosProc.update(state);

		state.getTopography().getElements(Pedestrian.class).stream()
				.forEach(ped -> this.setValue(new TimestepPedestrianIdDataKey(state.getStep(), ped.getId()),
						this.densAlg.getDensity(ped.getPosition(), state)));
	}

	@Override
	void init(final AttributesProcessor attributes, final ProcessorManager factory) {
		AttributesPedestrianDensityProcessor attDensProc = (AttributesPedestrianDensityProcessor) attributes;

		this.pedPosProc =
				(PedestrianPositionProcessor) factory.getProcessor(attDensProc.getPedestrianPositionProcessorId());
	}
}
