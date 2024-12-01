package no.ntnu.node;

import java.util.Objects;

/**
 * Represents one sensor reading (value).
 */
public class SensorReading {
  private final String type;
  private final String unit;
  private double value;

  /**
   * Create a new sensor reading.
   *
   * @param type  The type of sensor being read
   * @param value The current value of the sensor
   * @param unit  The unit, for example: %, lux
   */
  public SensorReading(String type, double value, String unit) {
    this.type = type;
    this.unit = unit;
    this.value = value;
  }

  public String readingToString(int nodeId) {
    return "Data=Reading;type=" + type + ";" + "value=" + value + ";" + "unit=" + unit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SensorReading that = (SensorReading) o;
    return Double.compare(value, that.value) == 0
        && Objects.equals(type, that.type)
        && Objects.equals(unit, that.unit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value, unit);
  }

  public void setValue(double newValue) {
    this.value = newValue;
  }

  /**
   * Get a human-readable (formatted) version of the current reading, including the unit.
   *
   * @return The sensor reading and the unit
   */
  public String getFormatted() {
    return value + unit;
  }

  public String getType() {
    return type;
  }

  public String getUnit() {
    return unit;
  }

  public double getValue() {
    return value;
  }
}