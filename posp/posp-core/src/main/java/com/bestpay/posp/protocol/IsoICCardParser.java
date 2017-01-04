package com.bestpay.posp.protocol;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bestpay.posp.protocol.util.HexCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsoICCardParser {
	public Map<String, String> hashMapTag = new HashMap<String, String>();

	public void setBuffer(String str) throws Exception {
		byte[] tagByte = HexCodec.hexDecode(str);
		hashMapTag.clear();
		for (int pos = 0; pos < tagByte.length;) {
			byte[] byTag = null;
			String tag = null;
			String tagValue = null;
			if (((tagByte[pos] & 0x1F) & 0xFF) == 0x1F) {
				byTag = new byte[2];
				byTag[0] = tagByte[pos];
				pos += 1;
				if (pos > tagByte.length)
					throw new ArrayIndexOutOfBoundsException("FORMAT_EXCEPTION: pos > tagByte.length");

				byTag[1] = tagByte[pos];
				pos += 1;
				if (pos > tagByte.length)
					throw new ArrayIndexOutOfBoundsException("FORMAT_EXCEPTION: pos > tagByte.length");

				tag = HexCodec.hexEncode(byTag);
			} else {
				byTag = new byte[1];
				byTag[0] = tagByte[pos];
				pos += 1;
				if (pos > tagByte.length)
					throw new ArrayIndexOutOfBoundsException("FORMAT_EXCEPTION: pos > tagByte.length");
				tag = HexCodec.hexEncode(byTag);
			}

			byte[] btLen = null;
			byte ch = tagByte[pos];
			if (((ch & 0x80) & 0xFF) == 0x80) {
				int L_len = (ch & 0x7F) & 0xFF;
				btLen = new byte[L_len];
				pos += 1; // 跳过1字节（L控制位）
				for (int i = 0; i < L_len; i++) {
					btLen[i] = tagByte[pos + i];
				}
				pos += L_len;
			} else {
				btLen = new byte[1];
				btLen[0] = tagByte[pos];
				pos += 1;
				if (pos > tagByte.length)
					throw new ArrayIndexOutOfBoundsException("FORMAT_EXCEPTION: pos > tagByte.length");
			}

			int len = HexCodec.bytesToInt(btLen);
			byte[] byTagValue = new byte[len];
			for (int i = 0; i < len; i++) {
				byTagValue[i] = tagByte[pos + i];
			}
			pos += len;
			tagValue = HexCodec.hexEncode(byTagValue);

			if (tag != null)
				hashMapTag.put(tag, tagValue);
		}

	}

	public void printMessage(String id) {
		for (Entry<String, String> entry : hashMapTag.entrySet()) {

			if (entry.getKey().equals("9F1E") || entry.getKey().equals("9F74")) {
				try {
					log.info(String.format("[%s]  [%-4s]: <%s> {%s}",
							id, entry.getKey(), entry.getValue(),
							new String(HexCodec.hexDecode(entry.getValue()),
									"GBK")));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					log.info(String.format("[%s]  %s", id, e.getMessage()));
				}
			} else
				log.info(String.format("[%s]  [%-4s]: <%s>", id,
						entry.getKey(), entry.getValue()));
		}
	}
}
