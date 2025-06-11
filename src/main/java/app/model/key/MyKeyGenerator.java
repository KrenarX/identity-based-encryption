package app.model.key;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

public class MyKeyGenerator {

	private static byte[] masterKey = new byte[32];

	public MyKeyGenerator() {
		new Random().nextBytes(masterKey);
	}

	public ECPublicKeyParameters generatePublicKey(String userIdentity) {
		Security.addProvider(new BouncyCastleProvider());

		ECNamedCurveParameterSpec namedCurveSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECDomainParameters domainParameters = new ECDomainParameters(namedCurveSpec.getCurve(), namedCurveSpec.getG(),
				namedCurveSpec.getN(), namedCurveSpec.getH(), namedCurveSpec.getSeed());

		// Concatena l'identità e la master key
		byte[] combinedData = new byte[userIdentity.length() + masterKey.length];
		System.arraycopy(userIdentity.getBytes(StandardCharsets.UTF_8), 0, combinedData, 0, userIdentity.length());
		System.arraycopy(masterKey, 0, combinedData, userIdentity.length(), masterKey.length);

		// Utilizza un KDF per derivare il punto sulla curva (chiave pubblica) in modo
		// deterministico
		ECPoint publicKeyPoint = deriveDeterministicPublicKeyPoint(combinedData, domainParameters);

		ECPublicKeyParameters publicKey = new ECPublicKeyParameters(publicKeyPoint, domainParameters);

		return publicKey;
	}

	public ECPrivateKeyParameters generatePrivateKey(String userIdentity) {
		ECNamedCurveParameterSpec namedCurveSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECDomainParameters domainParameters = new ECDomainParameters(namedCurveSpec.getCurve(), namedCurveSpec.getG(),
				namedCurveSpec.getN(), namedCurveSpec.getH(), namedCurveSpec.getSeed());

		// Concatena l'identità e la master key per ottenere un valore deterministico
		byte[] combinedData = new byte[userIdentity.length() + masterKey.length];
		System.arraycopy(userIdentity.getBytes(StandardCharsets.UTF_8), 0, combinedData, 0, userIdentity.length());
		System.arraycopy(masterKey, 0, combinedData, userIdentity.length(), masterKey.length);

		// Utilizza una funzione deterministica per derivare la chiave privata
		BigInteger privateKey = deriveDeterministicPrivateKey(combinedData, domainParameters);

		return new ECPrivateKeyParameters(privateKey, domainParameters);
	}

	private BigInteger deriveDeterministicPrivateKey(byte[] input, ECDomainParameters domainParameters) {
		// Implementa una funzione deterministica per derivare la chiave privata
		// Puoi utilizzare una funzione di hash o una funzione di derivazione delle
		// chiavi (KDF)
		// In questo esempio, useremo una funzione di hash deterministica
		byte[] derivedBytes = deterministicHashFunction(input);
		return new BigInteger(1, derivedBytes).mod(domainParameters.getN()); // Assicurati che la chiave sia nel dominio
																				// della curva
	}

	private ECPoint deriveDeterministicPublicKeyPoint(byte[] input, ECDomainParameters domainParameters) {
		// KDF
		byte[] derivedBytes = deterministicHashFunction(input);
		return domainParameters.getG().multiply(new BigInteger(1, derivedBytes));
	}

	private byte[] deterministicHashFunction(byte[] input) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			return sha256.digest(input);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Errore durante l'hashing: " + e.getMessage());
		}
	}
}
