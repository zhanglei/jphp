package ru.regenix.jphp.runtime.memory.support;

import ru.regenix.jphp.runtime.lang.ForeachIterator;
import ru.regenix.jphp.runtime.memory.*;

abstract public class Memory {
    public enum Type {
        NULL, BOOL, INT, DOUBLE, STRING, ARRAY, OBJECT, REFERENCE, KEY_VALUE;

        public Class toClass(){
            if (this == DOUBLE)
                return Double.TYPE;
            else if (this == INT)
                return Long.TYPE;
            else if (this == STRING)
                return String.class;
            else if (this == BOOL)
                return Boolean.TYPE;
            else if (this == ARRAY)
                return ArrayMemory.class;
            else if (this == OBJECT)
                return ObjectMemory.class;
            else if (this == REFERENCE)
                return Memory.class;
            else if (this == KEY_VALUE)
                return KeyValueMemory.class;

            return null;
        }

        public static Type valueOf(Class clazz){
            if (clazz == Long.TYPE)
                return INT;
            if (clazz == Double.TYPE)
                return DOUBLE;
            if (clazz == String.class)
                return STRING;
            if (clazz == Boolean.TYPE)
                return BOOL;
            if (clazz == ArrayMemory.class)
                return ARRAY;
            if (clazz == ObjectMemory.class)
                return OBJECT;
            if (clazz == KeyValueMemory.class)
                return KEY_VALUE;

            return REFERENCE;
        }

        @Override
        public String toString(){
            switch (this){
                case ARRAY: return "array";
                case BOOL: return "boolean";
                case DOUBLE: return "float";
                case INT: return "integer";
                case NULL: return "NULL";
                case OBJECT: return "object";
                case STRING: return "string";
                default:
                    return "unknown";
            }
        }
    }

    public final Type type;

    protected Memory(Type type) {
        this.type = type;
    }

    public static final Memory NULL = NullMemory.INSTANCE;
    public static final Memory FALSE = FalseMemory.INSTANCE;
    public static final Memory TRUE = TrueMemory.INSTANCE;

    public static final Memory CONST_INT_0 = new LongMemory(0);
    public static final Memory CONST_INT_M1 = new LongMemory(-1);
    public static final Memory CONST_INT_1 = new LongMemory(1);
    public static final Memory CONST_INT_2 = new LongMemory(2);
    public static final Memory CONST_INT_3 = new LongMemory(3);
    public static final Memory CONST_INT_4 = new LongMemory(4);
    public static final Memory CONST_INT_5 = new LongMemory(5);

    public static final Memory CONST_DOUBLE_0 = new DoubleMemory(0.0);
    public static final Memory CONST_DOUBLE_1 = new DoubleMemory(1.0);

    public static final Memory CONST_EMPTY_STRING = new StringMemory("");

    public boolean isNull(){
        return type == Type.NULL;
    }

    public boolean isShortcut(){
        return false;
    }

    abstract public long toLong();
    public int toInteger(){ return (int)toLong(); }

    abstract public double toDouble();
    abstract public boolean toBoolean();
    abstract public Memory toNumeric();
    abstract public String toString();

    public Type getRealType(){
        return type;
    }

    public char toChar(){
        switch (type){
            case STRING:
                String tmp = toString();
                if (tmp.isEmpty())
                    return '\0';
                else
                    return tmp.charAt(0);
            default:
                return (char)toLong();
        }
    }

    public int getPointer(boolean absolute){
        return super.hashCode();
    }

    public int getPointer(){
        return super.hashCode();
    }

    public Memory newKeyValue(Memory memory){ return new KeyValueMemory(this.toValue(), memory); }
    public Memory newKeyValue(long memory){ return new KeyValueMemory(this.toValue(), LongMemory.valueOf(memory)); }
    public Memory newKeyValue(double memory){ return new KeyValueMemory(this.toValue(), new DoubleMemory(memory)); }
    public Memory newKeyValue(boolean memory){ return new KeyValueMemory(this.toValue(), memory ? TRUE : FALSE); }
    public Memory newKeyValue(String memory){ return new KeyValueMemory(this.toValue(), new StringMemory(memory)); }

