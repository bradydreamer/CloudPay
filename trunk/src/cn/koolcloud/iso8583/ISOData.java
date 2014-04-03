package cn.koolcloud.iso8583;

public class ISOData
{
  public static final int MAX_LENGTH = 1024;
  
  protected short[] bitFlag = new short[128];			// bitFlag表示位图中是否置位
  protected short offset;								// 当前的长度
  protected byte[] dataBuffer = new byte[MAX_LENGTH];	// 整个8583包的数据
  protected ISOTable[] isotable;						// 定义了含[ 位图位置， 最大长度， 数据类型]的对象

  public ISOData()
  {
	  offset = 0;
  }

  // copy指定数据到这个8583包中
  public void setDataBuffer(byte[] data, int dataOffset, int length)
  {
	  System.arraycopy(data, dataOffset, dataBuffer, offset, length);
	  offset += length;
  }

  // 从8583包中copy到所给的byte[]数组中。
  public void fetchDataBuffer(byte[] data, int dataOffset, int length)
  {
	  System.arraycopy(dataBuffer, offset, data, dataOffset,  length);
	  offset += length;
  }
  
  public byte[] getDataBuffer()
  {
	  return dataBuffer;
  }
  
  public void setOffset(short offvalue)
  {
	  offset = offvalue;
  }
  
  public void clearBitFlag()
  {
	  if(bitFlag == null)
	  {
		  bitFlag = new short[128];
	  }
	  else
	  {
		  for(int i=0;i<128;i++)
		  {
			  bitFlag[i] = 0x00;
		  }
	  }
  }
  
  public void SetBitmap( )
  {
    byte bitmask, bitmap;
    int i, j, n; 

    /* copy iso elements to dataBuffer */
    for (i = 0; i < 8; i++)
    {
      bitmap = (byte) 0x00;
      bitmask = (byte) 0x80;
      for (j = 0; j < 8; j++, bitmask = (byte) ((bitmask & 0xFF) >>> 1))
      {
        n = (i << 3) + j;
        if (n == 0 )
          continue;
        n--;
        if (bitFlag[n] == 0)
          continue;
        bitmap |= bitmask;
      }
      dataBuffer[2+i] = bitmap;
    }
    return;
  }
}