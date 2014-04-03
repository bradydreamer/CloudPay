package cn.koolcloud.parameter;

import cn.koolcloud.constant.Constant;

public class TransDetailInfo extends TransDetailTable implements Constant, Cloneable
{
    // unstored data
    private byte[] responseCode = null;
    private byte[] responseInfo = null;
    private boolean macFlag = false;
    private byte[] mac = new byte[8];
    private byte[] pinBlock = null;
    private long balance;
    private byte[] masterKey = null;
    private byte[] keyCheckSum = null;
    private String serviceCode = "";
    private String issuerName = "";
    private String acquirerName = "";
    public byte[] uploadRecord = null;
    private int batchNumber = 0;
    private String cardholderName = "";
    // 用于参数下载
    private byte paramType = PARAM_MAG;
    private byte[] paramData = null;
    private int paramOffset = 0;
    private byte paramNextFlag = 0;
    private int paramCount = 0;
    private byte[] iccRevData = null;
    private byte[] issuerAuthData = null;
    public TransDetailInfo()
    {
    	init();
    }
    
    public void init()
    {
    	super.init();
    	responseCode = null;	
    	responseInfo = null;
        mac = new byte[8];
        macFlag = false;
        pinBlock = null;
        balance = 0;
        uploadRecord = null;
        masterKey = null;
        keyCheckSum = null;
        serviceCode = "";
        issuerName = "";
        acquirerName = "";
        batchNumber = 0;
        cardholderName = "";
        paramType = PARAM_MAG;
        paramData = null;
        paramOffset = 0;
        paramNextFlag = 0;
        paramCount = 0;
        
    }
    
    // responseCode
    public byte[] getResponseCode()
    {
    	return responseCode;
    }
    
    public void setResponseCode(byte[] responseCode)
    {
    	if(responseCode != null && responseCode.length == 2)
    	{
    		this.responseCode = new byte[2];
    		System.arraycopy(responseCode, 0, this.responseCode, 0, 2);
    	}
    }
    
    // responseInfo
    public byte[] getResponseInfo()
    {
    	return responseInfo;
    }
    
    public void setResponseInfo(byte[] responseInfo)
    {
    	if(responseInfo != null)
    	{
    		this.responseInfo = new byte[responseInfo.length];
    		System.arraycopy(responseInfo, 0, this.responseInfo, 0, responseInfo.length);
    	}
    }
    
    // macFlag
    public boolean getMacFlag()
    {
    	return macFlag;
    }
    
    public void setMacFlag(boolean macFlag)
    {
    	this.macFlag = macFlag;
    }
    
    // mac
    public byte[] getMac()
    {
    	return mac;
    }
    
    public void setMac(byte[] mac)
    {
    	if(mac.length == 8)
    	{
    		System.arraycopy(mac, 0, this.mac, 0, mac.length);
    	}
    }
    
    // pinBlock
    public byte[] getPinBlock()
    {
    	return pinBlock;
    }
    
    public void setPinBlock(byte[] pinBlock)
    {
    	if(pinBlock != null && pinBlock.length == 8)
    	{
    		this.pinBlock = new byte[8];
    		System.arraycopy(pinBlock, 0, this.pinBlock, 0, 8);
    	}
    }
    
    // balance
    public long getBalance()
    {
    	return balance;
    }
    
    public void setBalance(long balance)
    {
   		this.balance = balance;
    }
    
    // masterKey
    public byte[] getMasterKey()
    {
    	return masterKey;
    }
    
    public void setMasterKey(byte[] masterKey)
    {
    	if(masterKey != null)
    	{
    		this.masterKey = new byte[masterKey.length];
    		System.arraycopy(masterKey, 0, this.masterKey, 0, masterKey.length);
    	}
    }
    
    // keyCheckSum
    public byte[] getKeyCheckSum()
    {
    	return keyCheckSum;
    }
    
