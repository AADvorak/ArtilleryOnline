package com.github.aadvorak.artilleryonline.battle.contact;

import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Vector;
import com.github.aadvorak.artilleryonline.battle.common.lines.Polygon;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.ArrayList;
import java.util.List;

public class SATPolygonsContactDetector implements PolygonsContactDetector {

    /**
     * Detects collision between two convex polygons using the Separating Axis Theorem (SAT).
     *
     * @param p1 First polygon
     * @param p2 Second polygon
     * @return Contact information if polygons are colliding, null otherwise
     */
    @Override
    public Contact detect(Polygon p1, Polygon p2) {
        Contact bestContact = null;
        bestContact = checkPolygonAxes(p1, p2, bestContact, false);
        if (bestContact == null) {
            return null;
        }
        bestContact = checkPolygonAxes(p2, p1, bestContact, true);
        return bestContact;
    }

    /**
     * Checks separating axes from a polygon against another polygon.
     *
     * @param sourcePolygon Polygon whose axes to check
     * @param targetPolygon Polygon to check against
     * @param bestContact Current best contact, or null if none
     * @param invert if contact should be inverted
     * @return Updated best contact, or null if separating axis found
     */
    private Contact checkPolygonAxes(Polygon sourcePolygon, Polygon targetPolygon,
                                            Contact bestContact, boolean invert) {
        for (Segment side : sourcePolygon.sides()) {
            Contact contact = checkSeparatingAxis(sourcePolygon, targetPolygon, side.normal());
            if (contact == null) {
                return null;
            }
            if (bestContact == null || contact.depth() < bestContact.depth()) {
                bestContact = invert ? contact.inverted() : contact;
            }
        }
        return bestContact;
    }

    /**
     * Checks if two polygons are separated along a given axis.
     *
     * @param p1 First polygon
     * @param p2 Second polygon
     * @param axis Normal vector of the axis to check
     * @return Contact information if overlapping, null if separated
     */
    private Contact checkSeparatingAxis(Polygon p1, Polygon p2, Vector axis) {
        // Project polygons onto the axis
        double min1 = Double.POSITIVE_INFINITY;
        double max1 = Double.NEGATIVE_INFINITY;
        double min2 = Double.POSITIVE_INFINITY;
        double max2 = Double.NEGATIVE_INFINITY;

        // Project first polygon
        for (Position vertex : p1.vertices()) {
            double projection = vertex.getX() * axis.getX() + vertex.getY() * axis.getY();
            min1 = Math.min(min1, projection);
            max1 = Math.max(max1, projection);
        }

        // Project second polygon
        for (Position vertex : p2.vertices()) {
            double projection = vertex.getX() * axis.getX() + vertex.getY() * axis.getY();
            min2 = Math.min(min2, projection);
            max2 = Math.max(max2, projection);
        }

        // Check for separation
        if (max1 < min2 || max2 < min1) {
            return null; // Separated along this axis
        }

        // Calculate overlap depth and contact information
        double overlap1 = max1 - min2;
        double overlap2 = max2 - min1;
        double depth = Math.min(overlap1, overlap2);

        // Determine contact normal (always point from p1 to p2)
        Vector normal = axis;
        if (overlap1 < overlap2) {
            normal = axis.inverted();
        }

        // Calculate contact position (approximate center of overlap)
        // The overlap region is from max(min1, min2) to min(max1, max2)
        double overlapStart = Math.max(min1, min2);
        double overlapEnd = Math.min(max1, max2);

        // Find vertices that contribute to the overlap region
        List<Position> overlappingVertices = new ArrayList<>();
        for (Position vertex : p1.vertices()) {
            double projection = vertex.getX() * axis.getX() + vertex.getY() * axis.getY();
            if (projection >= overlapStart && projection <= overlapEnd) {
                overlappingVertices.add(vertex);
            }
        }
        for (Position vertex : p2.vertices()) {
            double projection = vertex.getX() * axis.getX() + vertex.getY() * axis.getY();
            if (projection >= overlapStart && projection <= overlapEnd) {
                overlappingVertices.add(vertex);
            }
        }

        // Calculate the approximate center of the overlapping region
        Position contactPosition = new Position();
        if (!overlappingVertices.isEmpty()) {
            double sumX = 0;
            double sumY = 0;
            for (Position vertex : overlappingVertices) {
                sumX += vertex.getX();
                sumY += vertex.getY();
            }
            contactPosition
                    .setX(sumX / overlappingVertices.size())
                    .setY(sumY / overlappingVertices.size());
        }

        return Contact.of(depth, normal.inverted(), contactPosition);
    }
}
