package cn.koolcloud.iso8583;

public class MessageTypeTable
{
	protected int transType;
	protected byte[] reqMsgType;
	protected byte[] processingCode; //ProcessState
	
	public MessageTypeTable()
	{
		transType = -1;
	}

	/**
	 * @param trans -
	 *          transType
	 * @param reqType -
	 *          reqMsgType
	 * @param respType -
	 *          respMsgType
	 * @param procCode -
	 *          processingCode
	 */
	public MessageTypeTable(int trans, String reqType, String procCode)
	{
		this.transType = trans;
		this.reqMsgType = reqType.getBytes();
		this.processingCode = procCode.getBytes();
	}

}
