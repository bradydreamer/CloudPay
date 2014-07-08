package cn.koolcloud.iso8583;

public class ISOField
{
  public static final byte FH_DCP = -4;
  public static final byte FH_IDENT = -3;
  public static final byte MTI = -2;
  public static final byte VERSION = -1;
  
  public static final byte F00_MSGID                       = 0;  
  public static final byte F01_BITMAP                      = 1;  
  public static final byte F02_PAN                         = 2;  
  public static final byte F03_PROC                        = 3;  
  public static final byte F04_AMOUNT                      = 4;  
  public static final byte F05                             = 5;  
  public static final byte F06                             = 6;  
  public static final byte F07                             = 7;  
  public static final byte F08                             = 8;  
  public static final byte F09                             = 9;  
  public static final byte F10                            = 10; 
  public static final byte F11_STAN                       = 11; 
  public static final byte F12_TIME                       = 12; 
  public static final byte F13_DATE                       = 13; 
  public static final byte F14_EXP                        = 14; 
  public static final byte F15_SETTLE_DATE                = 15; 
  public static final byte F16                            = 16; 
  public static final byte F17                            = 17; 
  public static final byte F18_FUNCTION_CODE              = 18; 
  public static final byte F19                            = 19; 
  public static final int F20                             = 20; 
  public static final int F21                             = 21; 
  public static final int F22_POSE                        = 22; 
  public static final int F23                             = 23; 
  public static final int F24_NII                         = 24; 
  public static final int F25_POCC                        = 25; 
  public static final int F26_CAPTURE                     = 26; 
  public static final int F27                             = 27; 
  public static final int F28                             = 28; 
  public static final int F29                             = 29; 
  public static final int F30                             = 30; 
  public static final int F31                             = 31; 
  public static final int F32_ACQUIRER                    = 32; 
  public static final int F33                             = 33; 
  public static final int F34                             = 34; 
  public static final int F35_TRACK2                      = 35; 
  public static final int F36_TRACK3                      = 36; 
  public static final int F37_RRN                         = 37; 
  public static final int F38_AUTH                        = 38; 
  public static final int F39_RSP                         = 39; 
  public static final int F40                             = 40; 
  public static final int F41_TID                         = 41; 
  public static final int F42_ACCID                       = 42;
  public static final int F43                             = 43;
  public static final int F44_ADDITIONAL                  = 44;
  public static final int F45_TRACK1                      = 45;
  public static final int F46                             = 46;
  public static final int F47                             = 47;
  public static final int F48                             = 48;
  public static final int F49_CURRENCY                    = 49;
  public static final int F50                             = 50;
  public static final int F51                             = 51;
  public static final int F52_PIN                         = 52;
  public static final int F53_SCI                         = 53;
  public static final int F54_TIP                         = 54;
  public static final int F55_ICC                         = 55;
  public static final int F56                             = 56;
  public static final int F57                             = 57;
  public static final int F58                             = 58;
  public static final int F59                             = 59;
  public static final int F60                             = 60;
  public static final int F61                             = 61;
  public static final int F62                             = 62;
  public static final int F63                             = 63;
  public static final int F64_MAC                         = 64;

  public static final int[][] bitMap = {

	  /* 00 BALANCE */
   	  { 
 		  	F02_PAN,    F03_PROC, 
 			F11_STAN,   F14_EXP, 
 			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
 			F35_TRACK2, F36_TRACK3, F39_RSP, F40,
 			F41_TID,    F42_ACCID,  F49_CURRENCY,
 			F52_PIN,    F53_SCI,    F55_ICC,
 			F60,        F64_MAC
 	  },
	  
      /* 01 SALE */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, /*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  
	  /* 02 VOID */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	F38_AUTH, 
			F40,		F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
	  },
	  
