/*
 * Copyright (c) 2013-2024 Hutool Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.json.jwt.signers;

import org.dromara.hutool.core.map.BiMap;
import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.crypto.asymmetric.SignAlgorithm;
import org.dromara.hutool.crypto.digest.mac.HmacAlgorithm;

import java.util.HashMap;

/**
 * 算法工具类，算法和JWT算法ID对应表
 *
 * @author looly
 * @since 5.7.0
 */
public class AlgorithmUtil {

	private static final BiMap<String, String> map;

	static {
		map = new BiMap<>(new HashMap<>());
		map.put("HS256", HmacAlgorithm.HmacSHA256.getValue());
		map.put("HS384", HmacAlgorithm.HmacSHA384.getValue());
		map.put("HS512", HmacAlgorithm.HmacSHA512.getValue());

		map.put("HMD5", HmacAlgorithm.HmacMD5.getValue());
		map.put("HSHA1", HmacAlgorithm.HmacSHA1.getValue());
		map.put("SM4CMAC", HmacAlgorithm.SM4CMAC.getValue());

		map.put("RS256", SignAlgorithm.SHA256withRSA.getValue());
		map.put("RS384", SignAlgorithm.SHA384withRSA.getValue());
		map.put("RS512", SignAlgorithm.SHA512withRSA.getValue());

		map.put("ES256", SignAlgorithm.SHA256withECDSA.getValue());
		map.put("ES384", SignAlgorithm.SHA384withECDSA.getValue());
		map.put("ES512", SignAlgorithm.SHA512withECDSA.getValue());

		map.put("PS256", SignAlgorithm.SHA256withRSA_PSS.getValue());
		map.put("PS384", SignAlgorithm.SHA384withRSA_PSS.getValue());
		map.put("PS512", SignAlgorithm.SHA512withRSA_PSS.getValue());

		map.put("RMD2", SignAlgorithm.MD2withRSA.getValue());
		map.put("RMD5", SignAlgorithm.MD5withRSA.getValue());
		map.put("RSHA1", SignAlgorithm.SHA1withRSA.getValue());
		map.put("DNONE", SignAlgorithm.NONEwithDSA.getValue());
		map.put("DSHA1", SignAlgorithm.SHA1withDSA.getValue());
		map.put("ENONE", SignAlgorithm.NONEwithECDSA.getValue());
		map.put("ESHA1", SignAlgorithm.SHA1withECDSA.getValue());
	}

	/**
	 * 获取算法，用户传入算法ID返回算法名，传入算法名返回本身
	 * @param idOrAlgorithm 算法ID或算法名
	 * @return 算法名
	 */
	public static String getAlgorithm(final String idOrAlgorithm){
		return ObjUtil.defaultIfNull(getAlgorithmById(idOrAlgorithm), idOrAlgorithm);
	}

	/**
	 * 获取算法ID，用户传入算法名返回ID，传入算法ID返回本身
	 * @param idOrAlgorithm 算法ID或算法名
	 * @return 算法ID
	 */
	public static String getId(final String idOrAlgorithm){
		return ObjUtil.defaultIfNull(getIdByAlgorithm(idOrAlgorithm), idOrAlgorithm);
	}

	/**
	 * 根据JWT算法ID获取算法
	 *
	 * @param id JWT算法ID
	 * @return 算法
	 */
	private static String getAlgorithmById(final String id) {
		return map.get(id.toUpperCase());
	}

	/**
	 * 根据算法获取JWT算法ID
	 *
	 * @param algorithm 算法
	 * @return JWT算法ID
	 */
	private static String getIdByAlgorithm(final String algorithm) {
		return map.getKey(algorithm);
	}
}
