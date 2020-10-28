package org.vadere.simulator.models.bhm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.vadere.state.attributes.models.AttributesBHM;
import org.vadere.state.scenario.Pedestrian;
import org.vadere.state.scenario.Topography;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.util.geometry.shapes.Vector2D;
import org.vadere.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Let agents evade to the right-hand side. Use following evasion preference:
 *
 * <ol>
 *     <li>Evade tangentially (45°) with current speed).</li>
 *     <li>Evade tangentially (45°) with speed/2) if not enough space in previous step.</li>
 *     <li>Evade sideways (90°) with speed/2) if not enough space in previous step.</li>
 *     <li>Evade sideways (90°) with speed/4) if not enough space in previous step.</li>
 *     <li>Wait.</li>
 * </ol>
 */
public class NavigationEvasion implements Navigation {

    private static Logger logger = Logger.getLogger(NavigationEvasion.class);

    private final PedestrianBHM me;
    private final Topography topography;
    private final AttributesBHM attributesBHM;

    public NavigationEvasion(PedestrianBHM me, Topography topography) {
        this.me = me;
        this.topography = topography;
        this.attributesBHM = me.getAttributesBHM();
    }

    @Override
    public VPoint getNavigationPosition() {
        VPoint nextPosition = me.getPosition();

        VPoint currentWalkingDirection = getCurrentTargetDirectionNormedByGradient();
        double stepLength = me.getStepLength();
        List<Pedestrian> closePedestrians = topography.getSpatialMap(Pedestrian.class)
                .getObjects(me.getPosition(), me.getAttributes().getSearchRadius())
                .stream()
                .filter(pedestrian -> pedestrian.getId() != me.getId())
                .collect(Collectors.toList());

        List<Pair<Double, Double>> rotationAndSlowDownStrategies = createRotationAndSlowDownStrategies();

        for (Pair<Double, Double> strategy : rotationAndSlowDownStrategies) {
            Double rotation = strategy.getLeft();
            Double slowDownFactor = strategy.getRight();
            double stepLengthTrial = stepLength * slowDownFactor;

            VPoint possiblePosition = getNextPosition(currentWalkingDirection, rotation, stepLengthTrial);

            if (collideWithPedestrian(possiblePosition, me.getRadius(), closePedestrians) == false) {
                if (me.collidesWithObstacle(possiblePosition) == false) {
                    nextPosition = possiblePosition;
                    break;
                }
            }
        }

        return nextPosition;
    }

    public VPoint getCurrentTargetDirectionNormedByGradient() {
        Vector2D targetGradient = me.getPotentialFieldTarget().getTargetPotentialGradient(me.getPosition(), me);
        Vector2D pedestrianWalkingDirection = targetGradient.rotate(Math.toRadians(180));

        return pedestrianWalkingDirection.norm();
    }


    public VPoint getNextPosition(VPoint currentWalkingDirection, double rotationAngleDegCcw, double stepLength) {
        double rotationAngleRad = Math.toRadians(rotationAngleDegCcw);
        VPoint evasionDirection = currentWalkingDirection.rotate(rotationAngleRad);
        VPoint nextPosition = me.getPosition().add(evasionDirection.scalarMultiply(stepLength));

        return nextPosition;
    }

    private List<Pair<Double, Double>> createRotationAndSlowDownStrategies() {
        // 1st Double = rotation, 2nd Double = slow down from current speed
        List<Pair<Double, Double>> rotationAndSlowDownStrategies = new ArrayList<>();

        rotationAndSlowDownStrategies.add(new ImmutablePair<Double, Double>(-45.0, 1.0));
        rotationAndSlowDownStrategies.add(new ImmutablePair<Double, Double>(-45.0, 0.5));
        rotationAndSlowDownStrategies.add(new ImmutablePair<Double, Double>(-90.0, 0.5));
        rotationAndSlowDownStrategies.add(new ImmutablePair<Double, Double>(-90.0, 0.25));

        return rotationAndSlowDownStrategies;
    }

    private boolean collideWithPedestrian(VPoint position, double allowedDelta, List<Pedestrian> closePedestrians) {
        boolean isCollision = false;

        for (Pedestrian other : closePedestrians) {
            if (position.distance(other.getPosition()) < allowedDelta) {
                isCollision = true;
                break;
            }
        }

        return isCollision;
    }
}