      /* 03 REFUND */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F63,          F64_MAC
      },
      
      /* 04 AUTH */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F64_MAC
      },
      
      /* 05 ADD_AUTH */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,      F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
      },
      
      /* 06 AUTH_CANCEL */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
      },
      
      /* 07 AUTH_SETTLEMENT */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
      },
     
      /* 08 AUTH_COMPLETE */
      { 
  			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
      },
      
      /* 09 AUTH_COMPLETE_CANCEL */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
      },
      
      /* 10 OFFLINE */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC, 
			F38_AUTH,   
			F41_TID,    F42_ACCID,  F48,        F49_CURRENCY,
			F60,        F61,        F63,        F64_MAC
      },
      
      /* 11 ADJUST */
      {
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC, 
			F37_RRN,    F38_AUTH,   
			F41_TID,    F42_ACCID,  F48,        F49_CURRENCY,
			F60,        F61,        F63,        F64_MAC
      },
      
      /* 12 LOGIN */
   	  { 
   			F11_STAN,
   			F41_TID,    F42_ACCID,
   			F60,        /*F62,*/        F63
      },
      
      /* 13 LOGOUT */
      { 
 			F11_STAN,
   			F41_TID,    F42_ACCID,
   			F60
      },
      
      /* 14 VOID_SALE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
	  },
	  
	  /* 15 VOID_OFFLINE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F60,        F61,        F64_MAC
	  },
	  
      /* 16 ADJUST_SALE */
      {
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC, 
			F37_RRN,    F38_AUTH,   
			F41_TID,    F42_ACCID,  F48,        F49_CURRENCY,
			F60,        F61,        F63,        F64_MAC
      },
      
      /* 17 ADJUST_OFFLINE */
      {
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC, 
			F37_RRN,    F38_AUTH,   
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F60,        F61,        F63,        F64_MAC
      },

      /* 18 分期消费 */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,      F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F62,        F64_MAC
	  },
      
      /* 19 分期撤销 */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
	  },
      
      /* 20 TRAN_BONUS_SALE */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,      F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F62,        F64_MAC
	  },
      
      /* 21 TRAN_BONUS_VOID_SALE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F64_MAC
	  },
	  
	  /* 22 TRAN_BONUS_QUERY */
   	  { 
 		  	F02_PAN,    F03_PROC, 
 			F11_STAN,   F14_EXP, 
 			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
 			F35_TRACK2, F36_TRACK3, F39_RSP,
 			F41_TID,    F42_ACCID,  F49_CURRENCY,
 			F52_PIN,    F53_SCI, 
 			F60,        F64_MAC
 	  },
 	  
      /* 23 TRAN_BONUS_REFUND */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F61,        F63,          F64_MAC
      },
      
      /* 24 TRAN_RESERV_SALE */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,      F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F62,        F64_MAC
	  },
      
      /* 25 TRAN_RESERV_VOID_SALE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
	  },
      
      /* 26 TRAN_MOTO_SALE */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,      F39_RSP,
			F41_TID,    F42_ACCID,  F48,           F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F62,        F64_MAC
	  },
      
      /* 27 TRAN_MOTO_VOID_SALE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
	  },
      
      /* 28 TRAN_MOTO_REFUND */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F63,          F64_MAC
      },
      
      /* 29 TRAN_MOTO_AUTH */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F64_MAC
      },
      
      /* 30 TRAN_MOTO_CANCEL */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
      },
      
      /* 31 TRAN_MOTO_AUTH_COMP */
      { 
  			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
      },
      
      /* 32 TRAN_MOTO_VOID_COMP */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F37_RRN,	  F38_AUTH, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
      },
      
      /* 33  TRAN_MOTO_AUTH_SETTLE */
      { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3, F38_AUTH,     F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI, 
			F60,        F61,        F64_MAC
      },
      
      /* 34 TRAN_EC_SALE */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,
			F35_TRACK2, F36_TRACK3, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,    F55_ICC,
			F60,        F62,        F64_MAC
	  },
      
      /* 35 TRAN_EC_REFUND */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,
			F35_TRACK2, F36_TRACK3, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,    F55_ICC,
			F60,        F62,        F64_MAC
	  },
      
      /* 36 TRAN_EC_CASH_SAVING */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,
			F35_TRACK2, F36_TRACK3, F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,    F55_ICC,
			F60,        F62,        F64_MAC
	  },
      
      /* 37 TRAN_EC_VOID_SAVING */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,
			F35_TRACK2, F36_TRACK3, F37_RRN,           F38_AUTH,        F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,    F55_ICC,
			F60,        F61,        F64_MAC
	  },
      
      /* 38 TRAN_EC_LOAD */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,      F26_CAPTURE,
			F39_RSP,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC,
			F60,        F64_MAC
	  },
      
      /* 39 TRAN_EC_LOAD_NOT_APPOINTED */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F23,        F25_POCC,      F26_CAPTURE,
			F35_TRACK2, F36_TRACK3, F39_RSP,
			F41_TID,    F42_ACCID,  F48,           F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC,
			F60,        F62,        F64_MAC
	  },
      
      /* 40 TRAN_MAG_LOAD_CASH_CHECK */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,
			F35_TRACK2, F36_TRACK3,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,
			F60,        F62,        F64_MAC
	  },
      
      /* 41 TRAN_MAG_LOAD_CASH */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,
			F35_TRACK2, F36_TRACK3, F37_RRN,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F53_SCI,
			F60,        F61,        F62,        F64_MAC
	  },
      
      /* 42 TRAN_MAG_LOAD_CASH_CON */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F60,        F64_MAC
	  },

      /* 43 TRAN_MAG_LOAD_ACCOUNT */
	  { 
			F02_PAN,    F03_PROC, 	F04_AMOUNT,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,   F26_CAPTURE, 
			F35_TRACK2, F36_TRACK3,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,
			F60,        F62,        F64_MAC
	  },
	        
      /* 44 TRAN_LOGIN_BONUS */
      {
			F02_PAN,
    	    F14_EXP,
    	    F22_POSE,
    	    F35_TRACK2, F36_TRACK3, 
   			F41_TID,    F42_ACCID,
   			F53_SCI,
   			F60
      },
      
      /* 45 CHECK_CARDHOLDER */
	  { 
			F02_PAN,    F03_PROC,
			F11_STAN,   F14_EXP, 
			F22_POSE,   F25_POCC,
			F35_TRACK2, F36_TRACK3,
			F41_TID,    F42_ACCID,  F48,     F49_CURRENCY,
			F53_SCI,
			F60,        F62,        F64_MAC
	  },
	  
	  /* 46 UPLOAD_MAG_OFFLINE */
      {
			F02_PAN,
    	    F14_EXP,
    	    F22_POSE,
    	    F35_TRACK2, F36_TRACK3, 
   			F41_TID,    F42_ACCID,
   			F53_SCI,
   			F60
      },
	  
	  /* 47 UPLOAD_PBOC_OFFLINE */
      {
			F02_PAN,
    	    F14_EXP,
    	    F22_POSE,
    	    F35_TRACK2, F36_TRACK3, 
   			F41_TID,    F42_ACCID,
   			F53_SCI,    F55_ICC,
   			F60
      },
      
      /* 48 UPLOAD_SCRIPT_RESULT */
      {
			F02_PAN,
    	    F14_EXP,
    	    F22_POSE,
    	    F35_TRACK2, F36_TRACK3, 
   			F41_TID,    F42_ACCID,
   			F53_SCI,    F55_ICC,
   			F60
      },
      
      /* 49 BATCH */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,        F49_CURRENCY,
   			F60,        F63
      },
      
      /* 50 TRAN_BATCH_UPLOAD_MAG_OFFLINE */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 51 TRAN_BATCH_UPLOAD_PBOC_OFFLINE_SUCC */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 52 TRAN_BATCH_UPLOAD_MAG_ONLINE */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 53 TRAN_BATCH_UPLOAD_MAG_ADVICE */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 54 TRAN_BATCH_UPLOAD_PBOC_ADVICE */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 55 TRAN_BATCH_UPLOAD_PBOC_ONLINE */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 56 TRAN_BATCH_UPLOAD_PBOC_OFFLINE_FAIL */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 57 TRAN_BATCH_UPLOAD_PBOC_RISK */
      { 
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60
      },
      
      /* 58 BATCH_END */
      {
			F11_STAN,
   			F41_TID,    F42_ACCID,  F48,
   			F60,
      },  
	  
      /* 59 TRAN_DOWN_PARAM */
   	  { 
   			F41_TID,    F42_ACCID,
   			F60,        F62
   	  },
    	  
      /* 60 TRAN_TESTING */
   	  { 
			F41_TID,    F42_ACCID,
   			F60
      },
      
      /* 61 UP_STATUS */
      { 
   			F41_TID,    F42_ACCID,
   			F60,        F62
      },
      
      /* 62 TRAN_DOWN_CAPK */
   	  { 
   			F41_TID,    F42_ACCID,
   			F60,        F62
   	  },
    	  
      /* 63 TRAN_DOWN_IC_PARAM */
   	  { 
			F41_TID,    F42_ACCID,
   			F60
      },
      
      /* 64 TRAN_DOWN_BLACKLIST */
      { 
   			F41_TID,    F42_ACCID,
   			F60
      },
      /* 65 TRAN_SALE 9121 协议支付 */
      { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			/*F35_TRACK2, F36_TRACK3, *//*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			/*F52_PIN, */   F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  /* 66 TRAN_SALE_9100 现金消费 */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			/*F35_TRACK2, F36_TRACK3, *//*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			/*F52_PIN, */   F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  /* 67 TRAN_SALE_9110 账户消费 */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			/*F35_TRACK2, F36_TRACK3, *//*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			/*F52_PIN, */   F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  /* 68 TRAN_SALE_9130 会员卡消费 */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			/*F35_TRACK2, F36_TRACK3, *//*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			F52_PIN,    F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  /* 69 TRAN_SALE_9140 优惠券消费 */
	  { 
		  	F02_PAN,    F03_PROC, 	F04_AMOUNT,
		  	F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F26_CAPTURE, 
			/*F35_TRACK2, F36_TRACK3, *//*F38_AUTH, */     F39_RSP,
			F40,
			F41_TID,    F42_ACCID,  F49_CURRENCY,
			/*F52_PIN, */   F53_SCI,    F55_ICC, 
			F60,        F64_MAC
	  },
	  /*70 TRAN_SALE_REVERSAL pos消费冲正*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  },
	  /*71 TRAN_REVOCATION_REVERSAL pos撤销冲正*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  },
	  /*72 TRAN_AUTH_REVERSAL*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  },
	  /*73 TRAN_AUTH_CANCEL_REVERSAL*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  },
	  /*74 TRAN_AUTH_COMPLETE_REVERSAL*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  },
	  /*75 TRAN_AUTH_COMPLETE_CANCEL_REVERSAL*/
	  {
		    F02_PAN,    
		    F03_PROC, 	F04_AMOUNT, F11_STAN,   F14_EXP, 
		  	F22_POSE,   F23,        F25_POCC,   F32_ACQUIRER,
		  	F38_AUTH,	
			F39_RSP,	F40,		F41_TID,    F42_ACCID,
			F49_CURRENCY,F55_ICC, 
			F60,        F61,		F64_MAC
		  
	  }
  };

}