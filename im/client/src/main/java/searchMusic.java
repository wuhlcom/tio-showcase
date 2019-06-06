import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by rain on 2017/2/24.
 */
public class searchMusic {
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = null;
		System.out.println("输入您要搜索的内容:");
		try {
			str = br.readLine();
		} catch (IOException e) {
			System.out.println("发生错误...");
		}

		// 创建默认的httpClient实例.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httppost = new HttpPost("http://music.163.com/api/search/pc");
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<>();
		formparams.add(new BasicNameValuePair("s", str));
		formparams.add(new BasicNameValuePair("offset", "0"));
		formparams.add(new BasicNameValuePair("limit", "20"));
		formparams.add(new BasicNameValuePair("type", "1"));
		//设置头信息
		httppost.setHeader("Referer", "http://music.163.com/");
		httppost.setHeader("Cookie", "appver=1.5.0.75771");
		httppost.setHeader("appver", "1.5.0.75771");
		UrlEncodedFormEntity uefEntity;
		try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);

			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					System.out.println("--------------------搜索歌曲列表------------------");
					//                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
					JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(entity, "UTF-8"));
					JSONObject result = jsonObject.getJSONObject("result");

					JSONArray jsonArrays = (JSONArray) result.get("songs");
					Map<Integer, String> map = new HashMap<>();
					for (int i = 0; i < jsonArrays.size(); i++) {
						//                        jsonArrays.getJSONObject(i).getJSONObject("");
						String name = jsonArrays.getJSONObject(i).get("name").toString();
						String zhuanji = jsonArrays.getJSONObject(i).getJSONObject("album").get("name").toString();
						String singer = jsonArrays.getJSONObject(i).getJSONObject("album").getJSONArray("artists").getJSONObject(0).get("name").toString();
						System.out.println(i + 1 + " [ 歌曲名称：" + name + " 演唱者：" + singer + " 专辑：" + zhuanji + "]");
						map.put(i + 1, jsonArrays.getJSONObject(i).get("mp3Url").toString());
					}
					System.out.println("------------------------------------------------");
					System.out.println("请输入数字编号来播放音乐:");
					try {
						str = br.readLine();
					} catch (IOException e) {
						System.out.println("发生错误...");
					}
					String url = map.get(Integer.parseInt(str));
					PlayerMusic p = new PlayerMusic(url);
					p.play();
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
