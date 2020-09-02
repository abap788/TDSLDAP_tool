package com.absa;

import org.omg.CORBA.SystemException;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv2;

public class LDAPOperations {
	
	LDAPConnection con = new LDAPConnection();
	String USER_OBJECT_CLASS = "top";// inetOrgPerson
	String myBaseDN = "O=ABSA";
	LDAPSearchResults res = null;
	String UserAttribute = "ou";
	String[] myAttrs = {};
	
	public Boolean createContainer(String app_name , String app_dn , LDAPConnection ldapConn) throws LDAPException, SystemException {
		// TODO Auto-generated method stub
		
		this.con = ldapConn;

		String[] oulist = { app_name };

		res = search(oulist);
		
		if (res.getCount() == 0) {

			LDAPAttributeSet attrs = new LDAPAttributeSet();
			String objclass[] = { "top", "organizationalUnit" };

			attrs.add(new LDAPAttribute("objectclass", objclass));
			attrs.add(new LDAPAttribute("ou", app_name));

			String dn = app_dn;
			LDAPEntry theEntry = new LDAPEntry(dn, attrs);
			//// System.out.println(con);

			// The add may fail for lack of privileges or other reasons
			con.add(theEntry);
			String Groupdn = creategroup(dn, "groups");
			String userdn = creategroup(dn, "users");
			createSystemUser(userdn);

			return true;

		} else {
			System.out.println("appname already present !!!");
			return false;
		}
	}

	
	public String creategroup(String containerdn, String value) throws LDAPException {

		LDAPAttributeSet attrs = new LDAPAttributeSet();
		String objclass[] = { "top", "organizationalUnit" };

		attrs.add(new LDAPAttribute("objectclass", objclass));
		attrs.add(new LDAPAttribute("ou", value));

		String dn = "ou=" + value + "," + containerdn;
		LDAPEntry theEntry = new LDAPEntry(dn, attrs);
		//// System.out.println(con);

		// The add may fail for lack of privileges or other reasons
		con.add(theEntry);

		return dn;
	}

	public void createSystemUser(String userdn) throws LDAPException {
		LDAPAttributeSet attrs = new LDAPAttributeSet();
		String objclass[] = { "top", "organizationalPerson", "person", "inetOrgPerson" };

		attrs.add(new LDAPAttribute("objectclass", objclass));
		attrs.add(new LDAPAttribute("ou", "system"));
		attrs.add(new LDAPAttribute("cn", "system"));
		attrs.add(new LDAPAttribute("sn", "system"));
		attrs.add(new LDAPAttribute("uid", "system"));
		attrs.add(new LDAPAttribute("absaapppwdexp", "-1"));
		attrs.add(new LDAPAttribute("absaintdetect", "-1"));
		attrs.add(new LDAPAttribute("userPassword", "Password1"));

		String dn = "uid=system," + userdn;
		LDAPEntry theEntry = new LDAPEntry(dn, attrs);
		//// System.out.println(con);
		// The add may fail for lack of privileges or other reasons
		con.add(theEntry);

	}
	
	public LDAPSearchResults search(String[] userslist) throws LDAPException {

		for (String usr : userslist) {

			String LDAPuser = usr;
			StringBuilder filter = new StringBuilder();

			filter.delete(0, filter.length());

			filter.append("(&(objectClass=");
			filter.append(USER_OBJECT_CLASS);
			filter.append(")(");
			filter.append(UserAttribute);
			filter.append("=");
			filter.append(usr);
			filter.append("))");

			//// System.out.println(filter.toString());
			//// System.out.println(con);
			// //System.out.println("uid=abasab0,ou=users,ou=staff,O=ABSA");

			res = con.search("o=absa", LDAPv2.SCOPE_SUB, filter.toString(), myAttrs, false);

			//// System.out.println("^&^&^*^*&" + res.getCount());

			if (res.hasMoreElements() == false) {
				// map.put(usr, "unable to find the user");
				continue;
			}

		}
		return res;

	}


}
