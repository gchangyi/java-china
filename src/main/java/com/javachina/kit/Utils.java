package com.javachina.kit;

import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.blade.kit.json.JSONKit;
import com.blade.mvc.http.Request;
import com.javachina.Constant;
import com.javachina.ext.Funcs;
import com.javachina.ext.markdown.BlockEmitter;
import com.javachina.ext.markdown.Configuration;
import com.javachina.ext.markdown.Processor;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class Utils {

	public static FamousDay getTodayFamous(){
		FamousDay famousDay = new FamousDay();
		String key = Constant.config.get("famous.key");
		if(StringKit.isNotBlank(key)){
			String body = HttpRequest.get("http://api.avatardata.cn/MingRenMingYan/Random?key=" + key).body();
			if(StringKit.isNotBlank(body)){
				String famous_saying = JSONKit.parseObject(body).get("result").asJSONObject().getString("famous_saying");
				String famous_name = JSONKit.parseObject(body).get("result").asJSONObject().getString("famous_name");
				famousDay.setFamous_saying(famous_saying);
				famousDay.setFamous_name(famous_name);
			}
		} else {
			famousDay.setFamous_saying("好奇的目光常常可以看到比他所希望看到的东西更多。");
			famousDay.setFamous_name("莱辛");
		}
		return famousDay;
	}

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(Request request) {
		if (null == request) {
			return "0.0.0.0";
		}
		String ip = request.header("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.header("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.header("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.address();
		}
		return ip;
	}
	
	/**
	 * 获取@的用户列表
	 * @param str
	 * @return
	 */
	public static Set<String> getAtUsers(String str){
		Set<String> users = new HashSet<String>();
		if(StringKit.isNotBlank(str)){
			Pattern pattern= Pattern.compile("\\@([a-zA-Z_0-9-]+)\\s");
			Matcher matcher = pattern.matcher(str);
			while(matcher.find()){
				users.add(matcher.group(1));
			}
		}
		
		return users;
	}

	public static boolean isEmail(String str){
		if(StringKit.isBlank(str)){
			return false;
		}
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 判断用户是否可以注册
	 * @param user_name
	 * @return
	 */
	public static boolean isSignup(String user_name){
		if(StringKit.isNotBlank(user_name)){
			user_name = user_name.toLowerCase();
			if(user_name.contains("admin") ||
					user_name.contains("test") ||
					user_name.contains("support")){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean isLegalName(String str){
		if(StringKit.isNotBlank(str)){
			Pattern pattern = Pattern.compile("^[a-zA-Z_0-9]{4,16}$");
			if(!pattern.matcher(str).find()){
				return false;
			}
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("resource")
	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {    
        FileChannel inputChannel = null;    
        FileChannel outputChannel = null;    
	    try {
	        inputChannel = new FileInputStream(source).getChannel();
	        outputChannel = new FileOutputStream(dest).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    } finally {
			assert inputChannel != null;
			inputChannel.close();
			assert outputChannel != null;
			outputChannel.close();
	    }
	}

	public static void run(Runnable t){
		Executors.newSingleThreadExecutor().submit(t);
	}

	final static Configuration config = Configuration.builder()
			.setSafeMode(true)
            .setCodeBlockEmitter(new CodeBlockEmitter())
            // Fenced code blocks are only available in 'extended mode'
            .forceExtentedProfile()
            .build();

    public static void upload(File file, String key) {

    }

    public static class CodeBlockEmitter implements BlockEmitter {
		@Override
		public void emitBlock(final StringBuilder out, final List<String> lines, final String meta) {
			out.append("<pre><code");
			if (meta != null && !meta.isEmpty()) {
//				out.append(" class=\"language-");
				out.append(" class=\"");
				out.append(meta);
				out.append('"');
			}
			out.append(">\r\n");
			for (final String l : lines) {
				escapedAdd(out, l);
				out.append('\n');
			}
			out.append("</code></pre>");
		}
	}

	public static void escapedAdd(final StringBuilder sb, final String str) {
		for (int i = 0; i < str.length(); i++) {
			final char ch = str.charAt(i);
			if (ch < 33 || Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
				sb.append(' ');
			} else {
				switch (ch) {
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				default:
					sb.append(ch);
					break;
				}
			}
		}
	}
	
	public static String markdown2html(String content) {
		if(StringKit.isBlank(content)){
			return content;
		}
		
		String member = Funcs.base_url("/member/");
		String content_ = content.replaceAll("@([a-zA-Z_0-9-]+)\\s", "<a href='"+ member +"$1'>@$1</a>&nbsp;");
		
		String processed = Processor.process(content_, config);

		assert processed != null;
		if(processed.contains("[mp3:")){
			processed = processed.replaceAll("\\[mp3:(\\d+)\\]", "<iframe frameborder='no' border='0' marginwidth='0' marginheight='0' width=330 height=86 src='http://music.163.com/outchain/player?type=2&id=$1&auto=0&height=66'></iframe>");
		}
		if(processed.contains("https://gist.github.com/")){
			processed = processed.replaceAll("&lt;script src=\"https://gist.github.com/(\\w+)/(\\w+)\\.js\">&lt;/script>", "<script src=\"https://gist.github.com/$1/$2\\.js\"></script>");
		}
		return Funcs.emoji(processed);
	}
	
	/**
	 * 计算帖子权重
	 * 
	 * 根据点赞数、收藏数、评论数、下沉数、创建时间计算
	 * 
	 * @param loves			点赞数：权重占比1
	 * @param favorites 	点赞数：权重占比2
	 * @param comment		点赞数：权重占比2
	 * @param sinks			点赞数：权重占比-1
	 * @param create_time	创建时间，越早权重越低
	 * @return
	 */
	public static double getWeight(Integer loves, Integer favorites, Integer comment, Integer sinks, Integer create_time) {
		
		long score = Math.max(loves - 1, 1) + favorites * 2 + comment * 2 - sinks;
		
		// 投票方向
		int sign = (score == 0) ? 0 : (score > 0 ? 1 : -1);
		
		// 帖子争议度
		double order = Math.log10(Math.max(Math.abs(score), 1));
		
		// 1459440000是项目创建时间
		double seconds = create_time - 1459440000;
		return Double.parseDouble(String.format("%.2f", order + sign * seconds / 45000));
	}


	/**
	 * 在字符串左侧填充一定数量的特殊字符
	 *
	 * @param o
	 *            可被 toString 的对象
	 * @param width
	 *            字符数量
	 * @param c
	 *            字符
	 * @return 新字符串
	 */
	public static String alignRight(Object o, int width, char c) {
		if (null == o)
			return null;
		String s = o.toString();
		int len = s.length();
		if (len >= width)
			return s;
		return new StringBuilder().append(dup(c, width - len)).append(s).toString();
	}

	/**
	 * 复制字符
	 *
	 * @param c
	 *            字符
	 * @param num
	 *            数量
	 * @return 新字符串
	 */
	public static String dup(char c, int num) {
		if (c == 0 || num < 1)
			return "";
		StringBuilder sb = new StringBuilder(num);
		for (int i = 0; i < num; i++)
			sb.append(c);
		return sb.toString();
	}

	public static String encrypt(String plainText, String encryptionKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return new BASE64Encoder().encode(encryptedBytes);
	}

	public static String decrypt(String cipherText, String encryptionKey) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] cipherTextBytes = new BASE64Decoder().decodeBuffer(cipherText);
		byte[] decValue = cipher.doFinal(cipherTextBytes);
		return new String(decValue);
	}

}
