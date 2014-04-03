package cn.koolcloud.test;

public class SplitString {
	
	static final String str = "6F02110008        6F11110008        6F08110020201308291504377346006F101100182013082915043748096F131100049150";

	/**
	 * @param apOrderId 通联订单号 F40 6F10
	 * @param payOrderBatch 批次号 F40 6F08
	 * 交易类型： 放到40域6F13标签
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String [] strs = str.split("6F");
		for(String s : strs){
			System.out.println(s);
			if(s.length()<2){
				continue;
			}
			String flag = s.substring(0, 2);
			System.out.println(flag);
			if(flag.equals("08")){
				String payOrderBatch = s.substring(8);
				System.err.println("payOrderBatch = "+payOrderBatch);
				continue;
			}
			if(flag.equals("10")){
				String apOrderId = s.substring(8);
				System.err.println("apOrderId = "+apOrderId);
				continue;
			}
			if(flag.equals("13")){
				String transType = s.substring(8);
				System.err.println("transType = "+transType);
				continue;
			}
			System.out.println();
		}
	}

}
