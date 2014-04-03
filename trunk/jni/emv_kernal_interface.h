#include <jni.h>
#ifndef uchar
#define uchar unsigned char
#endif

#ifndef ushort
#define ushort unsigned short
#endif

#ifndef uint
#define uint unsigned int
#endif

#define STATUS_ERROR 0
#define STATUS_CONTINUE 1
#define STATUS_COMPLETION 2

#define APPROVE_OFFLINE 1
#define APPROVE_ONLINE  2
#define DECLINE_OFFLINE 3
#define DECLINE_ONLINE  4

#define SUCCESS 0
#define ERROR_APP_NO_INFO 1
#define ERROR_NO_APP 2
#define ERROR_APP_ANALYSIS 3
#define ERROR_APP_BLOCKED 4
#define ERROR_APP_SELECT 5
#define ERROR_NO_AIPAFL 6
#define ERROR_INIT_APP 7
#define ERROR_OTHER_CARD 8
#define ERROR_EXPIRED_CARD 9
#define ERROR_APP_DATA 10
#define ERROR_AUTH_METHOD_BLOCKED 11
#define ERROR_REFDATA_INVALIDATED 12
#define ERROR_COND_NOT_SATISFIED 13
#define ERROR_FUNC_NOT_SUPPORTED 14
#define ERROR_FILE_NOT_FOUND 15
#define ERROR_RECORD_NOT_FOUND 16
#define ERROR_REFDATA_NOT_FOUND 17
#define ERROR_SELFILE_INVALIDATED 18
#define ERROR_AUTH_FAILED 19
#define ERROR_COUNTER_X 20
#define ERROR_BLOCKED_NOSEL 21
#define ERROR_ANALYSIS 22
#define ERROR_READ_DATA 23
#define ERROR_GEN_RANDOM 24
#define ERROR_GEN_DOLBLOCK 25
#define ERROR_GEN_AC 26
#define ERROR_NO_CDOL1 27
#define ERROR_NO_CDOL2 28
#define ERROR_LOGIC 29
#define ERROR_CHIP_CANNOT_BE_READ 30
#define ERROR_PROCESS_CMD 31
#define ERROR_AAR_ABORTED 32
#define ERROR_LOG_FILE 33
#define ERROR_SERVICE_NOT_ALLOWED 34
#define ERROR_PINENTERY_TIMEOUT 35
#define ERROR_OFFLINE_VERIFY 36
#define ERROR_NEED_ADVICE 37
#define ERROR_USER_CANCELLED 38

#define EMV_START 1
#define EMV_CANDIDATE_LIST 2
#define EMV_APP_SELECTED 3
#define EMV_GET_PROC_OPTION 4
#define EMV_READ_APP_DATA 5
#define EMV_DATA_AUTH 6
#define EMV_PROCESS_RESTRICT 7
#define EMV_ONLINE_ENC_PIN 8
#define EMV_PIN_BYPASS_CONFIRM 9
#define EMV_CARDHOLDER_VERIFY 10
#define EMV_TERMINAL_RISK_MANAGEMENT 11
#define EMV_TRANSACTION_PROCESS 12
#define EMV_PROCESS_ONLINE  13
#define EMV_ID_CHECK  14
#define EMV_END 15

#define TRANS_GOODS_SERVICE      0x00
#define TRANS_CASH 0x01
#define TRANS_INQUIRY 0x04
#define TRANS_TRANSFER 0x05
#define TRANS_PAYMENT 0x06
#define TRANS_ADMIN 0x07
#define TRANS_CASHBACK 0x09
#define TRANS_CARD_RECORD 0x0A
#define TRANS_EC_BALANCE 0x0B

typedef struct 
{
	uchar aidLength;
	uchar aid[16];   				// Application Identifier
	uchar appLabel[16+1];			// Application Label
	uchar appPreferredName[16+1];		// Application Preferred Name
	uchar PriorityExist;				// Application Priority exist flag
	uchar appPriority;				// Application Priority
}CandidateAppSTRU;