    public void setKeyCheckSum(byte[] keyCheckSum)
    {
    	if(keyCheckSum != null)
    	{
    		this.keyCheckSum = new byte[keyCheckSum.length];
    		System.arraycopy(keyCheckSum, 0, this.keyCheckSum, 0, keyCheckSum.length);
    	}
    }
    public void setIssuerAuthData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		issuerAuthData = new byte[length];
    		System.arraycopy(data, offset, issuerAuthData, 0, length);
    	}
    }
    
    private String getBankNameFromID(String id)
    {
    	if(id.length()  > 0)
    	{
            String headerID = id.substring(0, 4);
    		if(headerID.equals("0100"))
            {
            	return "邮储银行"; 
            }
            else if(headerID.equals("0102"))
	        {
            	return "工商银行"; 
	        }
            else if(headerID.equals("0103"))
	        {
            	return "农业银行"; 
	        }
            else if(   headerID.equals("0104")
            		|| headerID.equals("6104")
            	   )
	        {
            	return "中国银行"; 
	        }
            else if(headerID.equals("0105"))
	        {
            	return "建设银行"; 
	        }
            else if(headerID.equals("0301"))
	        {
            	return "交通银行"; 
	        }
            else if(headerID.equals("0302"))
	        {
            	return "中信银行"; 
	        }
            else if(headerID.equals("0303"))
	        {
            	return "光大银行"; 
	        }
            else if(headerID.equals("0304"))
	        {
            	return "华夏银行"; 
	        }
            else if(headerID.equals("0305"))
	        {
            	return "民生银行"; 
	        }
            else if(headerID.equals("0306"))
	        {
            	return "广发银行"; 
	        }
            else if(   headerID.equals("0307")
            		|| headerID.equals("0410")
            	   )
	        {
            	return "平安银行"; 
	        }
            else if(   headerID.equals("0308")
            		|| headerID.equals("6308")
            	   )
	        {
            	return "招商银行"; 
	        }
            else if(headerID.equals("0309"))
	        {
            	return "兴业银行"; 
	        }
            else if(headerID.equals("0310"))
	        {
            	return "浦发银行"; 
	        }
            else if(headerID.equals("0320"))
	        {
            	return "东亚银行"; 
	        }
            else{
	        	if(GUANGDONG_CUP)
	        	{
	        		if(   headerID.equals("0413")
	        		   || headerID.equals("6413")
	        		  )
	        		{
	        			return "广州银行";
	        		}
	        		else if(   headerID.equals("0425")
	        				|| headerID.equals("0505")
	        			   )
	        		{
	        			return "东莞银行";
	        		}
	        		else if(headerID.equals("0437"))
	        		{
	        			return "华润银行";
	        		}
	        		else if(  headerID.equals("0489")
	        				||headerID.equals("6489")
	        			   )
	        		{
	        			return "南粤银行";
	        		}
	        		else if(headerID.equals("0495"))
	        		{
	        			return "柳州商行";
	        		}
	        		else if(headerID.equals("0578"))
	        		{
	        			return "华兴银行";
	        		}
	        		else if(  headerID.equals("1405")
	        				||headerID.equals("6505")
	        			   )
	        		{
	        			return "广州农商";
	        		}
	        		else if(headerID.equals("1407"))
	        		{
	        			return "南海农信";
	        		}
	        		else if(  headerID.equals("1408")
	        				||headerID.equals("6508")
	        			   )
	        		{
	        			return "顺德农商";
	        		}
	        		else if(headerID.equals("1415"))
	        		{
	        			return "东莞农商";
	        		}
	        		else if(headerID.equals("1421"))
	        		{
	        			return "三水农信";
	        		}
	        		else if(headerID.equals("1425"))
	        		{
	        			return "新会农商";
	        		}
	        		else if(headerID.equals("1426"))
	        		{
	        			return "肇庆农信";
	        		}
	        		else if(headerID.equals("1427"))
	        		{
	        			return "佛山农信";
	        		}
	        		else if(headerID.equals("1431"))
	        		{
	        			return "珠海农信";
	        		}
	        		else if(headerID.equals("1432"))
	        		{
	        			return "中山农信";
	        		}
	        		else if(headerID.equals("1450"))
	        		{
	        			return "广东农信";
	        		}
	        	}
	        }
    	}
    	return id;
    }
    
    // serviceCode
    public String getServiceCode()
    {
    	return serviceCode;
    }
    
    public void setServiceCode(String serviceCode)
    {
    	this.serviceCode = serviceCode;
    }
    
    // issuerName
    public String getIssuerName()
    {
    	return issuerName;
    }
    
    public void setIssuerName()
    {
    	issuerName = getBankNameFromID(issuerID);
    }
    
    // acquireerName
    public String getAcquirerName()
    {
    	return acquirerName;
    }
    
    public void setAcquirerName()
    {
    	acquirerName = getBankNameFromID(acquirerID);
    }

    // batchNumber
    public Integer getBatchNumber()
    {
    	return batchNumber;
    }
    
    public void setBatchNumber(Integer batchNumber)
    {
    	this.batchNumber = batchNumber;
    }
    
    // cardholderName
    public String getCardholderName()
    {
    	return cardholderName;
    }
    
    public void setCardholderName(String name)
    {
    	this.cardholderName = name;
    }
    
    public byte getParamType()
    {
    	return paramType;
    }
    
    public void setParamType(byte flag)
    {
    	this.paramType = flag;
    }
    
    // paramData
    public byte[] getParamData()
    {
    	return paramData;
    }
    
    public int getParamDataLength()
    {
    	if(paramData == null)
    	{
    		return 0;
    	}
    	return paramData.length;
    }
    
    public void clearParamData()
    {
    	paramData = null;
    }
   
    public void setParamData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		paramData = new byte[length];
    		System.arraycopy(data, offset, paramData, 0, length);
    	}
    }
    
    public void addParamData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		if(paramData == null)
    		{
    			paramData = new byte[length];
        		System.arraycopy(data, offset, paramData, 0, length);
    		}
    		else{
	    		byte[] tmpData = new byte[paramData.length];
	    		System.arraycopy(paramData, 0, tmpData, 0, tmpData.length);
	    		paramData = new byte[tmpData.length + length];
	    		System.arraycopy(tmpData, 0, paramData, 0, tmpData.length);
	    		System.arraycopy(data, offset, paramData, tmpData.length, length);
    		}
    	}
    }
    
    // paramOffset
    public int getParamOffset()
    {
    	return paramOffset;
    }
    
    public void setParamOffset(int offset)
    {
    	this.paramOffset = offset;
    }
    
    // paramNextFlag
    public byte getParamNextFlag()
    {
    	return paramNextFlag;
    }
    
    public void setParamNextFlag(byte flag)
    {
    	this.paramNextFlag = flag;
    }
    
    // blacklistCount
    public int getParamCount()
    {
    	return paramCount;
    }
    
    public void setParamCount(int count)
    {
    	this.paramCount = count;
    }
 // iccRevData
    public byte[] getICCRevData()
    {
    	return iccRevData;
    }
    
    public void setICCRevData(byte[] data, int offset, int length)
    {
    	if(   data != null
    	   && (offset + length) <= data.length
    	  )
    	{
    		iccRevData = new byte[length];
    		System.arraycopy(data, offset, iccRevData, 0, length);
    	}
    }
    
    
    public Object clone()
    {  
        TransDetailInfo cloneObject = null;  
        try {  
        	cloneObject = (TransDetailInfo) super.clone();  
        } catch (CloneNotSupportedException e) {  

        }  
        return cloneObject;  
    } 
}
