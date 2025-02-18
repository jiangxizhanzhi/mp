package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class SsoUtils {

	private static final String K316_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCmeqrm4yZUub1HELEjsLN+CTfnxnAuT0W705BjXohAmlc5W2E7ndWu6Snxpdj49fKzVTI5ciQHAtSw38PMntkkE2hVi1KdPtB7qQjf4Rn3koIHnpvUcynlEcT5D7t5iTrGKihkLv+mmzvtm22njPkl8QCYaR93tVACQWeqFuGQE3vUhNChkmT2QN72gFjDYvyYkaf7TYd12R6C4vZW/rcmkql32b6ai0qz7TGA1OlJYIlYUo0RBmz4ZOJcwukUJjJ+i8hW/vpZX4JThdSzYvPzqquGqgpnwlL4CT+4ZDj9z9h+ELmnNkMK4M03X7dyWdxPqEJBk3h15uxkfTJpLFoXAgMBAAECggEAJ1sWbdDDLgJe2Z77kWljdWcufS9vM7Nv09BW8PbXFRanihqtKM/ORTQ/wy2m1mwmPPjmD6iJFdM4frvXaNcglCO2ZYqzNBM2bBVw0KRBZEKRVMXNJvM2sz15KDvs4PVtHI240L4/l55ZE3nL+XEdMTgBIQaw9ra5PF9D53TTTCC8a7YmVmqfm8Usiu9qgLMVxPtDXF85Q4vznY5P4ixtzgPR6XRFHnTV2gdCq1X5GjX7iGraVjsvhUQphv01afYmWm6u+FE7t/RdQkDGsO+uAjCs3b5wJXiAGbLLTVSqTIJg20+P7uPfua2iir+VV7loJHczcPY2YG54qIiNne3BYQKBgQDt/Ms8g7w5srl8md8PFvFtLrH33bWUXYsoyed3sQcvxp42zfhStBbICAeBxYWvZXKhTNKHSi4L2p8GlJzrbN5qrCClDjHGzEtvDa2HXH5xJQs4NWCryE/RZNtptgno1TtZVKINPe9ZMoxh4NxyfdB0K4zCs4IpjC4KogukvNHE5wKBgQCzFFcuBkIRULOlUEmfdlGx6eogDWx/ocK+9HwsVhYya3HpqXbmpFhQ69M8DoL8wAFxCRCTM2WHKaReXYJvBNxWxkGsaeUcRCiPnoKLq9FYMebdTzdT4k69Wp7befe02bOnd9A1TT1aR2lzPxSLE4XKU4p9H5BFGpXAUNLobevrUQKBgENtRAe5KzMNuA/daJ1V2sEcYb/gSPnoJu0/hx0aQvb+RwUwBhMoguvZmobh96ZiD8Azi9JY6gLhxfyPToZFKh7+9aDODLxOVXD5Zxja09gqrqsWNHV3I2YoNSIWOWiGQXJl0CQAUDftYmi41Lx7ePvw+siPI2myFbldf/8EoEoxAoGAOyXGva7Xd3yeOUXRa1fbF9ZYvAlzApzdA9EgikxMvI58a17LpNY3TVL2y+lHHlAUqc8CqHvbVs9MONbN0/lZBVgqLNY1FCb29qi2GYfAd07UH9igP5aJx0T9HySjyhn1mqSaImDGmODpVWVKVb02/WKmZTldn1VujLylTJuxxmECgYAhrlfubPdpBk9AFwzLBZmoVrv99NDRYPF8Y6+B3g9pm6saGo3TWm+/YJACR/mJDX0CM+ATIEvPiPAE+Byi701awmBIfefUTfMiL+5M6dbivE0tLL16lDp4VooxkqPYi3owWkdRwiz72vdRJBzzX+B5Nl8kWaMusMVp7byz8mO9Iw==";

	private static final String JWT_SECRET = "+z^ob34h-svlr$6#)rp3&6odg*x!mnz*i7g_9_j)6^=xxvl0=^";

	private static final String ALGORITHM = "RSA";
	public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		return keyFactory.generatePrivate(keySpec);
	}

    public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	public static String decryptK316(String encryptedData) throws Exception {
		PrivateKey privateKey = loadPrivateKey(K316_PRIVATE_KEY);
		return decrypt(encryptedData, privateKey);
	}

	public static String validateJwt(String jwtStr) throws Exception{
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT jwt = verifier.verify(jwtStr);
		return jwt.getClaim("username").asString();
	}

	public static void addJWTCookieToResponse(HttpServletResponse response, String newJwt) {
		Cookie cookie = new Cookie("cr_jwttoken", null);
		cookie.setPath("/");
		cookie.setMaxAge(Integer.MAX_VALUE);
		// httponly = false
		cookie.setHttpOnly(false);
		try {
			cookie.setValue(URLEncoder.encode(newJwt, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new MpRuntimeException(AuthErrorCode.ACCOUNT_NOT_LOGIN);
		}
		response.addCookie(cookie);
	}
}
