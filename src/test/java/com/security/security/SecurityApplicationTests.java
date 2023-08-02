package com.security.security;

import com.security.security.proxy.ExchangeOpenFeign;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static com.security.security.SecurityApplicationTests.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class Version implements Comparable<Version> {
	String versionName;
	boolean snapShot;
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	Version(String version) {
		this.versionName = version;
		if(this.versionName == null) {
			throw new IllegalArgumentException(errorVersionMustNotBeNull);
		} else {
			boolean matches = version.matches("[\\d+(\\.\\d+){0,2}(-SNAPSHOT)?]");
			if(!matches) throw new IllegalArgumentException(errorVersionMustMatchPattern);
		}

	}

	boolean isSnapshot() {
		return this.snapShot;
	}

	@Override
	public int compareTo(Version other) {
		if(other.getVersionName() == null )
			throw new IllegalArgumentException(errorOtherMustNotBeNull);


		return 0;
	}
}

class SecurityApplicationTests {




	static final String errorVersionMustNotBeNull = "'version' must not be null!";
	static final String errorOtherMustNotBeNull =  "'other' must not be null!";
	static final String errorVersionMustMatchPattern =  "'version' must match: 'major.minor.patch(-SNAPSHOT)'!";
	@Autowired
	private ExchangeOpenFeign exchangeOpenFeign;

	@Test
	void contextLoads() {
		Version ver1 = new Version("ddd");
		ver1.snapShot = true;

		HashMap<String, String> body = new HashMap<>();
		body.put("name", "홍길동");
		body.put("regNo", "860824-1655068");
		JSONObject jsonObject = new JSONObject(body);
		System.out.println(jsonObject);

        Object result = exchangeOpenFeign.codeTest("Bearer 1", jsonObject.toString());
		System.out.println(result);
	}

}
