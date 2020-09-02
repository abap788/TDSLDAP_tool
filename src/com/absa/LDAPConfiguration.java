package com.absa;


import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPv2;

public class LDAPConfiguration {

	// *******LDAP CONNECTION DETAILS******

	// set LDAP security authentication
	// private static int PORT=389;
	// set the LDAP client Version
	private static int LDAP_VERSION = 3;
	// This is our access ID
	// private static String LDAP_USERNAME="cn=root";
	// This is our access PW
	// private static String LDAP_PW="LDAPPassword1";
	// LDAP host name
	// private static String HOST="ZADRNBRAPP0035.corp.dsarena.com";

	String keystorePath = "C:\\Users\\ABAP788\\amit_bk\\Eclipseworkspace\\LDAP\\ACLProject_v2\\certificates\\tdsldapprod.corp.dsarena.com.kdb";
	String keystoreType = "CMS";
	String keystorePassword = "LDAP@Password1";
	// TLSv1 / TLSv1.2 / SSLv3 / SSLv2 / SSL
	String sslContext = "TLSv1.2";

	public LDAPConnection getAdminLdapConnection(String host, String user, String pass, int port) {
		LDAPConnection ldapConnection = new LDAPConnection();
		LDAPSSLSocketFactory sslConnection = null;

		java.security.Security.addProvider(new com.ibm.crypto.provider.IBMJCE());
		java.security.Security.addProvider(new com.ibm.security.cmskeystore.CMSProvider());

		try {

			sslConnection = new LDAPSSLSocketFactory(keystorePath, keystoreType, keystorePassword, false, sslContext);

			Boolean ssl = false;

			if (port == 389)
			{
//				connection.connect(LDAP_VERSION, host, port, user, pass);

				ldapConnection = new LDAPConnection();
				ldapConnection.setOption(LDAPv2.PROTOCOL_VERSION, new Integer(3));
				ldapConnection.connect(host, 389);
				ldapConnection.bind(user, pass);
				System.out.println(host+" : non-SSL Connection successful..!!");
			}
			else {
				ldapConnection = new LDAPConnection(sslConnection);
				ldapConnection.setOption(LDAPv2.PROTOCOL_VERSION, new Integer(3));
				ldapConnection.connect(host, 636);
				ldapConnection.bind(user, pass);
				System.out.println(host +" : new SSL Connection successful..!!");
			}

		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
		}
		return ldapConnection;
	}

	public void closeLdapConnection(LDAPConnection connection) {
		try {
			if (connection != null) {
				connection.disconnect();
				;
				System.out.println();
				System.out.println("CONNECTION CLOSED");
			}
		} catch (Throwable ignored) {
		}
	}

}
