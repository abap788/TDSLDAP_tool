package com.absa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPv3;

public class LdapSearch {

	static Attributes attrs;
	// private static String LDAP_BASE_DN="ou=intranet,ou=sites,O=ABSA";
	private static String[] attrIDs = { "aclentry" };
	private static String ACL_ADMIN = "group:CN=ADMINS,O=ABSA:object:ad:normal:rwsc:sensitive:rwsc:critical:rwsc:system:rsc:restricted:rwsc";

	private static String ACL_AUTHENTICATED = "group:CN=AUTHENTICATED:object:deny:ad:normal:rsc:normal:deny:w:sensitive:rsc:sensitive:deny:w:critical:deny:rwsc";
	// SEARCH FILERS
	// String filter="(ou=*)";
	String filter = "(objectClass=*)";
	LDAPSearchResults myResults = null, myResults_to = null;
	ArrayList<String> ACl_values = new ArrayList<String>();
	String DN;
	int count = 0;

	// ou=sites,O=ABSA
	public void searchUserinLDAP_toModify(LDAPConnection connection_to, String LDAP_BASE_DN) {

		try {
			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_BASE, filter, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
				System.out.println("(Modifying)Reading ACL for DN : " + DN);
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration attrsInSet = entryAttrs.getAttributes();

				// System.out.println(attrsInSet.getClass());
				while (attrsInSet.hasMoreElements()) {
					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
					String attrName = nextAttribute.getName();
					System.out.println("\t" + attrName + ":");
					Enumeration valsInAttr = nextAttribute.getStringValues();
					ACl_values.clear();
					while (valsInAttr.hasMoreElements()) {
						String nextValue = (String) valsInAttr.nextElement();
						System.out.println("\t\t" + nextValue);
						ACl_values.add(nextValue);
						System.out.println();

					}
					count++;
					modifyACL(connection_to, DN, ACl_values);

				}

			}
			System.out.println("*******total acl entry*****" + count);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());

		}

	}

	public void searchUserinLDAP_toCopy(LDAPConnection connection_from, LDAPConnection connection_to,
			String LDAP_BASE_DN) {

		try {
			myResults = connection_from.search(LDAP_BASE_DN, LDAPv3.SCOPE_BASE, filter, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
				System.out.println("(Coping)Reading ACL for DN :" + DN);
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration attrsInSet = entryAttrs.getAttributes();

				// System.out.println(attrsInSet.getClass());
				while (attrsInSet.hasMoreElements()) {
					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
					String attrName = nextAttribute.getName();
					System.out.println("\t" + attrName + ":");
					Enumeration valsInAttr = nextAttribute.getStringValues();
					ACl_values.clear();
					while (valsInAttr.hasMoreElements()) {
						String nextValue = (String) valsInAttr.nextElement();
						System.out.println("\t\t" + nextValue);
						ACl_values.add(nextValue);
						System.out.println();

					}
					count++;

					copyACL(connection_from, connection_to, LDAP_BASE_DN, ACl_values);

				}

			}
			System.out.println("*******total acl entry*****" + count);
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());

		}

	}

	void modifyACL(LDAPConnection connection_to, String DN, List<String> ACLlist) {

		boolean flag = false;
		Iterator<String> iterator = ACLlist.iterator();
		while (iterator.hasNext()) {
			String ACL_value = iterator.next();
			if (ACL_value.equalsIgnoreCase(ACL_ADMIN)) {
				flag = true;
			}
		}
		if (flag == false) {
			try {

				System.out.println("Need to add ACL for below DN");
				System.out.println("**" + DN + "**");

				int add = LDAPModification.ADD;
				// int delete=LDAPModification.DELETE;
				// int remove=LDAPModification.DELETE;

				LDAPAttribute attrAcl = new LDAPAttribute("aclentry", ACL_ADMIN);
				// LDAPAttribute attrDelAcl= new LDAPAttribute("aclentry");

				LDAPModification ACLChange = new LDAPModification(add, attrAcl);
				// LDAPModification ACLdelete=new LDAPModification(delete, attrDelAcl);

				connection_to.modify(DN, ACLChange);
				// connection.modify(DN, ACLdelete,ACL_ADMIN);
			} catch (LDAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("No need to add ACL for : " + DN);
		}

	}

	void addACL(LDAPConnection connection_to, String DN, List<String> ACLlist) {

		boolean flag = false;
		Iterator<String> iterator = ACLlist.iterator();
		while (iterator.hasNext()) {
			String ACL_value = iterator.next();
			if (ACL_value.equalsIgnoreCase(ACL_ADMIN)) {
				flag = true;
			}
		}
		if (flag == false) {
			try {

				System.out.println("Need to add ACL for below DN");
				System.out.println("**" + DN + "**");

				int add = LDAPModification.ADD;
				// int delete=LDAPModification.DELETE;
				// int remove=LDAPModification.DELETE;

				LDAPAttribute attrAcl = new LDAPAttribute("aclentry", ACL_ADMIN);
				// LDAPAttribute attrDelAcl= new LDAPAttribute("aclentry");

				LDAPModification ACLChange = new LDAPModification(add, attrAcl);
				// LDAPModification ACLdelete=new LDAPModification(delete, attrDelAcl);

				connection_to.modify(DN, ACLChange);
				// connection.modify(DN, ACLdelete,ACL_ADMIN);
			} catch (LDAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("No need to add ACL for : " + DN);
		}

	}

	void copyACL(LDAPConnection connection_from, LDAPConnection connection_to, String DN, List<String> ACLlist) {

		try {

			System.out.println("Adding ACLs for DN : " + DN);

			Iterator<String> iterator = ACLlist.iterator();
			while (iterator.hasNext()) {
				String ACL_value = iterator.next();

				int add = LDAPModification.ADD;
				LDAPAttribute attrAcl = new LDAPAttribute("aclentry", ACL_value);
				LDAPModification ACLChange = new LDAPModification(add, attrAcl);
				connection_to.modify(DN, ACLChange);

				System.out.println("ACL added : " + ACL_value);

			}
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getACL(LDAPConnection connection_to, String LDAP_BASE_DN) {

		try {
			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_BASE, filter, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
				System.out.println("(Modifying)Reading ACL for DN : " + DN);
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration attrsInSet = entryAttrs.getAttributes();

				// System.out.println(attrsInSet.getClass());
				while (attrsInSet.hasMoreElements()) {
					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
					String attrName = nextAttribute.getName();
//					System.out.println("\t" + attrName + ":");
					Enumeration valsInAttr = nextAttribute.getStringValues();
					ACl_values.clear();
					while (valsInAttr.hasMoreElements()) {
						String nextValue = (String) valsInAttr.nextElement();
//						System.out.println("\t\t" + nextValue);
						ACl_values.add(nextValue);
//						System.out.println();

					}
					count++;
					// modifyACL(connection_to,DN,ACl_values);

				}

			}
			System.out.println("*******total acl entry*****" + ACl_values.size());
			//System.out.println("ACL list : " + ACl_values.toString());
			return ACl_values;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
			return null;
		}

	}
	
	public List<String> getMembers(LDAPConnection connection_to, String LDAP_BASE_DN) {

		try {
			//String member = "abcsa2n";
			String filter_1 = "(objectClass=*)";
			String[] attrIDs = { "member" };

			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_BASE, filter_1, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
//				System.out.println("(Modifying)Reading ACL for DN : " + DN);
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration attrsInSet = entryAttrs.getAttributes();

				// System.out.println(attrsInSet.getClass());
				while (attrsInSet.hasMoreElements()) {
					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
					String attrName = nextAttribute.getName();
//					System.out.println("\t" + attrName + ":");
					Enumeration valsInAttr = nextAttribute.getStringValues();
					ACl_values.clear();
					while (valsInAttr.hasMoreElements()) {
						String nextValue = (String) valsInAttr.nextElement();
//						System.out.println("\t\t" + nextValue);
						ACl_values.add(nextValue);
//						System.out.println();

					}
					count++;
					// modifyACL(connection_to,DN,ACl_values);

				}

			}
			Collections.sort(ACl_values);
			System.out.println("*******total members entry*****" + ACl_values.size());
			
			int cn = 1;
			for (String ACl_value : ACl_values) {
				System.out.println(String.valueOf(cn++)+") "+ACl_value);
			}
			
			return ACl_values;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
			return null;
		}

	}
	
	public String getMember(LDAPConnection connection_to, String LDAP_BASE_DN, String member) {

		try {
			//String member = "abcsa2n";
			String filter_1 = "(objectClass=groupOfNames)";
			String[] attrIDs = { "member" };

			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_BASE, filter_1, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
//				System.out.println("(Modifying)Reading ACL for DN : " + DN);
				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
				Enumeration attrsInSet = entryAttrs.getAttributes();

				// System.out.println(attrsInSet.getClass());
				while (attrsInSet.hasMoreElements()) {
					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
					String attrName = nextAttribute.getName();
//					System.out.println("\t" + attrName + ":");
					Enumeration valsInAttr = nextAttribute.getStringValues();
					ACl_values.clear();
					while (valsInAttr.hasMoreElements()) {
						String nextValue = (String) valsInAttr.nextElement();
//						System.out.println("\t\t" + nextValue);
						if(nextValue.substring(0, nextValue.indexOf(",")).equalsIgnoreCase("uid="+member))
							return member+" user found on : "+LDAP_BASE_DN;
						
							
//						ACl_values.add(nextValue);
//						System.out.println();

					}
					count++;
					// modifyACL(connection_to,DN,ACl_values);

				}

			}

			
			return "User NOT found on : "+LDAP_BASE_DN;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
			return null;
		}

	}
	
	
	public String checkUser(LDAPConnection connection_to, String ab_id) {

		try {
			String LDAP_BASE_DN = "o=absa";
			filter = "(&(uid="+ab_id+")(objectclass=top))";
			//filter = "(&(member='uid=abcsa2n,ou=users,ou=staff,o=absa')(objectclass=GroupOfNames))";

			//member='uid=abcsa2n,ou=users,ou=staff,o=absa'
			String[] attrIDs_uid = { "uid" };
			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_SUB, filter, attrIDs_uid, false);
			
			if(!myResults.hasMoreElements())
				return ab_id+" User NOT found on : "+LDAP_BASE_DN;
			
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
//				System.out.print(DN + " #### ");
				if((DN==null) || (DN.equalsIgnoreCase("")))
					return ab_id+ " User NOT found on : "+LDAP_BASE_DN;
				else if (DN.equalsIgnoreCase("uid="+ab_id+",ou=users,ou=staff,o=absa"))
						return ab_id+" user found on : "+LDAP_BASE_DN;
				else
					return ab_id+" user found on : "+DN;
//				LDAPAttributeSet entryAttrs = myEntry.getAttributeSet();
//				Enumeration attrsInSet = entryAttrs.getAttributes();
//
//				// System.out.println(attrsInSet.getClass());
//				while (attrsInSet.hasMoreElements()) {
//					LDAPAttribute nextAttribute = (LDAPAttribute) attrsInSet.nextElement();
//					String attrName = nextAttribute.getName();
////					System.out.println("\t" + attrName + ":");
//					Enumeration valsInAttr = nextAttribute.getStringValues();
//					ACl_values.clear();
//					while (valsInAttr.hasMoreElements()) {
//						String nextValue = (String) valsInAttr.nextElement();
////						System.out.println("\t\t" + nextValue);
//						ACl_values.add(nextValue);
////						System.out.println();
//
//					}
//					count++;
//					// modifyACL(connection_to,DN,ACl_values);
//
//				}

			}
//			System.out.println("*******total acl entry*****" + ACl_values.size());
//			System.out.println("User list : " + ACl_values.toString());
//			return ACl_values;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
			return ab_id+" user found";
		}
		return ab_id+" user found";

	}
	
	public List<String> getSubTree(LDAPConnection connection_to, String LDAP_BASE_DN) {

		try {
			ArrayList<String> subTrees = new ArrayList<String>();
			// (&(ou=*,ou=internet,ou=sites,o=absa)(objectclass=top))
			filter = "(&(ou=*)(objectclass=top))";
			
			myResults = connection_to.search(LDAP_BASE_DN, LDAPv3.SCOPE_ONE, filter, attrIDs, false);
			while (myResults.hasMoreElements()) {
				LDAPEntry myEntry = myResults.next();
				DN = myEntry.getDN();
//				System.out.println("(Modifying)Reading ACL for DN : " + DN);
				subTrees.add(DN);
}
			System.out.println("*******total acl entry*****" + subTrees.size());
			System.out.println("subTrees list : " + subTrees.toString());
			return subTrees;
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLDAPResultCode());
			return null;
		}

	}

	void addAppACL(LDAPConnection connection_to, String DN, List<String> ACLlist) {

		try {

			System.out.println("Adding ACLs for DN : " + DN);

			Iterator<String> iterator = ACLlist.iterator();
			while (iterator.hasNext()) {
				String ACL_value = iterator.next();

				int add = LDAPModification.ADD;
				LDAPAttribute attrAcl = new LDAPAttribute("aclentry", ACL_value);
				LDAPModification ACLChange = new LDAPModification(add, attrAcl);
				connection_to.modify(DN, ACLChange);

				System.out.println("ACL added : " + ACL_value);

			}
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void addExtraACL(LDAPConnection connection_to, String DN, String ACL) {

		try {
			int add = LDAPModification.ADD;
			// int delete=LDAPModification.DELETE;
			// int remove=LDAPModification.DELETE;

			LDAPAttribute attrAcl = new LDAPAttribute("aclentry", ACL);
			// LDAPAttribute attrDelAcl= new LDAPAttribute("aclentry");

			LDAPModification ACLChange = new LDAPModification(add, attrAcl);
			// LDAPModification ACLdelete=new LDAPModification(delete, attrDelAcl);

			connection_to.modify(DN, ACLChange);
			
		} catch (LDAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
