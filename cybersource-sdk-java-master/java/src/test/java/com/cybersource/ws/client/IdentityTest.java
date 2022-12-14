package com.cybersource.ws.client;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;

public class IdentityTest {

	@Test
	public void testSetUpMerchant() throws SignException, ConfigException{
		File p12file = Mockito.mock(File.class);
		MerchantConfig mc = Mockito.mock(MerchantConfig.class);

		String keyAlias = "CN="+mc.getMerchantID()+",SERIALNUMBER=400000009910179089277";
		X509Certificate x509Cert = Mockito.mock(X509Certificate.class);
		Principal principal =  Mockito.mock(Principal.class);
		PrivateKey pkey = Mockito.mock(PrivateKey.class);
		Logger logger = Mockito.mock(Logger.class);
		Mockito.when(x509Cert.getSubjectDN()).thenReturn(principal);
		Mockito.when(principal.getName()).thenReturn(keyAlias);

		Mockito.when(mc.getKeyFile()).thenReturn(p12file);
		Mockito.when(mc.getKeyPassword()).thenReturn("testPwd");
		Identity identity = new Identity(mc,x509Cert,pkey,logger);
		assertEquals(identity.getName(), mc.getMerchantID());
		assertEquals(identity.getSerialNumber(), "400000009910179089277");
		assertEquals(String.valueOf(identity.getPswd()), "testPwd");
		assertNotNull(identity.getPrivateKey());
	}

	@Test
	public void testSetUpServerForP12Certs() throws SignException, IOException, ConfigException {
		Properties merchantProps = getConfigProps();
		merchantProps.setProperty("enableCacert", "false");
		MerchantConfig customConfig = new MerchantConfig(merchantProps, merchantProps.getProperty("merchantID"));
		String keyAlias = "CN=CyberSource_SJC_US,SERIALNUMBER=400000009910179089277";
		X509Certificate x509Cert = Mockito.mock(X509Certificate.class);
		Principal principal =  Mockito.mock(Principal.class);
		Mockito.when(x509Cert.getSubjectDN()).thenReturn(principal);
		Mockito.when(principal.getName()).thenReturn(keyAlias);
		Identity identity = new Identity(customConfig, x509Cert);
		assertEquals(identity.getName(), Utility.SERVER_ALIAS);
		assertEquals(identity.getSerialNumber(), "400000009910179089277");
		assertNull(identity.getPrivateKey());
	}

	@Test
	public void testSetUpServerForCaCerts() throws SignException, IOException, ConfigException {
		Properties merchantProps = getConfigProps();
		merchantProps.setProperty("enableCacert", "true");
		MerchantConfig customConfig = new MerchantConfig(merchantProps, merchantProps.getProperty("merchantID"));
		String keyAlias = "SERIALNUMBER=400000009910179089277,CN=CyberSource_SJC_US";
		X509Certificate x509Cert = Mockito.mock(X509Certificate.class);
		Principal principal =  Mockito.mock(Principal.class);
		Mockito.when(x509Cert.getSubjectDN()).thenReturn(principal);
		Mockito.when(principal.getName()).thenReturn(keyAlias);
		Identity identity = new Identity(customConfig, x509Cert);
		assertEquals(identity.getName(), Utility.SERVER_ALIAS);
		assertEquals(identity.getSerialNumber(), "400000009910179089277");
		assertNull(identity.getPrivateKey());
	}

	private Properties getConfigProps() throws IOException {
		Properties merchantProperties = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test_cybs.properties");
		if (in == null) {
			throw new RuntimeException("Unable to load test_cybs.properties file");
		}
		merchantProperties.load(in);
		return merchantProperties;
	}

}