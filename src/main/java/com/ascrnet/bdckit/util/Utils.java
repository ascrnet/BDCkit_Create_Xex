package com.ascrnet.bdckit.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;

import com.ascrnet.bdckit.App;
import com.ascrnet.bdckit.model.PackArchivos;

public class Utils {

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static byte[] replace(byte[] src, byte[] find, byte[] replace) {
		String replaced = cutBrackets(Arrays.toString(src))
				.replace(cutBrackets(Arrays.toString(find)), cutBrackets(Arrays.toString(replace)));
		return Arrays.stream(replaced.split(", ")).map(Byte::valueOf).collect(toByteArray());
	}

	private static String cutBrackets(String s) {
		return s.substring(1, s.length() - 1);
	}

	public static String center(String text, int len){
		if (len <= text.length())
			return text.substring(0, len);
		int before = (len - text.length())/2;
		if (before == 0)
			return String.format("%-" + len + "s", text);
		int rest = len - before;
		return String.format("%" + before + "s%-" + rest + "s", "", text);  
	}

	public static byte[] PCtoAtari(String text)
	{

		byte[] btitle = new byte[text.length()];

		for(int i = 0; i < text.length(); i++)
		{
			if (text.charAt(i) >= 'a' && text.charAt(i) <= 'z') {
				btitle[i] = (byte) ((int) text.charAt(i)+64);
			}
			else if(text.charAt(i) >= '0' && text.charAt(i) <= '9') {
				btitle[i] = (byte) ((int) text.charAt(i)+96);
			}
			else if(text.charAt(i) == ' ') {
				btitle[i] = (byte) ((int) 32);
			}
			else if (text.charAt(i) >= 'A' && text.charAt(i) <= 'Z')  {
				btitle[i] = (byte) ((int) text.charAt(i)-32);
			};	
		}

		return btitle;
	}


	private static Collector<Byte, ?, byte[]> toByteArray() {
		return Collector.of(ByteArrayOutputStream::new, ByteArrayOutputStream::write, (baos1, baos2) -> {
			try {
				baos2.writeTo(baos1);
				return baos1;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}, ByteArrayOutputStream::toByteArray);
	}


	public static void copyFile(File dest, PackArchivos pack, String newtitle) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		Integer regIni = 24816;
		Integer regFin = 0;

		byte runxex[] = {
				(byte)0xE0, (byte)0x02, (byte)0xE1, (byte)0x02, (byte)0xB5, (byte)0x5B
		};

		byte newt[] = PCtoAtari(center(newtitle,20));

		byte title[] = {
				(byte)0x20, (byte)0x20, (byte)0x20, (byte)0xA2, (byte)0xB9, (byte)0x20,
				(byte)0xB0, (byte)0xA5, (byte)0xB4, (byte)0xA5, (byte)0xB2, (byte)0x20,
				(byte)0xAC, (byte)0xA9, (byte)0xA5, (byte)0xB0, (byte)0xA1, (byte)0x20,
				(byte)0x20, (byte)0x20
		};

		try {

			is = App.class.getClass().getResourceAsStream("/resource/engine.dat");
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				buffer = Utils.replace(buffer, title, newt);
				os.write(buffer, 0, length);
			}

			is.close();
			os.close();

			os = new FileOutputStream(dest,true);


			String ncave = Integer.toHexString(pack.size());
			if (ncave.length()==1) {
				ncave = "0"+Integer.toHexString(pack.size());
			}

			os.write(Utils.hexStringToByteArray(ncave));

			byte[] buffer2 = new byte[504];

			for (Map.Entry<String, String> entry : pack.get().entrySet()) {

				os.write(Utils.hexStringToByteArray(Integer.toHexString(regIni).substring(2, 4)));
				os.write(Utils.hexStringToByteArray(Integer.toHexString(regIni).substring(0, 2)));
				regFin = regIni + 503;
				os.write(Utils.hexStringToByteArray(Integer.toHexString(regFin).substring(2, 4)));
				os.write(Utils.hexStringToByteArray(Integer.toHexString(regFin).substring(0, 2)));
				regIni = regFin + 9;

				File aaa = new File(entry.getValue());
				is = new FileInputStream(aaa);
				while ((length = is.read(buffer2)) > 0) {
					os.write(buffer2, 0, length);
				}

				is.close();	 	

			}
			os.write(runxex,0,6);

		} finally {
			os.close();
		}
	}
}