    public boolean isObject() { return type == Type.OBJECT; }
    public boolean isArray(){ return type == Type.ARRAY; }
    public boolean isString() { return type == Type.STRING; }
    public boolean isNumber() { return type == Type.INT || type == Type.DOUBLE; }
    public boolean isReference() { return false; }
    // <value>[index]
    public Memory valueOfIndex(Memory index) { return NULL; }
    public Memory valueOfIndex(long index) { return NULL; }
    public Memory valueOfIndex(double index) { return NULL; }
    public Memory valueOfIndex(String index) { return NULL; }
    public Memory valueOfIndex(boolean index) { return NULL; }

    public Memory refOfIndex(Memory index) { return NULL; }
    public Memory refOfIndex(long index) { return NULL; }
    public Memory refOfIndex(double index) { return NULL; }
    public Memory refOfIndex(String index) { return NULL; }
    public Memory refOfIndex(boolean index) { return NULL; }
    public Memory refOfPush() { return new ReferenceMemory(); }

    // INC DEC
    abstract public Memory inc();
    abstract public Memory dec();

    // NEGATIVE
    abstract public Memory negative();

    // CONCAT
    public String concat(Memory memory){  return toString() + memory.toString(); }
    public String concat(long value) { return toString() + value; }
    public String concat(double value) { return toString() + value; }
    public String concat(boolean value) { return toString() + boolToString(value); }
    public String concat(String value) { return toString() + value; }

    // PLUS
    abstract public Memory plus(Memory memory);
    public Memory plus(long value){ return new LongMemory(toLong() + value); }
    public Memory plus(double value){ return new DoubleMemory(toDouble() + value); }
    public Memory plus(boolean value){ return new LongMemory(toLong() + (value ? 1 : 0)); }
    public Memory plus(String value){ return plus(StringMemory.toNumeric(value)); }

    // MINUS
    abstract public Memory minus(Memory memory);
    public Memory minus(long value){ return new LongMemory(toLong() - value); }
    public Memory minus(double value){ return new DoubleMemory(toDouble() - value); }
    public Memory minus(boolean value){ return new LongMemory(toLong() - (value ? 1 : 0)); }
    public Memory minus(String value){ return minus(StringMemory.toNumeric(value)); }

    // MUL
    abstract public Memory mul(Memory memory);
    public Memory mul(long value){ return new LongMemory(toLong() * value); }
    public Memory mul(double value){ return new DoubleMemory(toDouble() * value); }
    public Memory mul(boolean value){ return LongMemory.valueOf(toLong() * (value ? 1 : 0));}
    public Memory mul(String value){ return mul(StringMemory.toNumeric(value)); }

    // DIV
    abstract public Memory div(Memory memory);
    public Memory div(long value){ if(value==0) return FALSE; return new DoubleMemory(toDouble() / value); }
    public Memory div(double value){ if(value==0.0) return FALSE; return new DoubleMemory(toDouble() / value); }
    public Memory div(boolean value){ if(!value) return FALSE; return LongMemory.valueOf(toLong()); }
    public Memory div(String value){ return div(StringMemory.toNumeric(value)); }

    // MOD
    abstract public Memory mod(Memory memory);
    public Memory mod(long value){ if (value==0) return FALSE; return LongMemory.valueOf(toLong() % value); }
    public Memory mod(double value){ return mod((long)value); }
    public Memory mod(boolean value){ if (!value) return FALSE; return LongMemory.valueOf(toLong() % 1); }
    public Memory mod(String value){ return div(StringMemory.toNumeric(value)); }

    // NOT
    public boolean not(){ return toLong() == 0; }

    // EQUAL
    abstract public boolean equal(Memory memory);
    public boolean equal(long value){ return toLong() == value; }
    public boolean equal(double value) { return DoubleMemory.almostEqual(toDouble(), value); }
    public boolean equal(boolean value) { return toBoolean() == value; }
    public boolean equal(String value) { return equal(StringMemory.toNumeric(value)); }

    // IDENTICAL
    abstract public boolean identical(Memory memory);
    public boolean identical(long value) { return type == Type.INT && toLong() == value; }
    public boolean identical(double value) { return type == Type.DOUBLE && DoubleMemory.almostEqual(toDouble(), value); }
    public boolean identical(boolean value) { return type == Type.BOOL && value ? toImmutable() == TRUE : toImmutable() == FALSE; }
    public boolean identical(String value) { return type == Type.STRING && toString().equals(value); }

    // NOT EQUAL
    abstract public boolean notEqual(Memory memory);
    public boolean notEqual(long value){ return toLong() != value; }
    public boolean notEqual(double value) { return toDouble() != value; }
    public boolean notEqual(boolean value) { return toBoolean() != value; }
    public boolean notEqual(String value) { return !toString().equals(value); }

