package org.kwater.aio.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;

public class Conversion 
{
	public static String byteArrayToHexString(byte[] a)
	{
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
		{
			sb.append(String.format("%02X", b & 0xff));
		}
		
		return sb.toString();
	}
	
	public static byte[] hexStringToByteArray(String str)
	{
		if(str == null || str.length() == 0)
		{
			return null;
		}
		
		byte[] byteTemp = new byte[str.length() / 2];
		for(int i = 0; i < byteTemp.length; i++)
		{
			byteTemp[i] = (byte) Integer.parseInt(str.substring(2*i, 2*i+2), 16);
		}
		
		return byteTemp;
	}
	
	public static Date byteArrayToDate(byte[] value)
	{
        BigInteger bi = new BigInteger(value);
        if(value.length <= 8)
        {
        	return new Date(bi.longValue() * 1000L);
        }
        else
        {
        	return null;
        }
        
    }
	
	public static byte[] dateToByteArray(Date value)
	{
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt((int)(value.getTime()/1000L));
		
		return buffer.array();
	}
	
	public static Number byteArrayToNumber(byte[] value)
	{
        BigInteger bi = new BigInteger(value);
        if (value.length == 1) 
        {
            return bi.byteValue();
        } 
        else if (value.length <= 2) 
        {
            return bi.shortValue();
        } 
        else if (value.length <= 4) 
        {
            return bi.intValue();
        } 
        else if (value.length <= 8) 
        {
            return bi.longValue();
        }
        else
        {
        	return null;
        }
    }

	
	public static byte[] numberToByteArray(Number number) 
	{
        ByteBuffer iBuf = null;
        long lValue = number.longValue();
        if (lValue >= Byte.MIN_VALUE && lValue <= Byte.MAX_VALUE) 
        {
            iBuf = ByteBuffer.allocate(1);
            iBuf.put((byte) lValue);
        } 
        else if (lValue >= Short.MIN_VALUE && lValue <= Short.MAX_VALUE) 
        {
            iBuf = ByteBuffer.allocate(2);
            iBuf.putShort((short) lValue);
        } 
        else if (lValue >= Integer.MIN_VALUE && lValue <= Integer.MAX_VALUE) 
        {
            iBuf = ByteBuffer.allocate(4);
            iBuf.putInt((int) lValue);
        } 
        else 
        {
            iBuf = ByteBuffer.allocate(8);
            iBuf.putLong(lValue);
        }
        return iBuf.array();
    }

    public static String MapToString(Object object)
    {
        ObjectMapper mapper = new ObjectMapper();
        String strResult = "";

        try
        {
            strResult = mapper.writeValueAsString(object);
        }
        catch(JsonProcessingException e)
        {
            strResult = "";
        }

        return strResult;
    }

	public static boolean isNumber(String str)
	{
		boolean result = false;
		
		try
		{
			Double.parseDouble(str);
			result = true;
		}
		catch(NumberFormatException e)
		{
			result = false;
		}
		
		return result;
	}

    public static boolean isValidInet4Address(String ip)
    {
        try
        {
            return Inet4Address.getByName(ip).getHostAddress().equals(ip);
        }
        catch(UnknownHostException e)
        {
            return false;
        }
    }
}
