/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package pfe_broker.avro;
@org.apache.avro.specific.AvroGenerated
public enum Type implements org.apache.avro.generic.GenericEnumSymbol<Type> {
  MARKET, LIMIT  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Type\",\"namespace\":\"pfe_broker.avro\",\"symbols\":[\"MARKET\",\"LIMIT\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}