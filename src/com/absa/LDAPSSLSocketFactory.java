package com.absa;



//~--- non-JDK imports --------------------------------------------------------

import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.InputStream;

import java.net.Socket;
import java.net.URL;

import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * The <code>LDAPSSLSocketFactory</code> class implements the custom SSL socket
 * factory used to create secure (SSL) connections to an LDAP server.
 *
 * @author Marcus Portmann
 */
public class LDAPSSLSocketFactory
  implements LDAPSocketFactory
{
  private javax.net.ssl.SSLSocketFactory socketFactory;

  /**
   * Constructs a new <code>LDAPSSLSocketFactory</code>
   *
   * @param keyStorePath               the path to the keystore that will be used to secure the
   *                                   ClientSSL connection to an LDAP server
   * @param keyStoreType               the keystore type e.g. pkcs12
   * @param keyStorePassword           the keystore password
   * @param disableServerTrustChecking disable server trust checking
   */
  public LDAPSSLSocketFactory(String keyStorePath, String keyStoreType, String keyStorePassword,
      boolean disableServerTrustChecking, String strSSLContext)
  {
    InputStream keyStoreInputStream = null;

    try
    {
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);

      File keyStoreFile = new File(keyStorePath);

      if (!keyStoreFile.exists())
      {
        throw new RuntimeException("The keystore (" + keyStorePath + ") could not be found");
      }

      URL keyStoreUrl = keyStoreFile.toURI().toURL();

      keyStoreInputStream = keyStoreUrl.openStream();
      keyStore.load(keyStoreInputStream,
          ((keyStorePassword == null) || (keyStorePassword.length() == 0))
          ? new char[0]
          : keyStorePassword.toCharArray());

      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
      {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
          throws CertificateException
        {
          // Skip client verification step
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
          throws CertificateException
        {
          // Skip server verification step
        }

        public X509Certificate[] getAcceptedIssuers()
        {
          return new X509Certificate[0];
        }
      } };

      // Setup the key manager for the client SSL socket factory
      KeyManagerFactory keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

      keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

      // Setup the trust manager for the client SSL socket factory
      TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      trustManagerFactory.init(keyStore);

      // Setup the SSL context
      SSLContext sslContext = SSLContext.getInstance(strSSLContext);

      if (disableServerTrustChecking)
      {
        sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, new SecureRandom());
      }
      else
      {
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
            new SecureRandom());
      }

      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
      {
        public boolean verify(String arg0, SSLSession arg1)
        {
          // TODO Auto-generated method stub
          return true;
        }
      });

      /*
       * Retrieve the socket factory from the SSL context that will be used to create the ClientSSL
       * connections.
       */
      socketFactory = sslContext.getSocketFactory();
    }
    catch (Throwable e)
    {
      throw new RuntimeException("Failed to initialise the LDAP SSL socket factory", e);
    }
    finally
    {
      try
      {
        if (keyStoreInputStream != null)
        {
          keyStoreInputStream.close();
        }
      }
      catch (Throwable e) {}
    }
  }

  /**
   * Returns <code>true</code> if we are running under the IBM JDK or <code>false</code> otherwise.
   *
   * @return <code>true</code> if we are running under the IBM JDK or <code>false</code> otherwise
   */
  public static boolean isIBMJDK()
  {
    for (Provider provider : Security.getProviders())
    {
      if (provider.getName().startsWith("IBMJSSE"))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Creates a socket and connects it to the specified remote host at the specified remote port.
   *
   * @param host the server host
   * @param port the server port
   *
   * @return a socket connected to the specified host and port
   *
   * @throws LDAPException
   */
  public Socket makeSocket(String host, int port)
    throws LDAPException
  {
    try
    {
      return socketFactory.createSocket(host, port);
    }
    catch (Throwable e)
    {
      throw new LDAPException("Failed to create an SSL connection to the LDAP server (" + host
          + ":" + port + "): " + e.getMessage());
    }
  }
}
