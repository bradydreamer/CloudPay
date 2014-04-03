package cn.koolcloud.test;

import cn.koolcloud.control.ISO8583Controller;
import cn.koolcloud.iso8583.ISOPackager;
import cn.koolcloud.util.StringUtil;

public class Test {
	
	private static final String data = "00 A1 60 00 00 06 15 60 31 00 31 00 01 02 10 60 3C 00 81 0A D0 8C 11 19 62 27 00 42 26 68 00 56 54 60 31 00 00 00 01 75 16 28 28 08 27 10 01 00 08 99 99 00 00 33 32 33 39 31 36 37 31 39 35 30 35 30 30 31 30 30 30 30 30 37 32 39 39 39 32 39 30 30 35 34 39 39 30 30 30 31 22 30 31 30 35 30 30 30 30 20 20 20 39 39 39 39 30 30 30 30 20 20 20 31 35 36 26 00 00 00 00 00 00 00 00 20 33 30 30 32 31 35 36 44 39 30 30 30 30 30 31 30 30 30 30 30 00 13 01 60 00 01 00 05 00 45 32 45 44 41 39 30 32 ";

	private static final String str = "00E560061500006031003100010200702004C021C0981116620048901003269600000000000000033300001210200012346200489010032696D49121201180400448010836463032313130303038202020202020202036463131313130303038202020202020202036463038313130303230323031333038323931303139333736393232303036463130313130303138323031333038323931303139333734353937364631333131303030343931353031303030303038323939393239303035343939303030313135364626000000000000000019226000010005000009904436353535414144";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ISO8583Controller iso = new ISO8583Controller("999290054990001","10000072",175,600001);
//		ISOPackager.F_MessageType[0] = 0x02;
//		ISOPackager.F_MessageType[0] = 0x10;
//		byte [] data1 = StringUtil.hexString2bytes(data);
//		iso.load(data1);
//		iso.getBanlance();
		String track2 = "6222804222601042940=40075205351020000";
		String track3 = "";
//		String acount = "6222804222601042940";
		byte [] pinblock = StringUtil.hexString2bytes("92 3C AD A9 46 F4 2D D2");
//		iso.purchaseChaXun(acount, track2, track3, pinblock);
		
////		交易类型 		签到 协议支付
//		iso.login();
//		System.out.println(iso.toString());
//		System.out.println("***************************************************************************");
//		
//		iso.purchaseHuiyuanKa("6225", "1002", "18101729231", pinblock);
//		System.out.println(iso.toString());
		byte [] payPwd = StringUtil.hexString2bytes("92 3C AD A9 46 F4 2D D2");
		byte [] authCode = StringUtil.hexString2bytes("92 3C AD A9 46 F4 2D D2");
//		iso.purchaseKuaiJie("130814103521", payPwd, authCode);
//		System.err.println(iso.toString());

//		
//		iso.purchaseShuaKa("6227004226680056546", track2, track3, pinblock);
//		
//		
//		String str = "60000006156031003100010210703c02810bd0881319622280422260104294000000000000000001230000931137250816491200000008999900003332323831313638353431353030013136463032313130303038333333343434202036463131313130303038333333343434202036463038313130303230323031333038313631313339353330333032303036463130313130303138323031333038313631313337303635323339364631323131303031337777772e62616964752e636f6d364631333131303030343931353031303030303037323939393239303035343939303030312220202020202020202020202020202020202020202020313536260000000000000000192200000100050000099000034355503638463039354135";
//		
//		iso.load(StringUtil.hexString2bytes(str));
//		System.out.println("返回码：" + iso.getResCode());
		
		Test.getFormatString(str);
//		byte[] iso8583 = StringUtil.hexString2bytes(str);
//		
//		iso.chongZheng(iso8583);
//		
//		
//		Test.getFormatString(str);
		
		
	}
	
	
	public static void getFormatString(String str){
		byte[] bytes = StringUtil.hexString2bytes(str);
		str ="";
		for(byte b:bytes){
			str += String.format("%02X ", b);
		}
		System.out.println(str);
	}

}
