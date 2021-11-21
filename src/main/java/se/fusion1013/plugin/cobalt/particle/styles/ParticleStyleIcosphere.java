package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.util.VectorUtils;

import java.util.*;

public class ParticleStyleIcosphere extends ParticleStyle implements IParticleStyle {

    private Map<String, Double> doubleValues = new HashMap<>();

    private int step = 0;

    public ParticleStyleIcosphere(ParticleStyleIcosphere target){
        super(target);

        doubleValues.put("ticks_per_spawn", target.getDouble("ticks_per_spawn"));
        doubleValues.put("radius", target.getDouble("radius"));
        doubleValues.put("particles_per_line", target.getDouble("particles_per_line"));
        doubleValues.put("divisions", target.getDouble("divisions"));
        doubleValues.put("angular_velocity_x", target.getDouble("angular_velocity_x"));
        doubleValues.put("angular_velocity_y", target.getDouble("angular_velocity_y"));
        doubleValues.put("angular_velocity_z", target.getDouble("angular_velocity_z"));
    }

    public ParticleStyleIcosphere(){
        this(Particle.BARRIER);
    }

    public ParticleStyleIcosphere(Particle particle) {
        super("icosphere");
        this.particle = particle;
        setDefaultSettings();
    }

    protected void setDefaultSettings() {
        doubleValues.put("ticks_per_spawn", 50.0);
        doubleValues.put("radius", 3.0);
        doubleValues.put("particles_per_line", 8.0);
        doubleValues.put("divisions", 1.0);
        doubleValues.put("angular_velocity_x", 0.00314159265);
        doubleValues.put("angular_velocity_y", 0.00369599135);
        doubleValues.put("angular_velocity_z", 0.00405366794);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Particle getParticle() {
        return this.particle;
    }

    @Override
    public List<PParticle> getParticles(Location location) {

        double ticksPerSpawn = doubleValues.get("ticks_per_spawn");
        int divisions =  (int)Math.round(doubleValues.get("divisions")); // TODO: Make less yank
        double radius = doubleValues.get("radius");
        double particlesPerLine = doubleValues.get("particles_per_line");
        double angularVelocityX = doubleValues.get("angular_velocity_x");
        double angularVelocityY = doubleValues.get("angular_velocity_y");
        double angularVelocityZ = doubleValues.get("angular_velocity_z");

        List<PParticle> particles = new ArrayList<>();
        if (this.step % ticksPerSpawn != 0)
            return particles;

        Icosahedron icosahedron = new Icosahedron(divisions, radius);
        Set<Vector> points = new HashSet<>();
        for (Icosahedron.Triangle triangle : icosahedron.getTriangles())
            points.addAll(this.getPointsAlongTriangle(triangle, particlesPerLine));

        double multiplier = ((double) this.step / ticksPerSpawn);
        double xRotation = multiplier * angularVelocityX;
        double yRotation = multiplier * angularVelocityY;
        double zRotation = multiplier * angularVelocityZ;

        for (Vector point : points) {
            VectorUtils.rotateVector(point, xRotation, yRotation, zRotation);
            particles.add(new PParticle(location.clone().add(point), 0, 0, 0, 0, false, null));
        }

        step++;

        return particles;
    }

    private Set<Vector> getPointsAlongTriangle(Icosahedron.Triangle triangle, double pointsPerLine) {
        Set<Vector> points = new HashSet<>();
        points.addAll(this.getPointsAlongLine(triangle.point1, triangle.point2, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point2, triangle.point3, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point3, triangle.point1, pointsPerLine));
        return points;
    }

    private Set<Vector> getPointsAlongLine(Vector point1, Vector point2, double pointsPerLine) {
        double distance = point1.distance(point2);
        Vector angle = point2.clone().subtract(point1).normalize();
        double distanceBetween = distance / pointsPerLine;

        Set<Vector> points = new HashSet<>();
        for (double i = 0; i < distance; i += distanceBetween)
            points.add(point1.clone().add(angle.clone().multiply(i)));

        return points;
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleIcosphere(this);
    }

    @Override
    public void setDouble(String key, double p) {
        doubleValues.put(key, p);
    }

    @Override
    public List<String> getDoubleKeys() {
        return new ArrayList<>(doubleValues.keySet());
    }

    @Override
    public double getDouble(String key) {
        return doubleValues.get(key);
    }

    /**
     * Largely taken from https://www.javatips.net/api/vintagecraft-master/src/main/java/at/tyron/vintagecraft/Client/Render/Math/Icosahedron.java
     */
    public static class Icosahedron {

        public static double X = 0.525731112119133606f;
        public static double Z = 0.850650808352039932f;

        public static double[][] vdata = {{-X, 0, Z}, {X, 0, Z}, {-X, 0, -Z}, {X, 0, -Z}, {0, Z, X}, {0, Z, -X},
                {0, -Z, X}, {0, -Z, -X}, {Z, X, 0}, {-Z, X, 0}, {Z, -X, 0}, {-Z, -X, 0}};

        public static int[][] tindx = {{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1}, {8, 10, 1}, {8, 3, 10},
                {5, 3, 8}, {5, 2, 3}, {2, 7, 3}, {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6}, {6, 1, 10},
                {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}};

        public Icosahedron(int depth, double radius) {
            for (int[] ints : tindx)
                this.subdivide(vdata[ints[0]], vdata[ints[1]], vdata[ints[2]], depth, radius);
        }

        private final List<Triangle> triangles = new ArrayList<>();

        private void addTriangle(double[] vA0, double[] vB1, double[] vC2, double radius) {
            Triangle triangle = new Triangle(
                    new Vector(vA0[0], vA0[1], vA0[2]).multiply(radius),
                    new Vector(vB1[0], vB1[1], vB1[2]).multiply(radius),
                    new Vector(vC2[0], vC2[1], vC2[2]).multiply(radius)
            );
            this.triangles.add(triangle);
        }

        private void subdivide(double[] vA0, double[] vB1, double[] vC2, int depth, double radius) {
            double[] vAB = new double[3];
            double[] vBC = new double[3];
            double[] vCA = new double[3];

            if (depth == 0) {
                this.addTriangle(vA0, vB1, vC2, radius);
                return;
            }

            for (int i = 0; i < 3; i++) {
                vAB[i] = (vA0[i] + vB1[i]) / 2;
                vBC[i] = (vB1[i] + vC2[i]) / 2;
                vCA[i] = (vC2[i] + vA0[i]) / 2;
            }

            double modAB = mod(vAB);
            double modBC = mod(vBC);
            double modCA = mod(vCA);

            for (int i = 0; i < 3; i++) {
                vAB[i] /= modAB;
                vBC[i] /= modBC;
                vCA[i] /= modCA;
            }

            this.subdivide(vA0, vAB, vCA, depth - 1, radius);
            this.subdivide(vB1, vBC, vAB, depth - 1, radius);
            this.subdivide(vC2, vCA, vBC, depth - 1, radius);
            this.subdivide(vAB, vBC, vCA, depth - 1, radius);
        }

        public static double mod(double[] v) {
            return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        }

        public List<Triangle> getTriangles() {
            return this.triangles;
        }

        public static class Triangle {
            public Vector point1;
            public Vector point2;
            public Vector point3;

            public Triangle(Vector point1, Vector point2, Vector point3) {
                this.point1 = point1;
                this.point2 = point2;
                this.point3 = point3;
            }
        }
    }

}
