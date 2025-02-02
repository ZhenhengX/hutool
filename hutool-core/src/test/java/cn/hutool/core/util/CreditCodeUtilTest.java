package cn.hutool.core.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CreditCodeUtilTest {

	@Test
	public void isCreditCodeBySimple() {
		String testCreditCode = "91310115591693856A";
		assertTrue(CreditCodeUtil.isCreditCodeSimple(testCreditCode));
	}

	@Test
	public void isCreditCode() {
		String testCreditCode = "91310110666007217T";
		assertTrue(CreditCodeUtil.isCreditCode(testCreditCode));
	}

	@Test
	public void isCreditCode2() {
		// 由于早期部分试点地区推行 法人和其他组织统一社会信用代码 较早，会存在部分代码不符合国家标准的情况。
		// 见：https://github.com/bluesky335/IDCheck
		String testCreditCode = "91350211M00013FA1N";
		assertFalse(CreditCodeUtil.isCreditCode(testCreditCode));
	}

	@Test
	public void randomCreditCode() {
		final String s = CreditCodeUtil.randomCreditCode();
		assertTrue(CreditCodeUtil.isCreditCode(s));
	}
}