    // NOT IDENTICAL
    public boolean notIdentical(Memory memory) { return !identical(memory); }
    public boolean notIdentical(long memory) { return !identical(memory); }
    public boolean notIdentical(double memory) { return !identical(memory); }
    public boolean notIdentical(boolean memory) { return !identical(memory); }
    public boolean notIdentical(String memory) { return !identical(memory); }

    // SMALLER
    abstract public boolean smaller(Memory memory);
    public boolean smaller(long value) { return toDouble() < value; }
    public boolean smaller(double value) { return toDouble() < value; }
    public boolean smaller(boolean value) { return toDouble() < (value ? 1 : 0); }
    public boolean smaller(String value) { return this.smaller(StringMemory.toNumeric(value)); }

    // SMALLER EQ
    abstract public boolean smallerEq(Memory memory);
    public boolean smallerEq(long value) { return toDouble() <= value; }
    public boolean smallerEq(double value) { return toDouble() <= value; }
    public boolean smallerEq(boolean value) { return toDouble() <= (value ? 1 : 0); }
    public boolean smallerEq(String value) { return this.smallerEq(StringMemory.toNumeric(value)); }

    // GREATER
    abstract public boolean greater(Memory memory);
    public boolean greater(long value) { return toDouble() > value; }
    public boolean greater(double value) { return toDouble() > value; }
    public boolean greater(boolean value) { return toDouble() > (value ? 1 : 0); }
    public boolean greater(String value) { return this.smaller(StringMemory.toNumeric(value)); }

    // GREATER EQ
    abstract public boolean greaterEq(Memory memory);
    public boolean greaterEq(long value) { return toDouble() >= value; }
    public boolean greaterEq(double value) { return toDouble() >= value; }
    public boolean greaterEq(boolean value) { return toDouble() >= (value ? 1 : 0); }
    public boolean greaterEq(String value) { return this.greaterEq(StringMemory.toNumeric(value)); }


    // BIT &
    public Memory bitAnd(Memory memory) { return LongMemory.valueOf( toLong() & memory.toLong() ); }
    public Memory bitAnd(long memory) { return LongMemory.valueOf( toLong() & memory ); }
    public Memory bitAnd(double memory) { return LongMemory.valueOf( toLong() & (long)memory ); }
    public Memory bitAnd(boolean memory) { return LongMemory.valueOf( toLong() & (memory ? 1 : 0) ); }
    public Memory bitAnd(String memory) { return LongMemory.valueOf( toLong() & StringMemory.toNumeric(memory).toLong() ); }

    // BIT |
    public Memory bitOr(Memory memory) { return LongMemory.valueOf( toLong() | memory.toLong() ); }
    public Memory bitOr(long memory) { return LongMemory.valueOf( toLong() | memory ); }
    public Memory bitOr(double memory) { return LongMemory.valueOf( toLong() | (long)memory ); }
    public Memory bitOr(boolean memory) { return LongMemory.valueOf( toLong() | (memory ? 1 : 0) ); }
    public Memory bitOr(String memory) { return LongMemory.valueOf( toLong() | StringMemory.toNumeric(memory).toLong() ); }

    // BIT XOR ^
    public Memory bitXor(Memory memory) { return LongMemory.valueOf( toLong() ^ memory.toLong() ); }
    public Memory bitXor(long memory) { return LongMemory.valueOf( toLong() ^ memory ); }
    public Memory bitXor(double memory) { return LongMemory.valueOf( toLong() ^ (long)memory ); }
    public Memory bitXor(boolean memory) { return LongMemory.valueOf( toLong() ^ (memory ? 1 : 0) ); }
    public Memory bitXor(String memory) { return LongMemory.valueOf( toLong() ^ StringMemory.toNumeric(memory).toLong() ); }

    // BIT not ~
    public Memory bitNot(){ return LongMemory.valueOf(~toLong()); }

    // SHR >>
    public Memory bitShr(Memory memory) { return LongMemory.valueOf( toLong() >> memory.toLong() ); }
    public Memory bitShr(long memory) { return LongMemory.valueOf( toLong() >> memory ); }
    public Memory bitShr(double memory) { return LongMemory.valueOf( toLong() >> (long)memory ); }
    public Memory bitShr(boolean memory) { return LongMemory.valueOf( toLong() >> (memory ? 1 : 0) ); }
    public Memory bitShr(String memory) { return LongMemory.valueOf( toLong() >> StringMemory.toNumeric(memory).toLong() ); }