typedef struct
{
	uchar  aidLength;
	uchar aid[16];			// Application Identifier
	uchar appLable[16+1];		// Application Label
	/** Application Preferred Name */
	uchar appPreferredName[16+1];
	uchar appPriority;		// Application Priority
	/** Terminal Floor Limit */
	uchar termFloorLimit[4];   // Hex
	/** Terminal Action Code - Default */
	uchar termActionCodeDefault[5];
	/** Terminal Action Code - Denial */
	uchar termActionCodeDenial[5];
	/** Terminal Action Code - Online */
	uchar termActionCodeOnline[5];
	/** Target Percentage */
	uchar targetPercentage;
	/** threshold Value */
	uchar thresholdValue[4];	 // Hex
	/** Maximum Target Percentage */
	uchar maxTargetPercentage;
	/** Acquirer Identifier */
	uchar acquirerId[6];
	/** Merchant Category Code */
	uchar mechantCategoryCode[2];
	/** Merchant Identifier */
	uchar merchantId[15];
	/** Application Version Number */
	uchar appVersionNumber[2];
	/** Point-of-Service(POS) Entry Mode */
	uchar posEntryMode;	
	/** Transaction Reference Currency Code */
	uchar transReferCurrencyCode[2];
	/** Transaction Reference Currency Exponent */
	uchar transReferCurrencyExponent;
	/** Default Dynamic Data Authentication Data Object List(DDOL) */
	uchar defaultDDOLLength;
	uchar defaultDDOL[128];	
	uchar defaultTDOLLength;
	/** Default Transaction Certificate Data Object List(TDOL) */
	uchar defaultTDOL[128];
	// supportOnlinePin[0] = 0 means the Application unsupported 
	// online PIN, any other value means the Application supported 
	// online PIN
	uchar supportOnlinePin;
	// supportAIDPartial[0] = 0 means the Application unsupported AID // Partial, any other value means the Application supported AID // Partial
	uchar supportAIDPartial;
	/** Option effective Flag */
	uchar optionEffectiveFlag;
}AIDParam;

typedef struct
{
	/** Registered Application Provider Identifier */
	uchar rid[5];
	/** Certificate Authority Public Key Index */
	uchar capki;
	/** Hash Algorithm Indicator */
	uchar hashInd;
	/** Certificate Authority Public Key Algorithm Indicator */
	uchar arithInd;
	/** The Length of Certificate Authority Public Key Modulus */
	u_int32_t modulLen;
	/** Certificate Authority Public Key Modulus */
	uchar modul[248];
	/** The Length of Certificate Authority Public Key Exponent */
	uchar exponentLen;
	/** Certificate Authority Public Key Exponent */
	uchar exponent[3];
	/** Certificate Authority Public Key Check Sum */
	uchar checkSum[20];
	/** Certificate Expiration Date */
	uchar expiry[8];
}CAPKParam;

typedef struct
{
	uchar terminal_country_code[2];             // 9F1A: Terminal Country Code
	uchar TID[8];								// 9F1C
	uchar IFD[8];                               // 9F1E: IFD Serial Number
	uchar transaction_currency_code[2];			// 5F2A
	uchar terminal_capabilities[3];             // 9F33
	uchar terminal_type[1];						// 9F35
	uchar transaction_currency_exponent[1];		// 5F36
	uchar additional_terminal_capabilities[5];  // 9F40
	uchar merchantNameLength;
	uchar merchantName[20]; // 9F4E 
	uchar ECTermTransLimit[6]; // 9F7B
}TERMINAL_INFO;

typedef struct
{
	/** PAN */
	uchar cardNo[19];
	/** PAN Sequence Number */
	uchar panSequence;
}BlackCard;

typedef struct
{
	/** Registered Application Provider Identifier */
	uchar rid[5];
	/** Certificate Authority Public Key Index */
	uchar capki;
}BlackCert;

typedef int (*SMART_CARD_TRANSMIT)(int nHandle, unsigned char* pAPDU, unsigned int nAPDULength, unsigned char* pResponse, unsigned int *pResponseLength);
typedef void (*EMV_PROCESS_NEXT_COMPLETED)(uchar status, uchar info);
typedef struct
{
	SMART_CARD_TRANSMIT pTransmitPointer;
	int pReaderHandle;
	unsigned char  cardType; // CARD CONTACT: 1 ;  CARD_CONTACTLESS: 2
	unsigned short ATRLength;
	unsigned char ATR[30];
	EMV_PROCESS_NEXT_COMPLETED pEMV_ProcessNextCompleted;
}EMV_INIT_PARAM;

