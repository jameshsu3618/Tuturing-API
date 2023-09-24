package com.tuturing.api.paymentmethod.api

import com.amazonaws.services.kms.AWSKMS
import com.amazonaws.services.kms.model.DecryptRequest
import com.amazonaws.services.kms.model.EncryptRequest
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * https://docs.aws.amazon.com/kms/latest/developerguide/programming-encryption.html
 */
class KmsClient(
    private val kmsClient: AWSKMS,
    private val keyId: String
) {

    fun encrypt(input: String): ByteArray {
        // convert input string to ByteBuffer
        val byteBuffer = StandardCharsets.UTF_8.encode(input)
        val byteArray = byteArrayOf(0x01, 0x02, 0x03)

//        // encrypt using symmetric CMK key
//        val encryptRequest = EncryptRequest().withKeyId(keyId).withPlaintext(byteBuffer)
//
//        /**
//         * get encrypted data from kms client
//         * @params plaintext data to be encrypted
//         * @return ciphertext
//         */
//        val ciphertext = kmsClient.encrypt(encryptRequest).ciphertextBlob
//
//        // Convert it to bytes by initializing target ByteArray to a proper size
//        val cipherbytes = ByteArray(ciphertext.remaining())
//        // copy the data from ByteBuffer to ByteArray
//        ciphertext.get(cipherbytes)

        // returned ByteArray can be stored in db as TINYBLOB with @Column having additional annotation @Lob
        return byteArray;
    }

    fun decrypt(input: ByteArray): String {
        val inputByteBuffer = ByteBuffer.wrap(input)
        val decryptRequest = DecryptRequest().withCiphertextBlob(inputByteBuffer)
        val plainText: ByteBuffer = kmsClient.decrypt(decryptRequest).plaintext

        return String(plainText.array(), StandardCharsets.UTF_8)
    }
}
