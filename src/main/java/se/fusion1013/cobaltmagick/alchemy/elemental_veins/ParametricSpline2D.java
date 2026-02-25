package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ParametricSpline2D {

    private final PolynomialSplineFunction xSpline;
    private final PolynomialSplineFunction ySpline;
    private final double tMin;
    private final double tMax;

    /**
     * Construct spline from ordered points.
     */
    public ParametricSpline2D(List<Point2D> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Need at least two points");
        }

        int n = points.size();
        double[] t = new double[n];
        double[] x = new double[n];
        double[] y = new double[n];

        // Arc-length parameterization
        t[0] = 0.0;
        x[0] = points.getFirst().x;
        y[0] = points.getFirst().y;

        for (int i = 1; i < n; i++) {
            Point2D p0 = points.get(i - 1);
            Point2D p1 = points.get(i);

            double dx = p1.x - p0.x;
            double dy = p1.y - p0.y;
            double dist = Math.hypot(dx, dy);

            t[i] = t[i - 1] + dist;
            x[i] = p1.x;
            y[i] = p1.y;
        }

        this.tMin = t[0];
        this.tMax = t[n - 1];

        SplineInterpolator interpolator = new SplineInterpolator();
        this.xSpline = interpolator.interpolate(t, x);
        this.ySpline = interpolator.interpolate(t, y);
    }

    /**
     * Evaluate spline at parameter t.
     */
    public Point2D value(double t) {
        return new Point2D(
                xSpline.value(t),
                ySpline.value(t)
        );
    }

    /**
     * Compute squared distance from spline at parameter t to point (px, py).
     */
    private double squaredDistance(double t, double px, double py) {
        double dx = xSpline.value(t) - px;
        double dy = ySpline.value(t) - py;
        return dx * dx + dy * dy;
    }

    public Result closestPoint(double px, double py) {

        int samples = 500;   // coarse scan resolution
        double dt = (tMax - tMin) / samples;

        double bestT = tMin;
        double bestDistSq = Double.POSITIVE_INFINITY;

        // ----------------------------------------------------
        // 1) Coarse scan to locate good candidate region
        // ----------------------------------------------------
        for (int i = 0; i <= samples; i++) {
            double t = tMin + i * dt;
            double distSq = squaredDistance(t, px, py);

            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                bestT = t;
            }
        }

        // ----------------------------------------------------
        // 2) Refine locally around best coarse point
        // ----------------------------------------------------
        double refineRadius = dt * 2.0;
        double lower = Math.max(tMin, bestT - refineRadius);
        double upper = Math.min(tMax, bestT + refineRadius);

        UnivariateFunction distanceFunction = t ->
                squaredDistance(t, px, py);

        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-14);

        UnivariatePointValuePair optimum = optimizer.optimize(
                new MaxEval(200),
                new UnivariateObjectiveFunction(distanceFunction),
                GoalType.MINIMIZE,
                new SearchInterval(lower, upper)
        );

        double refinedT = optimum.getPoint();
        double refinedDist = Math.sqrt(optimum.getValue());

        Point2D closest = value(refinedT);

        return new Result(closest, refinedDist, refinedT);
    }

    public List<Point2D> sampleEquidistant(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("n must be >= 2");
        }

        int samples = 1000; // internal resolution for length approximation
        double[] ts = new double[samples + 1];
        double[] cumulativeLength = new double[samples + 1];

        double dt = (tMax - tMin) / samples;

        ts[0] = tMin;
        cumulativeLength[0] = 0.0;

        Point2D prev = value(tMin);
        double totalLength = 0.0;

        // Build arc-length table
        for (int i = 1; i <= samples; i++) {
            double t = tMin + i * dt;
            ts[i] = t;

            Point2D curr = value(t);
            double segmentLength = Math.hypot(
                    curr.x - prev.x,
                    curr.y - prev.y
            );

            totalLength += segmentLength;
            cumulativeLength[i] = totalLength;

            prev = curr;
        }

        // Desired spacing
        double spacing = totalLength / (n - 1);

        return getPoints(n, spacing, cumulativeLength, ts, samples);
    }

    private List<Point2D> getPoints(int n, double spacing, double[] cumulativeLength, double[] ts, int samples) {

        List<Point2D> result = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            double targetLength = i * spacing;

            // Binary search in cumulative length
            int index = java.util.Arrays.binarySearch(cumulativeLength, targetLength);

            if (index < 0) {
                index = -index - 1;
            }

            if (index <= 0) {
                result.add(value(ts[0]));
            } else if (index >= samples) {
                result.add(value(ts[samples]));
            } else {
                // Linear interpolation in arc-length space
                double l1 = cumulativeLength[index - 1];
                double l2 = cumulativeLength[index];
                double t1 = ts[index - 1];
                double t2 = ts[index];

                double alpha = (targetLength - l1) / (l2 - l1);
                double tInterp = t1 + alpha * (t2 - t1);

                result.add(value(tInterp));
            }
        }

        return result;
    }

    public static List<Point2D> getPoints(List<Vector> vectors) {
        List<Point2D> points = new ArrayList<>();
        for (Vector vector : vectors) {
            points.add(new Point2D(vector.getX(), vector.getZ()));
        }
        return points;
    }

    public record Point2D(double x, double y) {
    }

    public record Result(Point2D point, double distance, double parameter) {
    }
}