package cn.koolcloud.parameter;

import cn.koolcloud.APDefine;



public class TerminalConfig 
{
    
	private String  mid = "";
	private String  tid = "";
	private String  merchantName = "allinpay";
	private Integer trace = 1;
	// 通讯参数
	private String  tpdu = APDefine.TPDU;
	
	private Integer commTimeout = 60;
    // 交易功能
    private byte voidSaleSwitch = 1;
    private byte balanceSwitch = 1;
    private byte authSwitch = 1;
    private byte offlineSwitch = 1;
    private byte refundSwitch = 1;
    private byte cancelSwitch = 1;
    private byte authCompSwitch = 1;
    private byte voidCompSwitch = 1;
    private byte authSettleSwitch = 1;
    private byte upCashSwitch = 1;
    private byte autoLogoffSwitch = 1;
    private byte tipSwitch = 0;
    private Integer tipPercent = 15;
    private String keyIndex = "0";
   
	private byte    nextProcess = 0;
	private String  MAK = "";
	private String  TDK = "";
	private byte    batchStatus  = 0;
	private byte    settleFlag = 0;
	private Integer uploadTotal = 0;
	
    // terminal config
    public void loadTerminalConfig()
    {
    }
    
    // tid
    public String getTID()
    {
    	return tid;
    }
    
    public void setTID(String tid)
    {
    	this.tid = tid;
//    	editor.putString(tidTag, tid);
//    	editor.commit();
    }
    
    // mid
    public String getMID()
    {
    	return mid;
    }
    
    public void setMID(String mid)
    {
    	this.mid = mid;
//    	editor.putString(midTag, mid);
//    	editor.commit();
    }
    
    // merchantName
    public String getMerchantName()
    {
    	return merchantName;
    }
    
    public void setMerchantName(String merchantName)
    {
    	this.merchantName = merchantName;
//    	editor.putString(merchantNameTag, merchantName);
//    	editor.commit();
    }
    
    // trace
    public int getTrace()
    {
    	return trace;
    }
    
    public void setTrace(int trace)
    {
    	this.trace = trace;
    }
    
    
	// 通讯参数
    //    tpdu
    public String getTPDU()
    {
    	return tpdu;
    }
    
    public void setTPDU(String tpdu)
    {
    	this.tpdu = tpdu;
    }
    
    
	//    commTimeout
    public Integer getCommTimeout()
    {
    	return commTimeout;
    }
    
    public void setCommTimeout(int timeout)
    {
    	this.commTimeout = timeout;
//    	editor.putInt(commTimeoutTag, timeout);
//    	editor.commit();
    }

    // 交易功能
	//    voidSale
    public byte getVoidSaleSwitch()
    {
    	return voidSaleSwitch;
    }
    
    public void setVoidSaleSwitch(byte flag)
    {
    	this.voidSaleSwitch = flag;
    }
    
	//    balance
    public byte getBalanceSwitch()
    {
    	return balanceSwitch;
    }
    
    public void setBalanceSwitch(byte flag)
    {
    	this.balanceSwitch = flag;
    }
    
	//    auth
    public byte getAuthSwitch()
    {
    	return authSwitch;
    }
    
    public void setAuthSwitch(byte flag)
    {
    	this.authSwitch = flag;
    }
    
	//    offline
    public byte getOfflineSwitch()
    {
    	return offlineSwitch;
    }
    
    public void setOfflineSwitch(byte flag)
    {
    	this.offlineSwitch = flag;
//    	editor.putInt(offlineSwitchTag, offlineSwitch);
//    	editor.commit();
    }
    
	//    refund
    public byte getRefundSwitch()
    {
    	return refundSwitch;
    }
    
    public void setRefundSwitch(byte flag)
    {
    	this.refundSwitch = flag;
//    	editor.putInt(refundSwitchTag, refundSwitch);
//    	editor.commit();
    }
    
	//    cancel
    public byte getCancelSwitch()
    {
    	return cancelSwitch;
    }
    
    public void setCancelSwitch(byte flag)
    {
    	this.cancelSwitch = flag;
//    	editor.putInt(cancelSwitchTag, cancelSwitch);
//    	editor.commit();
    }
    
