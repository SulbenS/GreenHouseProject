package no.ntnu.node;

/**
 * A sensor which can sense the environment in a specific way.
 */
public class Sensor {
  private static int nextId;

  private final SensorReading reading;
  private final double min;
  private final double max;

  private final int id;
  private final int nodeId;

  /**
   * Create a sensor.
   *
   * @param nodeId  The ID of the node to which this sensor is connected
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public Sensor(int nodeId, String type, double min, double max, double current, String unit) {
    this.reading = new SensorReading(type, current, unit);
    this.nodeId = nodeId;
    this.min = min;
    this.max = max;
    this.id = generateUniqueId();
    ensureValueBoundsAndPrecision(current);
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public Sensor createClone() {
    return new Sensor(this.nodeId, reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
  }

  /**
   * Add a random noise to the sensors to simulate realistic values.
   */
  public void addRandomNoise() {
    double newValue = this.reading.getValue() + generateRealisticNoise();
    ensureValueBoundsAndPrecision(newValue);
  }

  private void ensureValueBoundsAndPrecision(double newValue) {
    newValue = roundToTwoDecimals(newValue);
    if (newValue < min) {
      newValue = min;
    } else if (newValue > max) {
      newValue = max;
    }
    reading.setValue(newValue);
  }

  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  private double generateRealisticNoise() {
    final double wholeRange = max - min;
    final double onePercentOfRange = wholeRange / 100.0;
    final double zeroToTwoPercent = Math.random() * onePercentOfRange * 2;
    return zeroToTwoPercent - onePercentOfRange; // In the range [-1%..+1%]
  }

  /**
   * Apply an external impact (from an actuator) to the current value of the sensor.
   *
   * @param impact The impact to apply - the delta for the value
   */
  public void applyImpact(double impact) {
    double newValue = this.reading.getValue() + impact;
    ensureValueBoundsAndPrecision(newValue);
  }

  private static int generateUniqueId() {
    return nextId++;
  }

  //@Override
  //public String toString() {
  //return this.reading.readingToString();
  //} TODO: maybe this should be implemented

  public String getType() {
    return this.reading.getType();
  }

  /**
   * Return the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public SensorReading getReading() {
    return this.reading;
  }

  /**
   * Return the id of the sensor.
   *
   * @return the id of the sensor.
   */
  public int getSensorId() {
    return this.id;
  }

  /**
   * Return the node id of the sensor.
   *
   * @return the node id of the sensor.
   */
  public int getNodeId() {
    return this.nodeId;
  }
}