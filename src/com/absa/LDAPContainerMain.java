package com.absa;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.SystemException;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

public class LDAPContainerMain {

	static LDAPConnection connection_from = null, connection_to = null; // LDAP connection object (netscape ldap)

	public static void main(String[] args) throws SystemException, LDAPException {
		

		
		// TODO Auto-generated method stub

		LdapSearch ls = new LdapSearch();
		LDAPOperations lo = new LDAPOperations();

		String app_name = "messagebroker_dev";

		String app_dn = "ou=" + app_name + ",ou=intranet,ou=sites,o=absa";
		String sys_user = "uid=system,ou=users," + app_dn;

		List<String> app_acl = new ArrayList<String>();

		app_acl.add(
				"group:CN=ADMINS,O=ABSA:object:ad:normal:rwsc:sensitive:rwsc:critical:rwsc:system:rsc:restricted:rwsc");
		app_acl.add(
				"access-id:UID=WASBIND,OU=USERS,OU=WAS,OU=APPLICATIONS,O=ABSA:object:ad:normal:rwsc:sensitive:rwsc:critical:rwsc:system:rsc:restricted:rwsc");
		app_acl.add("access-id:" + sys_user
				+ ":object:ad:normal:rwsc:sensitive:rwsc:critical:rwsc:system:rsc:restricted:rwsc");

		/// SEARCH LDAP USERS OBJECT
		LdapSearch searchACL = new LdapSearch();

		LDAPConfiguration ldapconfig_from = new LDAPConfiguration();// LDAPConnection object
		LDAPConfiguration ldapconfig_to = new LDAPConfiguration();// LDAPConnection object
//		LDAPConfiguration lo = new LDAPConfiguration();// LDAPConnection object

//		LDAPConnection lc_from = ldapconfig_from.getAdminLdapConnection("zaprnbrapp1373.corp.dsarena.com", "cn=root",
//				"LDAPPassword1", 389);


		
		// Copy ACL
//		LDAPConnection lc_from = ldapconfig_from.getAdminLdapConnection("ZAPSDCRAPP1609.corp.dsarena.com", "cn=root",
//				"LDAPPassword1", 389);
//		app_dn = "ou=applications,O=ABSA";
//		List<String> app_acl_from = ls.getACL(lc_from, app_dn);
//		System.out.println("LDAP ACL from: "+app_acl_from.toString());


		// 1 Get LDAP connection
		// ZAPSDCRAPP1608 zaprnbrapp1373 ZADSDCRAPP0022.corp.dsarena.com
		// ZADRNBRAPP0047 zadsdcrapp0031 zadsdcrapp0101 zadrnbrapp0124 tdsldapuat
		// ZADSDCRAPP0022 / ZAPSDCRAPP1608
		LDAPConnection lc_to = ldapconfig_to.getAdminLdapConnection("ZADSDCRAPP0022.corp.dsarena.com", "cn=root",
		"LDAPPassword1", 389);
//		
//		List<String> aclList = ls.getACL(lc_to, "ou=applications,O=ABSA");



		// 2 LDAP contanier creation
		
		 System.out.println("LDAP container creation : " +
		 lo.createContainer(app_name, app_dn, lc_to));
		 lo.creategroup("ou=groups,"+app_dn, "messagebroker_admin");
		 lo.creategroup("ou=groups,"+app_dn, "messagebroker_users");

		// 3
		// ADD container ACL
//		app_dn = "ou=applications,O=ABSA";
		ls.addAppACL(lc_to, app_dn, app_acl);
//		ls.addAppACL(lc_to, app_dn, app_acl_from);

		

		
		// 5
		// GET ACL
		//app_dn = "ou=applications,O=ABSA";
		//app_dn = "ou=staff,O=ABSA";
//		app_dn = "uid=svc-sailpoint-ldap,ou=users,ou=staff,O=ABSA";
//
//		List<String> aclList = ls.getACL(lc_to, app_dn);	
//		int count = 1;
//		for (String acl : aclList) {
//			System.out.println(count +") "+ acl);
//			count++;
//		}
//		System.out.println("LDAP ACL before : "+aclList.toString());
		
		//4 Add staff ACL
		String staff_acl = "access-id:"+sys_user+":normal:rsc:at.AbsaWpsRole:rwsc:at.AbsaTransactAccessLevel:rw:at.absaapppwdexp:rwsc:at.ibm-nativeId:rwsc:at.AbsaBranchCode:rw:at.absaintdetect:rwsc";
		ls.addExtraACL(lc_to, "ou=staff,O=ABSA", staff_acl);
//		
//		
//		aclList = ls.getACL(lc_to, "ou=staff,O=ABSA");		
//		System.out.println("LDAP ACL after : "+aclList.toString());
		
		// ############ get sub tree ##############
//		
//		LDAPConnection lc_from = ldapconfig_from.getAdminLdapConnection("ZAPSDCRAPP1608.corp.dsarena.com", "cn=root",
//		"LDAPPassword1", 389);
//		List<String> subTree_from = ls.getSubTree(lc_from, "ou=internet,ou=sites,o=absa");
		
// ########### get all ACLS ###########
		
		ArrayList<String> ldaphosts = new ArrayList<String>();
//		ldaphosts.add("ZAPSDCRAPP1608.corp.dsarena.com");
//		ldaphosts.add("ZAPSDCRAPP1609.corp.dsarena.com");
//		ldaphosts.add("zaprnbrapp1373.corp.dsarena.com");
//		ldaphosts.add("zaprnbrapp1374.corp.dsarena.com");
		
//		ldaphosts.add("22.147.152.125"); 22.240.48.23
//
//		ldaphosts.add("tdsldapprod.corp.dsarena.com"); // 18.2.5
//		ldaphosts.add("tdsldapuat.corp.dsarena.com"); // 18.2.8
//		ldaphosts.add("tdsldapdev.corp.dsarena.com"); // 18.2.8
		
//		ldaphosts.add("22.147.152.125"); // 270 uat
//		ldaphosts.add("22.240.48.23"); // sdc uat
		
//		ldaphosts.add("zadsdcrapp0101.corp.dsarena.com");

//		ldaphosts.add("22.240.48.22");
		
		ArrayList<String> ldapUsers = new ArrayList<String>();
//		ldapUsers.add("absn403");
//		ldapUsers.add("100233820");
//		ldapUsers.add("100598197");
//		ldapUsers.add("00276958");
//		ldapUsers.add("100551943");
//		ldapUsers.add("00183931");
		
//		ldapUsers.add("00250667");
//		ldapUsers.add("00232093");

//		ldapUsers.add("100547068");
//		ldapUsers.add("100358388");
//		ldapUsers.add("100360602");
//		ldapUsers.add("00280674");



		
//		for (String ldapUser : ldapUsers) {
//			
//			for (String ldaphost : ldaphosts) {
//				LDAPConnection lc_loop = ldapconfig_to.getAdminLdapConnection(ldaphost, "cn=root",
//						"LDAPPassword1", 389);
//				System.out.println("For server : "+ldaphost + " " + ls.checkUser(lc_loop, ldapUser));
//				System.out.println(ls.checkUser(lc_loop, ldapUser));
//
//				List<String> aclList = ls.getACL(lc_loop, "ou=datapowerACL,ou=applications,O=ABSA");
//				
//				
//
//				for (int i = 0; i < 1; i++) {
//					
//					LDAPConnection lc_loop = ldapconfig_to.getAdminLdapConnection(ldaphost, "uid=svctds,ou=users,ou=staff,o=absa",
//							"LDAPPassword1", 636);
//					
//					
//					System.out.println(String.valueOf(i+1)+"#");
//					System.out.println(ls.getMember(lc_loop, "cn=DataPowerAdmins,ou=groups,ou=datapowerAdmin,ou=applications,o=absa",ldapUser));
//					ls.getMembers(lc_loop, "cn=DataPowerAdmins,ou=groups,ou=datapowerAdmin,ou=applications,o=absa");
//					System.out.println(ls.checkUser(lc_loop, ldapUser));
//				}
//			}
//			
//		}
		

		
		
// ######## COPY ACL #############
		
		ArrayList<String> app_dns = new ArrayList<String>();
//		app_dns.add("ou=apigateway,ou=applications,O=ABSA");
//		app_dns.add("ou=authadm,ou=applications,O=ABSA");
//		app_dns.add("ou=bamboo,ou=applications,O=ABSA");
//		app_dns.add("ou=cloudburst,ou=applications,O=ABSA");
//		app_dns.add("ou=confluence,ou=applications,O=ABSA");
//		app_dns.add("ou=contentmanager,ou=applications,O=ABSA");
//		app_dns.add("ou=datapowerACL,ou=applications,O=ABSA");
//		app_dns.add("ou=datapowerAdmin,ou=applications,O=ABSA");
//		app_dns.add("ou=filenet,ou=applications,O=ABSA");
//		app_dns.add("ou=health_check,ou=applications,O=ABSA");
//		app_dns.add("ou=ip,ou=applications,O=ABSA");
//		app_dns.add("ou=jira,ou=applications,O=ABSA");
//		app_dns.add("ou=ldapsync,ou=applications,O=ABSA");
//		app_dns.add("ou=megarawas,ou=applications,O=ABSA");
//		app_dns.add("ou=oneCertFeeds,ou=applications,O=ABSA");
//		app_dns.add("ou=processserver,ou=applications,O=ABSA");
//		app_dns.add("ou=securityservice,ou=applications,O=ABSA");
//		app_dns.add("ou=ServiceAccounts,ou=applications,O=ABSA");
//		app_dns.add("ou=stash,ou=applications,O=ABSA");
//		app_dns.add("ou=test,ou=applications,O=ABSA");
//		app_dns.add("ou=vignette,ou=applications,O=ABSA");
//		app_dns.add("ou=VIP_F5,ou=applications,O=ABSA");
//		app_dns.add("ou=was,ou=applications,O=ABSA");
//		app_dns.add("ou=wps,ou=applications,O=ABSA");
		
//		app_dns.add("ou=sites,o=absa");
//		app_dns.add("ou=retailerportal,ou=extranet,ou=sites,o=absa");
//


//		List<String> subTree_from = ls.getSubTree(lc_from, "ou=sites,o=absa");
//
//		for (String app_dn_loop : subTree_from) {
//			List<String> app_acl_from = ls.getACL(lc_from, app_dn_loop);
//			ls.addAppACL(lc_to, app_dn_loop, app_acl_from);
//		}

	}
	


}
