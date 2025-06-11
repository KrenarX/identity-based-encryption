package app.model.key;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;

public class MyEncryption {
	
	private IESEngine iese = new IESEngine(new ECDHBasicAgreement(), new KDF2BytesGenerator(new SHA256Digest()), new HMac(new SHA256Digest()));
    
	public byte[] cifra(String plaintext, ECPublicKeyParameters chiavepub, ECPrivateKeyParameters chiavepriv)
	{
		byte[] tmpPlaintext = plaintext.getBytes();
        ECKeyParameters chiavepubECKey = (ECKeyParameters) chiavepub;
        iese.init(true, chiavepriv, chiavepubECKey, new IESWithCipherParameters(null, null, 256, 256));
        
        byte[] datiCifrati = null;
        
		try {
			datiCifrati = iese.processBlock(tmpPlaintext, 0, tmpPlaintext.length);
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}

        return datiCifrati;
	}
	
	public byte[] decifra(byte[] chipertext, ECPublicKeyParameters chiavepub, ECPrivateKeyParameters chiavepriv)
	{
		 ECKeyParameters chiavepubECKey = (ECKeyParameters) chiavepub;
		 
		 iese.init(false, chiavepriv, chiavepubECKey, new IESWithCipherParameters(null, null, 0, 0));
		 
	        byte[] datiDecifrati = null;
			try {
				datiDecifrati = iese.processBlock(chipertext, 0, chipertext.length);
			} catch (InvalidCipherTextException e) {
				e.printStackTrace();
			}

	        return datiDecifrati;
	}
	
}
