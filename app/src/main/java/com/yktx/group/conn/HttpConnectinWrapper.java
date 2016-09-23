package com.yktx.group.conn;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


/**
 * 
 * @version 2011-02-20
 */
public class HttpConnectinWrapper {

	private static HttpThread httpThread = null;

	private static HttpThread httpImageThread = null;

	private HttpClient httpclient;

	public static String FilePath;
	
	private static int time = 0;

	/**
	 * �������󣬵��߳�ģʽ�첽����
	 * 
	 * @param uri
	 *            Ҫ�����url��ַ
	 * @param param
	 *            post��Ҫ���͵�json����
	 * @param httpPostListener
	 *            ����l�Ӽ�����
	 * @param httpMethod
	 *            http����ʽ��GET/POST��
	 */
	public void request(final String httpMethod, final String uri,
			final List<NameValuePair> param, final Map<String, String> params,final Map<String, File> files,
			final HttpPostListener httpPostListener) {
		if (httpclient != null)
			httpclient = null;
		httpThread = new HttpThread() {
			public void run() {
				// ÿ���µ�����������0
				time = 0;
				if ("POST".equals(httpMethod))// POST����
					doHttpPostJSON(uri, param, httpPostListener);
				else if ("GET".equals(httpMethod))// GET����
					doHttpGetJSON(uri, httpPostListener);
				else if ("PUT".equals(httpMethod))
					try {
						post(uri, params, files, httpPostListener);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					doHttpPutJSON(uri, param, httpPostListener);
			}
		};
		httpThread.start();
	}

	/**
	 * ������������ͼƬ,���յ�ǰ���uiչ���޲�������ͼƬ��̣��ʴ˴�ʹ�õ��߳�ģʽ
	 * 
	 * @param uri
	 *            Ҫ�����ͼƬ��ַ
	 * @param netImageListener
	 *            ��·l�Ӽ�����
	 */
	public static void requestImage(final String uri,
			final NetImageListener netImageListener) {
		httpImageThread = new HttpThread() {
			public void run() {
				doGetImage(uri, netImageListener);
			}
		};
		httpImageThread.start();
	}

	/**
	 * �����ȡͼƬ��������
	 * 
	 * @param uri
	 * @param netImageListener
	 */
	public static void doGetImage(String uri, NetImageListener netImageListener) {
		URL url = null;
		HttpURLConnection conn = null;
		HttpURLParam urlparam = null;
		try {
			if (UrlParams.isCmwap) {
				urlparam = new HttpURLParam(uri);
				uri = "http://10.0.0.172:80" + urlparam.getPath();
			}
			url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// if (UrlParams.isCmwap)
			// conn.setRequestProperty("X-Online-Host", urlparam.getHost()
			// + ":" + urlparam.getPort());
			conn.connect();
			InputStream is = conn.getInputStream();

			// ����ݶu�����������У�ֱ�Ӷ�ȡʱ���ܻᷢ���ȡ��ݲ���ȫ���
			int i;
			byte[] b1 = new byte[8 * 1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((i = is.read(b1)) != -1) {
				baos.write(b1, 0, i);
			}
			byte[] all_byte_array = baos.toByteArray();
			baos.close();
			baos = null;
			Bitmap bitmap = BitmapFactory.decodeByteArray(all_byte_array, 0,
					all_byte_array.length);
			netImageListener.connSuccess(bitmap);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			// httpPostListener.connFail(e.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			// httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			// httpPostListener.connFail("����IO�쳣�������ԣ�");
		} catch (Exception e) {
			e.printStackTrace();
			// httpPostListener.connFail(e.toString());
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}

	/**
	 * GET��ʽ����JSON���
	 * 
	 * @param uri
	 * @param httpPostListener
	 */
	private void doHttpGetJSON(String uri,
			HttpPostListener httpPostListener) {
		// URL url = null;
		// HttpURLConnection conn = null;
		// HttpURLParam urlparam = null;
		HttpGet httpGet = new HttpGet(uri);
		try {
			// if (UrlParams.isCmwap) {
			// urlparam = new HttpURLParam(uri);
			// uri = "http://10.0.0.172:80" + urlparam.getPath();
			// }
			httpclient = new DefaultHttpClient();
			// url = new URL(uri);
			// conn = (HttpURLConnection) url.openConnection();
			// conn.setRequestMethod("GET");
//			if (LoginActivity.cookies != null) {
//				httpGet.setHeader("Cookie", LoginActivity.cookies);
//				Log.i(  "aaa", "cookies.toString() = "
//						+ LoginActivity.cookies.toString());
//			}
			HttpResponse httpresponse = httpclient.execute(httpGet);

			// conn.setDoInput(true);
			// if (UrlParams.isCmwap)
			// conn.setRequestProperty("X-Online-Host", urlparam.getHost()
			// + ":" + urlparam.getPort());
			// conn.connect();
			// InputStream is = conn.getInputStream();
			String strResult = EntityUtils.toString(httpresponse.getEntity());
			// ����ݶu�����������У�ֱ�Ӷ�ȡʱ���ܻᷢ���ȡ��ݲ���ȫ���
			// int i;
			// byte[] b1 = new byte[8 * 1024];
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// while ((i = is.read(b1)) != -1) {
			// baos.write(b1, 0, i);
			// }
			// byte[] all_byte_array = baos.toByteArray();
			// baos.close();
			// baos = null;
			// String retSrc = new String(all_byte_array, "utf-8");
			// Log.i(  "motel168", "retSrc:" + retSrc);
			httpPostListener.connSuccess(strResult);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpGetJSON(uri, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail("����IO�쳣�������ԣ�");
			else
				doHttpGetJSON(uri, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} finally {
			// if (conn != null) {
			// conn.disconnect();
			// conn = null;
			// }
		}
	}

	/**
	 * POST��ʽ����JSON���
	 * 
	 * @param uri
	 * @param param
	 * @param httpPostListener
	 */
	private void doHttpPostJSON(String uri, List<NameValuePair> param,
			HttpPostListener httpPostListener) {
		URL url = null;
		HttpResponse httpresponse = null;
		httpclient = new DefaultHttpClient();

		HttpPost post = null;
		post = new HttpPost(uri);

		// ���cookie ��Ϊ�� l�ӵ�ʱ�����
//		if (LoginActivity.cookies != null) {
//			post.setHeader("Cookie", LoginActivity.cookies);
//			Log.i(  "aaa", "cookies.toString() = "
//					+ LoginActivity.cookies.toString());
//		}
		
		//���͵�httpheader ���

		
		
		try {

			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			httpresponse = httpclient.execute(post);
			// CookieStore cookies
			// =((AbstractHttpClient)httpclient).getCookieStore();//��cookie

			/**
			 * ����ǵ�½��״̬��ȡcookie LodingActivity.cookies -- CookieStore
			 * */
//			if (LoginActivity.isJion) {
//
//				for (int i = 0; i < httpresponse.getAllHeaders().length; i++) {
//					
//					String fileValue = httpresponse.getAllHeaders()[i]
//							.getValue().toString();
//					Log.i(  "aaa", "getAllHeaders = "+fileValue);
//					String cookie = Tools.spit(fileValue, "mg_auth");
//					if (cookie != null) {
//						LoginActivity.cookies = cookie;
//						Log.i(  "aaa", "LodingActivity.cookies ======== "
//								+ LoginActivity.cookies);
//
//					}
//				}
//
//			}
			
	     
			url = new URL(uri);
			String strResult = EntityUtils.toString(httpresponse.getEntity());
			httpPostListener.connSuccess(strResult);
		} catch (SocketTimeoutException e) {
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(uri, param, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(uri, param, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		}
	}
	
	

	/**
	 * POST��ʽ����JSON���
	 * 
	 * @param uri
	 * @param param
	 * @param httpPostListener
	 */
	private static void doHttpPutJSON(String uri, List<NameValuePair> param,
			HttpPostListener httpPostListener) {
		URL url = null;
		HttpURLConnection conn = null;
		try {
			// if (UrlParams.isCmwap) {
			// urlparam = new HttpURLParam(uri);
			// uri = "http://10.0.0.172:80" + urlparam.getPath();
			// }
			url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
//			if (LoginActivity.cookies != null) {
//				conn.addRequestProperty("Cookie", LoginActivity.cookies
//						.toString());
//				Log.i(  "aaa", "cookie = " + LoginActivity.cookies.toString());
//			}

			conn.setDoInput(true);
			conn.setDoOutput(true);
			// if (UrlParams.isCmwap )
			// conn.setRequestProperty("X-Online-Host", urlparam.getHost()
			// + ":" + urlparam.getPort());
			conn.connect();
			if (param != null) {
				OutputStream os = conn.getOutputStream();
				os.write(param.toString().getBytes("UTF-8"));
				os.flush();
				os.close();
			}
			int length = conn.getContentLength();
			InputStream is = conn.getInputStream();
			// ����ݶu�����������У�ֱ�Ӷ�ȡʱ���ܻᷢ���ȡ��ݲ���ȫ���
			int i;
			byte[] b1 = new byte[8 * 1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((i = is.read(b1)) != -1) {
				baos.write(b1, 0, i);
			}
			byte[] all_byte_array = baos.toByteArray();
			baos.close();
			baos = null;

			String retSrc = new String(all_byte_array, "utf-8");
			httpPostListener.connSuccess(retSrc);
		} catch (SocketTimeoutException e) {
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPutJSON(uri, param, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPutJSON(uri, param, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		}
	}
	
	
	
	
	/**
	 * POST��ʽ����JSON���
	 * 
	 * @param uri
	 * @param param
	 * @param httpPostListener
	 */
	private void doHttpPostFile(String uri, List<NameValuePair> param,
			HttpPostListener httpPostListener) {
		URL url = null;
		HttpResponse httpresponse = null;
		httpclient = new DefaultHttpClient();

		HttpPost post = null;
		post = new HttpPost(uri);
		
		// ���cookie ��Ϊ�� l�ӵ�ʱ�����
//		if (LoginActivity.cookies != null) {
//			post.setHeader("Cookie", LoginActivity.cookies);
//			Log.i(  "aaa", "cookies.toString() = "
//					+ LoginActivity.cookies.toString());
//		}
		try {
			
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			httpresponse = httpclient.execute(post);
			// CookieStore cookies
			// =((AbstractHttpClient)httpclient).getCookieStore();//��cookie

			/**
			 * ����ǵ�½��״̬��ȡcookie LodingActivity.cookies -- CookieStore
			 * */
			url = new URL(uri);
			/* ���j��ɹ�����ȡ������� */
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			con.setRequestMethod("POST");

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			File file = new File(FilePath);

			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;

			while ((length = fStream.read(buffer)) != -1) {

				ds.write(buffer, 0, length);
			}

			fStream.close();
			ds.flush();

			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			ds.close();

		String strResult = EntityUtils.toString(httpresponse.getEntity());
		Log.i(  "aaa", "strResult = " + strResult);
		httpPostListener.connSuccess(strResult);
			
			
		} catch (SocketTimeoutException e) {
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(uri, param, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(uri, param, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		}
	}
	
	
	public static String post(String actionUrl, Map<String, String> params,
			Map<String, File> files, HttpPostListener httpPostListener) 
		throws IOException { 
		
		Log.i(  "aaa", "post");
	    String BOUNDARY = java.util.UUID.randomUUID().toString();
	     
	    String PREFIX = "--" , LINEND = "\r\n";
	     
	    String MULTIPART_FROM_DATA = "multipart/form-data"; 
	    String CHARSET = "UTF-8";
	     

	    URL uri = new URL(actionUrl); 
	    HttpURLConnection conn = (HttpURLConnection) uri.openConnection(); 
	    conn.setReadTimeout(5 * 1000); // ������ʱ�� 
	    conn.setDoInput(true);// �������� 
	    conn.setDoOutput(true);// ������� 
	    conn.setUseCaches(false); // ������ʹ�û��� 
	    conn.setRequestMethod("POST"); 
	    conn.setRequestProperty("connection", "keep-alive"); 
	    conn.setRequestProperty("Charsert", "UTF-8"); 
	    conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY); 
//	    conn.setRequestProperty("cookie", LoginActivity.cookies); 
		try {
	    // ������ƴ�ı����͵Ĳ��� 
	    StringBuilder sb = new StringBuilder(); 
	    for (Map.Entry<String, String> entry : params.entrySet()) { 
	    sb.append(PREFIX); 
	    sb.append(BOUNDARY); 
	    sb.append(LINEND); 
	    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
	     
	    sb.append("Content-Type: text/plain; charset=" + CHARSET+LINEND);
	     
	    sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
	     
	    sb.append(LINEND);
	     
	    sb.append(entry.getValue()); 
	    sb.append(LINEND); 
	    } 

	    DataOutputStream outStream = new DataOutputStream(conn.getOutputStream()); 
	    outStream.write(sb.toString().getBytes()); 
	    // �����ļ���� 
    	Log.i(  "aaa", "files = "+files);
	    if(files != null){
	    	Log.i(  "aaa", "files = "+files);
		    for (Map.Entry<String, File> file : files.entrySet()) { 
			    StringBuilder sb1 = new StringBuilder(); 
			    sb1.append(PREFIX); 
			    sb1.append(BOUNDARY); 
			    sb1.append(LINEND); 
			    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getKey()+"\""+LINEND);
			     
			    sb1.append("Content-Type: application/octet-stream; charset="+CHARSET+LINEND);
			     
			    sb1.append(LINEND);
			     
			    outStream.write(sb1.toString().getBytes()); 
		
			    InputStream is = new FileInputStream(file.getValue());
			     
			    byte[] buffer = new byte[1024000]; 
			    int len = 0; 
			    while ((len = is.read(buffer)) != -1) {
			    	outStream.write(buffer, 0, len); 
			    } 
		
			    is.close(); 
			    outStream.write(LINEND.getBytes()); 
		    } 
	    }
	    //��������־
	     
	    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes(); 
	    outStream.write(end_data); 
	    outStream.flush(); 
	    // �õ���Ӧ�� 
	    int res = conn.getResponseCode(); 
	    if (res == 200) {
	     
	    InputStream in = conn.getInputStream(); 
	    int ch; 
	    StringBuilder sb2 = new StringBuilder(); 
	    while ((ch = in.read()) != -1) {
	    	sb2.append((char) ch); 
	    } 
	    httpPostListener.connSuccess(sb2.toString());
	    Log.i(  "aaa", "sb2 = "+sb2.toString());
	    }
	    
	    outStream.close(); 
	    conn.disconnect(); 
		} catch (SocketTimeoutException e) {
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				post(actionUrl, params, files, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				post(actionUrl, params, files, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		}
	    return "ok"; 
	    } 


	
	
	/**
	 * �ϴ�ͼƬ
	 */
	
	public static void uploadFile(String uri, List<NameValuePair> param,
			HttpPostListener httpPostListener,String imageFilePath) {
		String actionUrl = "http://14.63.215.38:8080/api/web/ImageUpload.aspx";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			con.setRequestMethod("POST");

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			File file = new File(imageFilePath);

			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;

			while ((length = fStream.read(buffer)) != -1) {

				ds.write(buffer, 0, length);
			}

			fStream.close();
			ds.flush();

			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	static class HttpThread extends Thread {

		HttpThread() {

		}

		@Override
		public void run() {

		}
	}

}
