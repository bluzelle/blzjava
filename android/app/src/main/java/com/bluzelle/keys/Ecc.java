// elliptic curve cryptography using secp256k1
// usage:
//    Ecc.Point point = Ecc.ecc.gMultiply(aBigInteger); // (x,y) = g * i
//    BigInteger n = Ecc.ecc.n; // subgroup order
//    BigInteger x = point.x;
//    BigInteger y = point.y;
//    byte[] signature = Ecc.ecc.sign(messageBytes, privateKeyBigInteger);
//    bool verified = Ecc.ecc.verify(messageBytes, publicKeyPoint, signatureBytes);
package com.bluzelle.keys;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class Ecc {
    public static final Ecc ecc = new Ecc();
    private static final BigInteger two = BigInteger.valueOf(2);
    private static final BigInteger three = BigInteger.valueOf(3);

    private final BigInteger p = new BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f", 16);
    // subgroup, g * r = g * (r % n)
    // subgroup order, n is the smallest integer such that g * n = infinity, n < p; 0 < private key < n
    @SuppressWarnings("WeakerAccess")
    public final BigInteger n = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);
    private final BigInteger halfN = n.divide(BigInteger.valueOf(2)); // = n / 2
    // base point, generator
    private final Point g = new Point(
            new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16),
            new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)
    );
    // point at infinity, infinity + q = q, infinity * r = infinity
    private final Point infinity = new Point();

    private Ecc() {
    }

    /**
     * @param r value to be multiplied by g
     * @return scalar multiplication base point and r, g * r
     * @throws NullPointerException if r == null
     */
    @SuppressWarnings("WeakerAccess")
    public Point gMultiply(BigInteger r) {
        return g.multiply(r);
    }

    /**
     * @param message the message to be signed
     * @param d       private key to sign message
     * @return 64 byte array r and s, big endian
     * @throws NullPointerException     if message == null or d == null
     * @throws IllegalArgumentException if message is too big
     */
    public byte[] sign(byte[] message, BigInteger d) {
        BigInteger z = new BigInteger(1, message);
        if (z.bitLength() > n.bitLength()) {
            throw new IllegalArgumentException("the message is too big, requires bitLength <= n.bitLength");
        }
        BigInteger r;
        BigInteger s = BigInteger.ZERO;
        Random random = new SecureRandom();
        do {
            // random integer k, 1 <= k < n
            BigInteger k;
            do {
                k = new BigInteger(n.bitLength(), random);
            } while (k.bitLength() < (n.bitLength() >> 1) || k.compareTo(n) >= 0);
            // p = g * k
            // r = px % n
            r = gMultiply(k).x.mod(n);
            if (r.signum() == 0) {
                continue;
            }
            // s * k = (z + r * d)
            // s = k**(-1) * (z + r * d) % n
            s = k.modInverse(n).multiply(z.add(r.multiply(d))).mod(n);
            if (s.compareTo(halfN) > 0) {
                s = n.subtract(s);
            }
        } while (s.signum() == 0);

        byte[] signature = new byte[64];
        byte[] rBytes = r.toByteArray();
        int rStart = rBytes[0] == 0 ? 1 : 0;
        System.arraycopy(rBytes, rStart, signature, 32 - (rBytes.length - rStart), rBytes.length - rStart);
        byte[] sBytes = s.toByteArray();
        int sStart = sBytes[0] == 0 ? 1 : 0;
        System.arraycopy(sBytes, sStart, signature, 64 - (sBytes.length - sStart), sBytes.length - sStart);
        return signature;
    }

    /**
     * @param message   the message whose signature to be verify
     * @param publicKey as a Point
     * @param signature 64 byte array r and s big endian
     * @return true if signature is valid
     * @throws NullPointerException           if message == null or publicKey == null or signature == null
     * @throws ArrayIndexOutOfBoundsException if signature length < 32
     */
    @SuppressWarnings("WeakerAccess")
    public boolean verify(byte[] message, Point publicKey, byte[] signature) {
        BigInteger z = new BigInteger(1, message);
        if (z.bitLength() > n.bitLength()) {
            return false;
        }

        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signature, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signature, 32, 64));

        if (r.signum() != 1 || r.compareTo(n) >= 0) {
            return false;
        }
        if (s.signum() != 1 || s.compareTo(n) >= 0) {
            return false;
        }
        // p = g * k
        // s * k = (z + r * d)
        // p = g * s**(-1) * (z + r * d) = g * s**(-1) * z + g * d * s**(-1) * r
        // = g * s**(-1) * z + publicKey * s**(-1) * r
        BigInteger sInverse = s.modInverse(n);
        Point p = gMultiply(sInverse.multiply(z)).add(publicKey.multiply(sInverse.multiply(r)));
        // r == px % n
        return r.compareTo(p.x.mod(n)) == 0;
    }

    @SuppressWarnings("WeakerAccess")
    public class Point {
        // point on the elliptic curve, immutable
        public final BigInteger x; // -1 for the point at infinity
        public final BigInteger y;

        // creates point at infinity
        private Point() {
            x = BigInteger.valueOf(-1);
            y = BigInteger.ZERO;
        }

        private Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        private boolean isInfinity() {
            return x.signum() < 0;
        }

        private Point add(Point q) {
            if (isInfinity()) {
                // 0 + q = q
                return q;
            }
            if (q.isInfinity()) {
                // p + 0 = p
                return this;
            }
            int xCompare = x.compareTo(q.x);
            if (xCompare == 0) {
                if (y.compareTo(q.y) == 0) {
                    // p + p = 2 * p
                    return getTwice();
                } else {
                    // p - p = 0
                    return infinity;
                }
            } else {
                // for different points slope m = (y2 - y1) / (x2 - x1)
                BigInteger m;
                BigInteger deltaX;
                if (xCompare > 0) {
                    m = y.subtract(q.y);
                    deltaX = x.subtract(q.x);
                } else {
                    m = q.y.subtract(y);
                    deltaX = q.x.subtract(x);
                }
                // deltaY / deltaX = deltaY * deltaX**(-1)
                if (deltaX.compareTo(BigInteger.ONE) > 0) {
                    m = m.multiply(deltaX.modInverse(p));
                }
                // y**2 = x**3 + t * x + k and y = m * x + n
                // x**3 + (-m**2) * x**2 + (t - 2 * m * n) * x + (k - n**2) = 0
                // x1 + x2 + x3 = -b / a = m**2
                // rx = m**2 - px - qx
                BigInteger rx = m.pow(2).subtract(x).subtract(q.x).mod(p);
                // m * (rx - px) = ry - py
                // ry = m * (rx - px) + py
                BigInteger ry = m.multiply(rx.subtract(x)).add(y).mod(p);
                // p + q + r = 0
                // r = -(p + q)
                return new Point(rx, p.subtract(ry));
            }
        }

        private Point multiply(BigInteger r) {
            if (isInfinity()) {
                // 0 * n = 0
                return this;
            }
            if (r.signum() == 0) {
                // p * 0 = 0
                return infinity;
            }
            if (r.signum() == -1) {
                // p * (-r) = -(p * r)
                Point inverse = multiply(r.negate());
                return new Point(inverse.x, p.subtract(inverse.y));
            }

            Point result = infinity;
            Point twice = this;
            int bitN = 0;
            int stop = r.bitLength();
            do {
                if (r.testBit(bitN)) {
                    result = result.add(twice);
                }
                twice = twice.getTwice();
                bitN++;
            } while (bitN != stop);
            return result;
        }

        // returns this * 2
        private Point getTwice() {
            // tangent slope m = (3 * x**2 + a) / (2 * y), a = 0
            BigInteger m = three.multiply(x.pow(2)).multiply(two.multiply(y).modInverse(p));
            // rx = m**2 - px - px
            BigInteger rx = m.pow(2).subtract(x).subtract(x).mod(p);
            // ry = m * (rx - px) + py
            BigInteger ry = m.multiply(rx.subtract(x)).add(y).mod(p);
            return new Point(rx, p.subtract(ry));
        }
    }
}