	//    authComp
    public byte getAuthCompSwitch()
    {
    	return authCompSwitch;
    }
    
    public void setAuthCompSwitch(byte flag)
    {
    	this.authCompSwitch = flag;
//    	editor.putInt(authCompSwitchTag, authCompSwitch);
//    	editor.commit();
    }
    
	//    voidComp
    public byte getVoidCompSwitch()
    {
    	return voidCompSwitch;
    }
    
    public void setVoidCompSwitch(byte flag)
    {
    	this.voidCompSwitch = flag;
//    	editor.putInt(voidCompSwitchTag, voidCompSwitch);
//    	editor.commit();
    }
    
	//    authSettle
    public byte getAuthSettleSwitch()
    {
    	return authSettleSwitch;
    }
    
    public void setAuthSettleSwitch(byte flag)
    {
    	this.authSettleSwitch = flag;
//    	editor.putInt(authSettleSwitchTag, authSettleSwitch);
//    	editor.commit();
    }
    
	//    upCash
    public byte getUpCashSwitch()
    {
    	return upCashSwitch;
    }
    
    public void setUpCashSwitch(byte flag)
    {
    	this.upCashSwitch = flag;
//    	editor.putInt(upCashSwitchTag, upCashSwitch);
//    	editor.commit();
    }
    
	//    autoLogoff
    public byte getAutoLogoffSwitch()
    {
    	return autoLogoffSwitch;
    }
    
    public void setAutoLogoffSwitch(byte flag)
    {
    	this.autoLogoffSwitch = flag;
//    	editor.putInt(autoLogoffSwitchTag, autoLogoffSwitch);
//    	editor.commit();
    }
    
    // tipSwitch
    public byte getTipSwitch()
    {
    	return tipSwitch;
    }
    
    public void setTipSwitch(byte tipSwitch)
    {
    	this.tipSwitch = tipSwitch;
//    	editor.putInt(tipSwitchTag, tipSwitch);
//    	editor.commit();
    }
    
    // tipPercent
    public int getTipPercent()
    {
    	return tipPercent;
    }
    
    public void setTipPercent(int tipPercent)
    {
    	this.tipPercent = tipPercent;
//    	editor.putInt(tipPercentTag, tipPercent);
//    	editor.commit();
    }
    
    
    //    keyIndex
    public String getKeyIndex()
    {
    	return keyIndex;
    }
    
    public void setKeyIndex(String keyIndex)
    {
    	this.keyIndex = keyIndex;
//    	editor.putString(keyIndexTag, keyIndex);
//    	editor.commit();
    }
    
    
    // nextProcess
    public byte getNextProcess()
    {
    	return nextProcess;
    }
    
    public void setNextProcess(byte nextProcess)
    {
    	this.nextProcess = nextProcess;
//    	editor.putInt(nextProcessTag, nextProcess);
//    	editor.commit();
    }
    
    
    // MAK
    public String getMAK()
    {
    	return MAK;
    }
    
    public void setMAK(String key)
    {
    	this.MAK = key;
//    	editor.putString(MAKTag, MAK);
//    	editor.commit();
    }
    
    // TDK
    public String getTDK()
    {
    	return TDK;
    }
    
    public void setTDK(String key)
    {
    	this.TDK = key;
//    	editor.putString(TDKTag, TDK);
//    	editor.commit();
    }
    
    // for Settle
    // batchStatus
    public byte getBatchStatus()
    {
    	return batchStatus;
    }
    
    public void setBatchStatus(byte batchStatus)
    {
    	this.batchStatus = batchStatus;
//    	editor.putInt(batchStatusTag, batchStatus);
//    	editor.commit();
    }
    
    // settleFlag
    public byte getSettleFlag()
    {
    	return settleFlag;
    }
    
    public void setSettleFlag(byte settleFlag)
    {
    	this.settleFlag = settleFlag;
//    	editor.putInt(settleFlagTag, settleFlag);
//    	editor.commit();
    }
    
    // uploadTotal
    public int getUploadTotal()
    {
    	return uploadTotal;
    }
    
    public void setUploadTotal(int uploadTotal)
    {
    	this.uploadTotal = uploadTotal;
//    	editor.putInt(uploadTotalTag, uploadTotal);
//    	editor.commit();
    }
    
}
