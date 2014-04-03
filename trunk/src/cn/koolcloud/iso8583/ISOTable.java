package cn.koolcloud.iso8583;

public class ISOTable
{
	protected int fieldNumber;
	protected short fieldLen;
	protected byte fieldType;

	public ISOTable()
	{
		fieldNumber = -1;
	}

	/**
	 * @param n -
	 *          the FieldNumber
	 */
	public ISOTable(int n)
	{
		fieldNumber = n;
	}

	/**
	 * @param num -
	 *          fieldNumber
	 * @param len -
	 *          fieldLen
	 * @param type -
	 *          fieldType
	 */
	public ISOTable(int num, short len, byte type)
	{
		fieldNumber = num;
		fieldLen = len;
		fieldType = type;
	}
}