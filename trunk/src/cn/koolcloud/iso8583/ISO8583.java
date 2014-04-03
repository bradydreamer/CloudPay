package cn.koolcloud.iso8583;

public class ISO8583
{
  public static final byte ISO8583_CUP = 1;
  
  /**
   * Clear Bit Flag
   * 
   * @param iso -
   *          ISO_data
   * @param dataBuffer -
   *          dataBuffer
   * @param maxLen -
   *          maxLen
   * @param asciiFlag -
   *          asciiFlag
   * @param isotable -
   *          isotable
   * @param versionFlag -
   *          versionFlag
   */
  public static void ClearBit(ISOData iso, byte[] dataBuffer, int maxLen, byte isoVersion )
  {
    int i;

    for (i = 0; i < 128; i++)
    {
      iso.bitFlag[i] = 0;
    }

    iso.offset = 0;
    iso.dataBuffer = dataBuffer;

    iso.isotable = CUPPack.isotable;
    /***************************************************************************
     * ********** Delete ISO93Pack.java if (isoVersion == ISO8583_VER_87)
     * iso.isotable = ISO87Pack.isotable; else if (isoVersion == ISO8583_VER_93)
     * iso.isotable = ISO93Pack.isotable; else iso.isotable = null;
     **************************************************************************/
  }

  public static void GetDataBuffer(byte[] dataBuffer, short offset, ISOData iso)
  {
	  System.arraycopy(iso.dataBuffer,0,dataBuffer,offset,iso.offset);
  }
  
  public static short GetDataLength( ISOData iso)
  {
	  return iso.offset;
  }
}
