package com.scrcu.ebank.ebap.batch.common.utils;


import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class UUIDCreator
    implements Serializable, Comparable
{
    private static class Holder
    {

        static final SecureRandom numberGenerator = new SecureRandom();


        private Holder()
        {
        }
    }


    private UUIDCreator(byte data[])
    {
        long msb = 0L;
        long lsb = 0L;
        if(data.length != 16) {
            throw new AssertionError("data must be 16 bytes in length");
        }
        for(int i = 0; i < 8; i++) {
            msb = msb << 8 | (long)(data[i] & 255);
        }

        for(int i = 8; i < 16; i++) {
            lsb = lsb << 8 | (long)(data[i] & 255);
        }

        mostSigBits = msb;
        leastSigBits = lsb;
    }

    public UUIDCreator(long mostSigBits, long leastSigBits)
    {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    public static UUIDCreator randomUUID()
    {
        SecureRandom ng = Holder.numberGenerator;
        byte randomBytes[] = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] &= 15;
        randomBytes[6] |= 64;
        randomBytes[8] &= 63;
        randomBytes[8] |= 128;
        return new UUIDCreator(randomBytes);
    }

    public static UUIDCreator nameUUIDFromBytes(byte name[])
    {
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance("MD5");
        }
        catch(NoSuchAlgorithmException nsae)
        {
            throw new InternalError("MD5 not supported");
        }
        byte md5Bytes[] = md.digest(name);
        md5Bytes[6] &= 15;
        md5Bytes[6] |= 48;
        md5Bytes[8] &= 63;
        md5Bytes[8] |= 128;
        return new UUIDCreator(md5Bytes);
    }

    public static UUIDCreator fromString(String name)
    {
        String components[] = name.split("-");
        if(components.length != 5) {
            throw new IllegalArgumentException((new StringBuilder("Invalid UUID string: ")).append(name).toString());
        }
        for(int i = 0; i < 5; i++) {
            components[i] = (new StringBuilder("0x")).append(components[i]).toString();
        }

        long mostSigBits = Long.decode(components[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]).longValue();
        long leastSigBits = Long.decode(components[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]).longValue();
        return new UUIDCreator(mostSigBits, leastSigBits);
    }

    public long getLeastSignificantBits()
    {
        return leastSigBits;
    }

    public long getMostSignificantBits()
    {
        return mostSigBits;
    }

    public int version()
    {
        return (int)(mostSigBits >> 12 & 15L);
    }

    public int variant()
    {
        return (int)(leastSigBits >>> (int)(64L - (leastSigBits >>> 62)) & leastSigBits >> 63);
    }

    public long timestamp()
    {
        if(version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        } else {
            return (mostSigBits & 4095L) << 48 | (mostSigBits >> 16 & 65535L) << 32 | mostSigBits >>> 32;
        }
    }

    public int clockSequence()
    {
        if(version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        } else {
            return (int)((leastSigBits & 4611404543450677248L) >>> 48);
        }
    }

    public long node()
    {
        if(version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        } else {
            return leastSigBits & 281474976710655L;
        }
    }

    @Override
    public String toString()
    {
        return (new StringBuilder(String.valueOf(digits(mostSigBits >> 32, 8)))).append(digits(mostSigBits >> 16, 4)).append(digits(mostSigBits, 4)).append(digits(leastSigBits >> 48, 4)).append(digits(leastSigBits, 12)).toString();
    }

    private static String digits(long val, int digits)
    {
        long hi = 1L << digits * 4;
        return Long.toHexString(hi | val & hi - 1L).substring(1);
    }

    @Override
    public int hashCode()
    {
        long hilo = mostSigBits ^ leastSigBits;
        return (int)(hilo >> 32) ^ (int)hilo;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || obj.getClass() != UUIDCreator.class) {
            return false;
        }
        UUIDCreator id = (UUIDCreator)obj;
        return mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits;
    }

    public int compareTo(UUIDCreator val)
    {
        return mostSigBits >= val.mostSigBits ? mostSigBits <= val.mostSigBits ? leastSigBits >= val.leastSigBits ? ((byte) (((byte)(leastSigBits <= val.leastSigBits ? 0 : 1)))) : -1 : 1 : -1;
    }

    @Override
    public int compareTo(Object obj)
    {
        return compareTo((UUIDCreator)obj);
    }

    private static final long serialVersionUID = 5204034510214439076L;
    private final long mostSigBits;
    private final long leastSigBits;

}
