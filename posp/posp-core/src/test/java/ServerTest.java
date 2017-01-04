
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ServerTest extends TestCase {

	
	@SuppressWarnings("unused")
	public void testService(){
//		PospApplicationContext.startupTest();
//		
//		UnipayDefineInitializer udi = (UnipayDefineInitializer)PospApplicationContext.getBean("UnipayDefineInitializer");
//		PacketDefineInitializer pdi = (PacketDefineInitializer)PospApplicationContext.getBean("PacketDefineInitializer");
//		try {
//			IsoMessage posISO = new IsoMessage();
//			
//			PKT_DEF[] pos_pkt = pdi.findPacketDefine("7001");
//			posISO.setMessageDefine(pos_pkt);
//			
//			PKT_DEF[] unipay_pkt = pdi.findPacketDefine("6001");
//			IsoMessage unipayISO = new IsoMessage();
//			unipayISO.setMessageDefine(unipay_pkt);
//			unipayISO.setLengthValueCompress(true);
////			String message = "0810003800000AC00014000062110935101630303030303030313239383330303131323030303031383337343438303130303030303030001100000001003f0040D93D887EE4B5828B17CA0E29FEF4DF62852D4176FC504AAED7C12ED25425D34AE914AA0EA39C1991";
////			//String message = "0810003800000AC00014000062110935101630303030303030313239383330303131323030303031383337343438303130303030303030001100000001003f0009303132333435363738";
////			unipayISO.setMessageWithHex(message);
////			unipayISO.printMessage();
////			
//			String ss= "3F887EE4B5828B173F29FEF4DF623F0004176FC504AAED73FD25425D34A3F3FA4471";
//			
//			byte[] dd = HexCodec.hexDecode(ss);
//			
//			ByteBuffer byteArray = ByteBuffer.allocate(dd.length);
//			byteArray.wrap(dd);
//			
//			//byteArray.write16bit(value, code, index)
//			//Byte []by = new Byte[100];
//			//by.
////			HexCodec.hexEncode(dd);
//			String hh = HexCodec.hexEncode(dd);
//			log.debug("-----"+hh);
			
			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		TSysParaService s = (TSysParaService)PospApplicationContext.getCtx().getBean("TSysParaServiceImpl");
//		log.debug(s.getSerialNo());
//		log.debug(s.getSerialNo());
//		log.debug(s.getSerialNo());
//		log.debug(s.getSerialNo());
//		log.debug(s.getSerialNo());

		
	}
	
	
	public static void main(String[] args) throws Exception{
		byte[] b = hexStringToBytes("A1B2C3D4");
		log.debug(""+b);
		String str = bytesToHexString(b);
		log.debug(str);
		log.debug(""+str.getBytes("GBK").length);
		log.debug(""+b.length);

	}
	
	public static String subStringByteArrayLen(String str, int len, String encoding){
		try {
			byte[] sba = str.getBytes(encoding);
			
			if(sba.length > len){
				byte[] b = new byte[len];
				for (int i = 0; i < len; i++) {
					b[i] = sba[i];
				}
				return new String(b, encoding);
			}
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}
	
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	} 
	
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}
	
}