typedef uchar (*EMV_IS_TAG_PRESENT) (ushort  tag);                              // 1.1
typedef uchar (*EMV_GET_TAG_DATA )(uchar *data, ushort *length, ushort tag);    // 1.2
typedef uchar (*EMV_SET_TAG_DATA) (uchar *data, ushort length, ushort tag);     // 1.3
typedef void (*EMV_INITIALIZE)(uchar kernelType, void* parameter);              // 2.1
typedef uchar (*EMV_PROCESS_NEXT)();                                            // 2.2
typedef void (*EMV_GET_VERSION_STRING)(uchar* buffer);                          // 3.1
typedef void (*EMV_SET_TRANS_AMOUNT)(uint amount);                              // 3.2
typedef void (*EMV_SET_TRANS_TYPE)(uchar transType);                            // 3.3
typedef void (*EMV_SET_OTHER_AMOUNT)(uint amount);                              // 3.4
typedef uchar (*EMV_AIDPARAM_CLEAR)();                                          // 3.5
typedef uchar (*EMV_AIDPARAM_ADD)( uchar* AIDParam);                            // 3.6
typedef uchar (*EMV_CAPKPARAM_CLEAR)();                                         // 3.7
typedef uchar (*EMV_CAPKPARAM_ADD)( uchar* CAPKParam);                          // 3.8
typedef uchar (*EMV_TERMINAL_PARAM_SET)( uchar * TerminalParam);                // 3.9
typedef uchar (*EMV_BLACK_CARD_CLEAR)();                                        // 3.10
typedef uchar (*EMV_BLACK_CARD_ADD)( uchar* BlackCard);                         // 3.11
typedef uchar (*EMV_BLACK_CERT_CLEAR)();                                        // 3.12
typedef uchar (*EMV_BLACK_CERT_ADD)( uchar* BlackCert);                         // 3.13
typedef uchar (*IS_NEED_ADVICE)();                                              // 3.14
typedef uchar (*IS_NEED_SIGNATURE)();                                           // 3.15
typedef void (*EMV_SET_FORCE_ONLINE)(uchar flag);                               // 3.16
typedef uchar (*EMV_GET_CARD_RECORD)(uchar *recordNo, uchar *data);             // 3.17
typedef uchar (*EMV_GET_CANDIDATE_LIST)(uchar *aidNumber, uchar *aidList);      // 3.18
typedef uchar (*EMV_SET_CANDIDATE_LIST_RESULT)(uchar index);                    // 3.19
typedef uchar (*EMV_SET_ID_CHECK_RESULT)(uchar result);                         // 3.20
typedef uchar (*EMV_SET_ONLINE_PIN_ENTERED)(uchar result);                      // 3.21
typedef uchar (*EMV_SET_PINBYPASS_CONFIRMED)(uchar result);                     // 3.22
typedef uchar (*EMV_SET_ONLINE_RESULT)(uchar result, uchar *issuerRespData, uchar issuerRespDataLength); // 3.23


typedef struct emv_kernel_interface
{
	EMV_IS_TAG_PRESENT				EMV_IsTagPresent;    		// 1.1
	EMV_GET_TAG_DATA				EMV_GetTagData;      		// 1.2
	EMV_SET_TAG_DATA				EMV_SetTagData;      		// 1.3
	EMV_INITIALIZE					EMV_Initialize;      		// 2.1
	EMV_PROCESS_NEXT				EMV_ProcessNext;     		// 2.2
	EMV_GET_VERSION_STRING			EMV_GetVersionString;		// 3.1
	EMV_SET_TRANS_AMOUNT			EMV_SetTransAmount;  		// 3.2
	EMV_SET_TRANS_TYPE				EMV_SetTransType;    		// 3.3
	EMV_SET_OTHER_AMOUNT			EMV_SetOtherAmount;  		// 3.4
	EMV_AIDPARAM_CLEAR				EMV_AIDPARAM_Clear;  		// 3.5
	EMV_AIDPARAM_ADD				EMV_AIDPARAM_Add;    		// 3.6
	EMV_CAPKPARAM_CLEAR				EMV_CAPKPARAM_Clear; 		// 3.7
	EMV_CAPKPARAM_ADD				EMV_CAPKPARAM_Add;   		// 3.8
	EMV_TERMINAL_PARAM_SET			EMV_TerminalPARAM_Set;		// 3.9
	EMV_BLACK_CARD_CLEAR			EMV_BlackCard_Clear;		// 3.10
	EMV_BLACK_CARD_ADD				EMV_BlackCard_Add;		    // 3.11
	EMV_BLACK_CERT_CLEAR			EMV_BlackCert_Clear;		// 3.12
	EMV_BLACK_CERT_ADD				EMV_BlackCert_Add;		    // 3.13
	IS_NEED_ADVICE					isNeedAdvice;		        // 3.14
	IS_NEED_SIGNATURE				isNeedSignature;		    // 3.15
	EMV_SET_FORCE_ONLINE			EMV_SetForceOnline;		    // 3.16
	EMV_GET_CARD_RECORD				EMV_GetCardRecord;		    // 3.17
	EMV_GET_CANDIDATE_LIST          EMV_GetCandidateList;		// 3.18
	EMV_SET_CANDIDATE_LIST_RESULT   EMV_SetCandidateListResult;	// 3.19
	EMV_SET_ID_CHECK_RESULT         EMV_SetIDCheckResult;       // 3.20
	EMV_SET_ONLINE_PIN_ENTERED      EMV_SetOnlinePinEntered;    // 3.21
	EMV_SET_PINBYPASS_CONFIRMED     EMV_SetPinBypassConfirmed;  // 3.22
	EMV_SET_ONLINE_RESULT           EMV_SetOnlineResult;        // 3.23
	
	void*	pHandle;
	JNIEnv * g_jni_env;
	jclass g_jni_obj;
}EMV_KERNEL_INSTANCE;
