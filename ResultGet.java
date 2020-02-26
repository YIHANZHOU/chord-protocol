/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2019-04-05")
public class ResultGet implements org.apache.thrift.TBase<ResultGet, ResultGet._Fields>, java.io.Serializable, Cloneable, Comparable<ResultGet> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ResultGet");

  private static final org.apache.thrift.protocol.TField GENRE_FIELD_DESC = new org.apache.thrift.protocol.TField("genre", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TRACK_INFO_FIELD_DESC = new org.apache.thrift.protocol.TField("trackInfo", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ResultGetStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ResultGetTupleSchemeFactory());
  }

  public String genre; // required
  public List<String> trackInfo; // required
  public int type; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    GENRE((short)1, "genre"),
    TRACK_INFO((short)2, "trackInfo"),
    TYPE((short)3, "type");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // GENRE
          return GENRE;
        case 2: // TRACK_INFO
          return TRACK_INFO;
        case 3: // TYPE
          return TYPE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TYPE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.GENRE, new org.apache.thrift.meta_data.FieldMetaData("genre", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TRACK_INFO, new org.apache.thrift.meta_data.FieldMetaData("trackInfo", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ResultGet.class, metaDataMap);
  }

  public ResultGet() {
  }

  public ResultGet(
    String genre,
    List<String> trackInfo,
    int type)
  {
    this();
    this.genre = genre;
    this.trackInfo = trackInfo;
    this.type = type;
    setTypeIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ResultGet(ResultGet other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetGenre()) {
      this.genre = other.genre;
    }
    if (other.isSetTrackInfo()) {
      List<String> __this__trackInfo = new ArrayList<String>(other.trackInfo);
      this.trackInfo = __this__trackInfo;
    }
    this.type = other.type;
  }

  public ResultGet deepCopy() {
    return new ResultGet(this);
  }

  @Override
  public void clear() {
    this.genre = null;
    this.trackInfo = null;
    setTypeIsSet(false);
    this.type = 0;
  }

  public String getGenre() {
    return this.genre;
  }

  public ResultGet setGenre(String genre) {
    this.genre = genre;
    return this;
  }

  public void unsetGenre() {
    this.genre = null;
  }

  /** Returns true if field genre is set (has been assigned a value) and false otherwise */
  public boolean isSetGenre() {
    return this.genre != null;
  }

  public void setGenreIsSet(boolean value) {
    if (!value) {
      this.genre = null;
    }
  }

  public int getTrackInfoSize() {
    return (this.trackInfo == null) ? 0 : this.trackInfo.size();
  }

  public java.util.Iterator<String> getTrackInfoIterator() {
    return (this.trackInfo == null) ? null : this.trackInfo.iterator();
  }

  public void addToTrackInfo(String elem) {
    if (this.trackInfo == null) {
      this.trackInfo = new ArrayList<String>();
    }
    this.trackInfo.add(elem);
  }

  public List<String> getTrackInfo() {
    return this.trackInfo;
  }

  public ResultGet setTrackInfo(List<String> trackInfo) {
    this.trackInfo = trackInfo;
    return this;
  }

  public void unsetTrackInfo() {
    this.trackInfo = null;
  }

  /** Returns true if field trackInfo is set (has been assigned a value) and false otherwise */
  public boolean isSetTrackInfo() {
    return this.trackInfo != null;
  }

  public void setTrackInfoIsSet(boolean value) {
    if (!value) {
      this.trackInfo = null;
    }
  }

  public int getType() {
    return this.type;
  }

  public ResultGet setType(int type) {
    this.type = type;
    setTypeIsSet(true);
    return this;
  }

  public void unsetType() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TYPE_ISSET_ID);
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return EncodingUtils.testBit(__isset_bitfield, __TYPE_ISSET_ID);
  }

  public void setTypeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TYPE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case GENRE:
      if (value == null) {
        unsetGenre();
      } else {
        setGenre((String)value);
      }
      break;

    case TRACK_INFO:
      if (value == null) {
        unsetTrackInfo();
      } else {
        setTrackInfo((List<String>)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case GENRE:
      return getGenre();

    case TRACK_INFO:
      return getTrackInfo();

    case TYPE:
      return getType();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case GENRE:
      return isSetGenre();
    case TRACK_INFO:
      return isSetTrackInfo();
    case TYPE:
      return isSetType();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ResultGet)
      return this.equals((ResultGet)that);
    return false;
  }

  public boolean equals(ResultGet that) {
    if (that == null)
      return false;

    boolean this_present_genre = true && this.isSetGenre();
    boolean that_present_genre = true && that.isSetGenre();
    if (this_present_genre || that_present_genre) {
      if (!(this_present_genre && that_present_genre))
        return false;
      if (!this.genre.equals(that.genre))
        return false;
    }

    boolean this_present_trackInfo = true && this.isSetTrackInfo();
    boolean that_present_trackInfo = true && that.isSetTrackInfo();
    if (this_present_trackInfo || that_present_trackInfo) {
      if (!(this_present_trackInfo && that_present_trackInfo))
        return false;
      if (!this.trackInfo.equals(that.trackInfo))
        return false;
    }

    boolean this_present_type = true;
    boolean that_present_type = true;
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (this.type != that.type)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_genre = true && (isSetGenre());
    list.add(present_genre);
    if (present_genre)
      list.add(genre);

    boolean present_trackInfo = true && (isSetTrackInfo());
    list.add(present_trackInfo);
    if (present_trackInfo)
      list.add(trackInfo);

    boolean present_type = true;
    list.add(present_type);
    if (present_type)
      list.add(type);

    return list.hashCode();
  }

  @Override
  public int compareTo(ResultGet other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetGenre()).compareTo(other.isSetGenre());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGenre()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.genre, other.genre);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTrackInfo()).compareTo(other.isSetTrackInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTrackInfo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.trackInfo, other.trackInfo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ResultGet(");
    boolean first = true;

    sb.append("genre:");
    if (this.genre == null) {
      sb.append("null");
    } else {
      sb.append(this.genre);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("trackInfo:");
    if (this.trackInfo == null) {
      sb.append("null");
    } else {
      sb.append(this.trackInfo);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    sb.append(this.type);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ResultGetStandardSchemeFactory implements SchemeFactory {
    public ResultGetStandardScheme getScheme() {
      return new ResultGetStandardScheme();
    }
  }

  private static class ResultGetStandardScheme extends StandardScheme<ResultGet> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ResultGet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // GENRE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.genre = iprot.readString();
              struct.setGenreIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TRACK_INFO
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.trackInfo = new ArrayList<String>(_list8.size);
                String _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = iprot.readString();
                  struct.trackInfo.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setTrackInfoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = iprot.readI32();
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ResultGet struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.genre != null) {
        oprot.writeFieldBegin(GENRE_FIELD_DESC);
        oprot.writeString(struct.genre);
        oprot.writeFieldEnd();
      }
      if (struct.trackInfo != null) {
        oprot.writeFieldBegin(TRACK_INFO_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.trackInfo.size()));
          for (String _iter11 : struct.trackInfo)
          {
            oprot.writeString(_iter11);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TYPE_FIELD_DESC);
      oprot.writeI32(struct.type);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ResultGetTupleSchemeFactory implements SchemeFactory {
    public ResultGetTupleScheme getScheme() {
      return new ResultGetTupleScheme();
    }
  }

  private static class ResultGetTupleScheme extends TupleScheme<ResultGet> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ResultGet struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetGenre()) {
        optionals.set(0);
      }
      if (struct.isSetTrackInfo()) {
        optionals.set(1);
      }
      if (struct.isSetType()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetGenre()) {
        oprot.writeString(struct.genre);
      }
      if (struct.isSetTrackInfo()) {
        {
          oprot.writeI32(struct.trackInfo.size());
          for (String _iter12 : struct.trackInfo)
          {
            oprot.writeString(_iter12);
          }
        }
      }
      if (struct.isSetType()) {
        oprot.writeI32(struct.type);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ResultGet struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.genre = iprot.readString();
        struct.setGenreIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.trackInfo = new ArrayList<String>(_list13.size);
          String _elem14;
          for (int _i15 = 0; _i15 < _list13.size; ++_i15)
          {
            _elem14 = iprot.readString();
            struct.trackInfo.add(_elem14);
          }
        }
        struct.setTrackInfoIsSet(true);
      }
      if (incoming.get(2)) {
        struct.type = iprot.readI32();
        struct.setTypeIsSet(true);
      }
    }
  }

}

