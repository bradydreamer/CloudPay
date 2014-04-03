 package cn.koolcloud.iso8583;

public class CUPPack
{
  private static final byte FFIX    = 0x00;		//定长
  private static final byte FLLVAR  = 0x01;		//LL: 	可变长域的长度值 ( 二位数 ) 		VAR:	可变长域
  private static final byte FLLLVAR = 0x02;		//LLL:	可变长域的长度值 ( 三位数 ) 		VAR:	可变长域

  private static final byte ATTBIN  = 0x00;		//16进制码
  private static final byte ATTN    = 0x04;		//BCD
  private static final byte ATTAN   = 0x08;		//ASCII 数字
  private static final byte ATTANS  = 0x0C;		//ASCII 数字加字符
  
  public static ISOTable[] isotable = {
      /* 000 */new ISOTable(0,  (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "MESSAGE TYPE"),
      /* 001 */new ISOTable(1,  (short)  64,( (byte)(FFIX   + ATTBIN  ) ) ), // "BIT MAP"),
      /* 002 */new ISOTable(2,  (short)  19,( (byte)(FLLVAR + ATTN    ) ) ), // "PAN - PRIMARY ACCOUNT NUMBER"),
      /* 003 */new ISOTable(3,  (short)   6,( (byte)(FFIX   + ATTN    ) ) ), // "PROCESSING CODE"),
      /* 004 */new ISOTable(4,  (short)  12,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, TRANSACTION"),
      /* 005 */new ISOTable(5,  (short)  12,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, SETTLEMENT"),
      /* 006 */new ISOTable(6,  (short)  12,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, CARDHOLDER BILLING"),
      /* 007 */new ISOTable(7,  (short)  10,( (byte)(FFIX   + ATTN    ) ) ), // "TRANSMISSION DATE AND TIME"),
      /* 008 */new ISOTable(8,  (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, CARDHOLDER BILLING FEE"),
      /* 009 */new ISOTable(9,  (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "CONVERSION RATE, SETTLEMENT"),
      /* 010 */new ISOTable(10, (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "CONVERSION RATE, CARDHOLDER BILLING"),
      /* 011 */new ISOTable(11, (short)   6,( (byte)(FFIX   + ATTN    ) ) ), // "SYSTEM TRACE AUDIT  NUMBER"),			受卡方系统跟踪号(POS终端交易流水)
      /* 012 */new ISOTable(12, (short)   6,( (byte)(FFIX   + ATTN    ) ) ), // "TIME, LOCAL TRANSACTION"),				受卡方所在地时间	hhmmss
      /* 013 */new ISOTable(13, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "DATE, LOCAL TRANSACTION"),				受卡方所在地日期	MMDD
      /* 014 */new ISOTable(14, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "DATE, EXPIRATION"),					
      /* 015 */new ISOTable(15, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "DATE, SETTLEMENT"),
      /* 016 */new ISOTable(16, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "DATE, CONVERSION"),
      /* 017 */new ISOTable(17, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "DATE, CAPTURE"),
      /* 018 */new ISOTable(18, (short)   4,( (byte)(FFIX   + ATTN    ) ) ), // "MERCHANTS TYPE"),
      /* 019 */new ISOTable(19, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "ACQUIRING  INSTITUTION COUNTRY CODE"),
      /* 020 */new ISOTable(20, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "PAN EXTENDED COUNTRY CODE"),
      /* 021 */new ISOTable(21, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "FORWARDING INSTITUTION COUNTRY CODE"),
      /* 022 */new ISOTable(22, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "POINT OF SERVICE  ENTRY MODE"),
      /* 023 */new ISOTable(23, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "CARD SEQUENCE  NUMBER"),
      /* 024 */new ISOTable(24, (short)   3,( (byte)(FFIX   + ATTN    ) ) ), // "NETWORK  INTERNATIONAL  IDENTIFIEER"),
      /* 025 */new ISOTable(25, (short)   2,( (byte)(FFIX   + ATTN    ) ) ), // "POINT OF SERVICE  CONDITION CODE"),
      /* 026 */new ISOTable(26, (short)   2,( (byte)(FFIX   + ATTN    ) ) ), // "POINT OF SERVICE PIN CAPTURE CODE"),
      /* 027 */new ISOTable(27, (short)   1,( (byte)(FFIX   + ATTN    ) ) ), // "AUTHORIZATION  IDENTIFICATION RESP  LEN"),
      /* 028 */new ISOTable(28, (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, TRANSACTION  FEE"),
      /* 029 */new ISOTable(29, (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, SETTLEMENT  FEE"),
      /* 030 */new ISOTable(30, (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, TRANSACTION  PROCESSING FEE"),
      /* 031 */new ISOTable(31, (short)   8,( (byte)(FFIX   + ATTN    ) ) ), // "AMOUNT, SETTLEMENT  PROCESSING FEE"),
      /* 032 */new ISOTable(32, (short)  11,( (byte)(FLLVAR + ATTN    ) ) ), // "ACQUIRING  INSTITUTION IDENT  CODE"),	受理方标识码
      /* 033 */new ISOTable(33, (short)  11,( (byte)(FLLVAR + ATTN    ) ) ), // "FORWARDING  INSTITUTION IDENT CODE"),
      /* 034 */new ISOTable(34, (short)  28,( (byte)(FLLVAR + ATTN    ) ) ), // "PAN EXTENDED"),
      /* 035 */new ISOTable(35, (short)  37,( (byte)(FLLVAR + ATTN    ) ) ), // "TRACK 2 DATA"),
      /* 036 */new ISOTable(36, (short) 104,( (byte)(FLLLVAR+ ATTN    ) ) ), // "TRACK 3 DATA"),
      /* 037 */new ISOTable(37, (short)  12,( (byte)(FFIX   + ATTAN   ) ) ), // "RETRIEVAL REFERENCE  NUMBER"),			检索参考号
      /* 038 */new ISOTable(38, (short)   6,( (byte)(FFIX   + ATTAN   ) ) ), // "AUTHORIZATION IDENTIFICATION RESPONSE"), 
      /* 039 */new ISOTable(39, (short)   2,( (byte)(FFIX   + ATTAN   ) ) ), // "RESPONSE CODE"),						应答码
      /* 040 */new ISOTable(40, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ), // "SERVICE RESTRICTION CODE"),TODO
      /* 041 */new ISOTable(41, (short)   8,( (byte)(FFIX   + ATTANS  ) ) ), // "CARD ACCEPTOR TERMINAL IDENTIFICACION"),终端代码(受卡机终端标识码)
      /* 042 */new ISOTable(42, (short)  15,( (byte)(FFIX   + ATTANS  ) ) ), // "CARD ACCEPTOR  IDENTIFICATION CODE" ),"),商户代码(受卡方标识码)
      /* 043 */new ISOTable(43, (short)  40,( (byte)(FFIX   + ATTANS  ) ) ), // "CARD ACCEPTOR  NAME/LOCATION"),
      /* 044 */new ISOTable(44, (short)  25,( (byte)(FLLVAR + ATTANS  ) ) ), // "ADITIONAL RESPONSE DATA"),
      /* 045 */new ISOTable(45, (short)  76,( (byte)(FLLVAR + ATTANS  ) ) ), // "TRACK 1 DATA"),
      /* 046 */new ISOTable(46, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ), // "ADITIONAL DATA - ISO"), (CUP Operator ID)
      /* 047 */new ISOTable(47, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ),
      /* 048 */new ISOTable(48, (short) 999,( (byte)(FLLLVAR+ ATTN    ) ) ),
      /* 049 */new ISOTable(49, (short)   3,( (byte)(FFIX   + ATTAN   ) ) ),
      /* 050 */new ISOTable(50, (short)   3,( (byte)(FFIX   + ATTAN   ) ) ),
      /* 051 */new ISOTable(51, (short)   3,( (byte)(FFIX   + ATTAN   ) ) ),
      /* 052 */new ISOTable(52, (short)   8,( (byte)(FFIX   + ATTBIN  ) ) ),
      /* 053 */new ISOTable(53, (short)  16,( (byte)(FFIX   + ATTN    ) ) ),
      /* 054 */new ISOTable(54, (short) 120,( (byte)(FLLLVAR+ ATTAN   ) ) ),
      /* 055 */new ISOTable(55, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ),
      /* 056 */new ISOTable(56, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ), 
      /* 057 */new ISOTable(57, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ),
      /* 058 */new ISOTable(58, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ),
      /* 059 */new ISOTable(59, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ),
      /* 060 */new ISOTable(60, (short) 999,( (byte)(FLLLVAR+ ATTN    ) ) ), // 自定义域	：交易类型码	批次号 网络管理信息码
      /* 061 */new ISOTable(61, (short) 999,( (byte)(FLLLVAR+ ATTN    ) ) ), // 冲正
      /* 062 */new ISOTable(62, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ), // 终端密钥
      /* 063 */new ISOTable(63, (short) 999,( (byte)(FLLLVAR+ ATTANS  ) ) ), // 自定义域	：操作员代码
      /* 064 */new ISOTable(64, (short)   8,( (byte)(FFIX   + ATTBIN  ) ) ) 
  };

  public CUPPack()
  {
  }

}
