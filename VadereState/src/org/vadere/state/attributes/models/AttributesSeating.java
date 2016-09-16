package org.vadere.state.attributes.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.vadere.state.attributes.Attributes;
import org.vadere.state.attributes.models.seating.SeatFacingDirection;
import org.vadere.state.attributes.models.seating.SeatRelativePosition;
import org.vadere.state.attributes.models.seating.SeatSide;
import org.vadere.state.attributes.models.seating.ValueWithProbabilityFraction;
import org.vadere.state.scenario.Et423Geometry;

/**
 * Parameters for the seating model.
 *
 */
public class AttributesSeating extends Attributes {

	/** The train geometry class name used to generate the scenario with Traingen. */
	private String trainGeometry = Et423Geometry.class.getName();
	
	/**
	 * Choices with probabilities for the seat group. <code>true</code> is
	 * choosing a seat group with the least number of other passengers.
	 */
	private List<ValueWithProbabilityFraction<Boolean>> seatGroupChoice;

	/** Probabilities for seat indexes 0 to 3. */
	private double[] seatChoice0;

	private List<ValueWithProbabilityFraction<SeatRelativePosition>> seatChoice1;

	private List<ValueWithProbabilityFraction<SeatSide>> seatChoice2Side;

	private List<ValueWithProbabilityFraction<SeatFacingDirection>> seatChoice2FacingDirection;
	
	{
		// initialize fields with values from data collection
		// TODO make this autogenerated
	}

	public String getTrainGeometry() {
		return trainGeometry;
	}

	public List<Pair<Boolean, Double>> getSeatGroupChoice() {
		return toPairListForEnumeratedDistribution(seatGroupChoice);
	}

	public double[] getSeatChoice0() {
		return seatChoice0; // TODO fix names
	}

	public List<Pair<SeatRelativePosition, Double>> getSeatChoice1() {
		return toPairListForEnumeratedDistribution(seatChoice1); // TODO fix name
	}

	public List<Pair<SeatSide, Double>> getSeatChoice2Side() {
		return toPairListForEnumeratedDistribution(seatChoice2Side); // TODO fix name
	}

	public List<Pair<SeatFacingDirection, Double>> getSeatChoice2FacingDirection() {
		return toPairListForEnumeratedDistribution(seatChoice2FacingDirection); // TODO fix name
	}
	
	public static <T> List<Pair<T, Double>> toPairListForEnumeratedDistribution(
			List<ValueWithProbabilityFraction<T>> list) {
		return list.stream().map(ValueWithProbabilityFraction::toPair).collect(Collectors.toList());
	}

}