    // SHL <<
    public Memory bitShl(Memory memory) { return LongMemory.valueOf( toLong() << memory.toLong() ); }
    public Memory bitShl(long memory) { return LongMemory.valueOf( toLong() << memory ); }
    public Memory bitShl(double memory) { return LongMemory.valueOf( toLong() << (long)memory ); }
    public Memory bitShl(boolean memory) { return LongMemory.valueOf( toLong() << (memory ? 1 : 0) ); }
    public Memory bitShl(String memory) { return LongMemory.valueOf( toLong() << StringMemory.toNumeric(memory).toLong() ); }

    // ASSIGN
    public Memory assign(Memory memory){ throw new RuntimeException("Invalid assign"); }
    public Memory assign(long value){ throw new RuntimeException("Invalid assign"); }
    public Memory assign(double value) { throw new RuntimeException("Invalid assign"); }
    public Memory assign(boolean value) { throw new RuntimeException("Invalid assign"); }
    public Memory assign(String value){ throw new RuntimeException("Invalid assign"); }
    public Memory assignRef(Memory memory){ throw new RuntimeException("Invalid assign"); }

    public void unset(){  }

    public Memory toImmutable(){
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Memory> T toValue(Class<T> clazz){
        return (T) this;
    }

    public Memory toValue(){
        return this;
    }

    public boolean isImmutable(){
        return true;
    }

    /********** RIGHT ******************/
    public Memory minusRight(long value){ return LongMemory.valueOf(value).minus(this); }
    public Memory minusRight(double value){ return new DoubleMemory(value).minus(this); }
    public Memory minusRight(boolean value){ return LongMemory.valueOf((value ? 1 : 0)).minus(this); }
    public Memory minusRight(String value){ return StringMemory.toNumeric(value).minus(this); }

    public Memory divRight(long value){ return LongMemory.valueOf(value).div(this); }
    public Memory divRight(double value){ return new DoubleMemory(value).div(this); }
    public Memory divRight(boolean value){ if(!value) return CONST_INT_0; else return TRUE.div(this); }
    public Memory divRight(String value){ return StringMemory.toNumeric(value).div(this); }

    public Memory modRight(long value){ return LongMemory.valueOf(value).mod(this); }
    public Memory modRight(double value){ return new DoubleMemory(value).mod(this); }
    public Memory modRight(boolean value){ return LongMemory.valueOf((value ? 1 : 0)).mod(this); }
    public Memory modRight(String value){ return StringMemory.toNumeric(value).mod(this); }

    public String concatRight(long value) { return value + toString(); }
    public String concatRight(double value) { return value + toString(); }
    public String concatRight(boolean value) { return boolToString(value) + toString(); }
    public String concatRight(String value) { return value + toString(); }

    public Memory bitShrRight(long value){ return new LongMemory(value >> toLong()); }
    public Memory bitShrRight(double value){ return new LongMemory((long)value >> toLong()); }
    public Memory bitShrRight(boolean value){ return new LongMemory((value ? 1 : 0) >> toLong()); }
    public Memory bitShrRight(String value){ return StringMemory.toNumeric(value).bitShr(this); }

    public Memory bitShlRight(long value){ return new LongMemory(value << toLong()); }
    public Memory bitShlRight(double value){ return new LongMemory((long)value << toLong()); }
    public Memory bitShlRight(boolean value){ return new LongMemory((value ? 1 : 0) << toLong()); }
    public Memory bitShlRight(String value){ return StringMemory.toNumeric(value).bitShl(this); }

    /****************************************************************/
    /** Static *****/
    public static void assignRight(Memory value, Memory memory){ memory.assign(value); }
    public static void assignRight(long value, Memory memory){ memory.assign(value); }
    public static void assignRight(double value, Memory memory){ memory.assign(value); }
    public static void assignRight(boolean value, Memory memory){ memory.assign(value); }
    public static void assignRight(String value, Memory memory){ memory.assign(value); }

    public static void assignRefRight(Memory value, Memory memory) { memory.assignRef(value); }
    ////

    public static String boolToString(boolean value){
        return value ? "1" : "";
    }

    abstract public byte[] getBinaryBytes();

    public ForeachIterator getNewIterator(boolean getReferences, boolean getKeyReferences){ return null; }
}
