package com.example.fullstackcrudreact.fullstackbackend.financial;

import java.util.Arrays;
import java.util.List;

public class BarcodePayment {

    // Constants
    private static final List<Character> ALLOWED_SINGLE_BYTE_CHARACTERS = Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            ' ', ',', '.', ':', '-', '+', '?', '\'', '/', '(', ')'
    );
    private static final List<Character> ALLOWED_TWO_BYTE_CHARACTERS = Arrays.asList(
            'Š', 'Đ', 'Č', 'Ć', 'Ž', 'š', 'đ', 'č', 'ć', 'ž'
    );
    private static final List<Character> ALLOWED_CHARACTERS = new java.util.ArrayList<>(ALLOWED_SINGLE_BYTE_CHARACTERS);

    static {
        ALLOWED_CHARACTERS.addAll(ALLOWED_TWO_BYTE_CHARACTERS);
    }

    private static final String DELIMITER = "\n";
    private static final String HEADER = "HRVHUB30";
    private static final String CURRENCY = "EUR";
    private static final String PAYMENT_MODEL_PREFIX = "HR";

    private static final int PRICE_FIELD_LENGTH = 15;
    private static final String PRICE_PATTERN = "^[0-9]+,[0-9]{2}$";

    private boolean validateIBAN = false;
    private boolean validateModelPozivNaBroj = false;

    // Enumerations and helper classes
    public enum ValidationResult {
        OK,
        PricePatternInvalid, PriceMaxLengthExceeded,
        PayerNameInvalid, PayerNameMaxLengthExceeded,
        PayerAddressInvalid, PayerAddressMaxLengthExceeded,
        PayerHQInvalid, PayerHQMaxLengthExceeded,
        ReceiverNameInvalid, ReceiverNameMaxLengthExceeded,
        ReceiverAddressInvalid, ReceiverAddressMaxLengthExceeded,
        ReceiverHQInvalid, ReceiverHQMaxLengthExceeded,
        IBANInvalid, IBANMaxLengthExceeded,
        PaymentModelInvalid, PaymentModelMaxLengthExceeded,
        CalloutNumberInvalid, CalloutNumberMaxLengthExceeded,
        IntentCodeInvalid, IntentCodeMaxLengthExceeded,
        DescriptionInvalid, DescriptionMaxLengthExceeded
    }

    public static class PaymentParams {
        public String Iznos;
        public String ImePlatitelja;
        public String AdresaPlatitelja;
        public String SjedistePlatitelja;
        public String Primatelj;
        public String AdresaPrimatelja;
        public String SjedistePrimatelja;
        public String IBAN;
        public String ModelPlacanja;
        public String PozivNaBroj;
        public String SifraNamjene;
        public String OpisPlacanja;
    }

    public static class MaxLengths {
        public static final int Price = 16;
        public static final int PayerName = 30;
        public static final int PayerAddress = 27;
        public static final int PayerHQ = 27;
        public static final int ReceiverName = 25;
        public static final int ReceiverAddress = 25;
        public static final int ReceiverHQ = 27;
        public static final int IBAN = 21;
        public static final int PaymentModel = 2;
        public static final int CalloutNumber = 22;
        public static final int IntentCode = 4;
        public static final int Description = 35;
    }

    public BarcodePayment() {
        // Default constructor
    }

    public void init(boolean validateIBAN, boolean validateModelPozivNaBroj) {
        this.validateIBAN = validateIBAN;
        this.validateModelPozivNaBroj = validateModelPozivNaBroj;
    }

    public int getLength(String str) {
        int len = 0;
        if (str != null && !str.isEmpty()) {
            for (char c : str.toCharArray()) {
                if (ALLOWED_TWO_BYTE_CHARACTERS.contains(c)) {
                    len += 2;
                } else if (ALLOWED_SINGLE_BYTE_CHARACTERS.contains(c)) {
                    len += 1;
                } else {
                    return -1;
                }
            }
        }
        return len;
    }

    public boolean isIBANValid(String iban) {
        // Implement IBAN validation logic
        return true;
    }

    public boolean isPaymentModelValid(String paymentModel) {
        // Implement payment model validation logic
        return true;
    }

    public boolean isCalloutNumberValid(String calloutNumber, String paymentModel) {
        // Implement callout number validation logic
        return true;
    }

    public boolean isIntentCodeValid(String intentCode) {
        // Implement intent code validation logic
        return true;
    }

    public ValidationResult validatePaymentParams(PaymentParams paymentParams) {
        if (paymentParams == null) {
            return null;
        }

        ValidationResult result = ValidationResult.OK;
        int fieldLength;

        // Validate Price
        fieldLength = getLength(paymentParams.Iznos);
        if (fieldLength > MaxLengths.Price) {
            result = ValidationResult.PriceMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.Iznos) || !paymentParams.Iznos.matches(PRICE_PATTERN)) {
            result = ValidationResult.PricePatternInvalid;
        }

        // Validate Payer Name
        fieldLength = getLength(paymentParams.ImePlatitelja);
        if (fieldLength > MaxLengths.PayerName) {
            result = ValidationResult.PayerNameMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.ImePlatitelja)) {
            result = ValidationResult.PayerNameInvalid;
        }

        // Validate Payer Address
        fieldLength = getLength(paymentParams.AdresaPlatitelja);
        if (fieldLength > MaxLengths.PayerAddress) {
            result = ValidationResult.PayerAddressMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.AdresaPlatitelja)) {
            result = ValidationResult.PayerAddressInvalid;
        }

        // Validate Payer HQ
        fieldLength = getLength(paymentParams.SjedistePlatitelja);
        if (fieldLength > MaxLengths.PayerHQ) {
            result = ValidationResult.PayerHQMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.SjedistePlatitelja)) {
            result = ValidationResult.PayerHQInvalid;
        }

        // Validate Receiver Name
        fieldLength = getLength(paymentParams.Primatelj);
        if (fieldLength > MaxLengths.ReceiverName) {
            result = ValidationResult.ReceiverNameMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.Primatelj)) {
            result = ValidationResult.ReceiverNameInvalid;
        }

        // Validate Receiver Address
        fieldLength = getLength(paymentParams.AdresaPrimatelja);
        if (fieldLength > MaxLengths.ReceiverAddress) {
            result = ValidationResult.ReceiverAddressMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.AdresaPrimatelja)) {
            result = ValidationResult.ReceiverAddressInvalid;
        }

        // Validate Receiver HQ
        fieldLength = getLength(paymentParams.SjedistePrimatelja);
        if (fieldLength > MaxLengths.ReceiverHQ) {
            result = ValidationResult.ReceiverHQMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.SjedistePrimatelja)) {
            result = ValidationResult.ReceiverHQInvalid;
        }

        // Validate IBAN
        fieldLength = getLength(paymentParams.IBAN);
        if (fieldLength > MaxLengths.IBAN) {
            result = ValidationResult.IBANMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.IBAN) || (validateIBAN && !isIBANValid(paymentParams.IBAN))) {
            result = ValidationResult.IBANInvalid;
        }

        // Validate Payment Model
        fieldLength = getLength(paymentParams.ModelPlacanja);
        if (fieldLength > MaxLengths.PaymentModel) {
            result = ValidationResult.PaymentModelMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.ModelPlacanja) || !isPaymentModelValid(paymentParams.ModelPlacanja)) {
            result = ValidationResult.PaymentModelInvalid;
        }

        // Validate Callout Number
        fieldLength = getLength(paymentParams.PozivNaBroj);
        if (fieldLength > MaxLengths.CalloutNumber) {
            result = ValidationResult.CalloutNumberMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.PozivNaBroj) || !isCalloutNumberValid(paymentParams.PozivNaBroj, paymentParams.ModelPlacanja)) {
            result = ValidationResult.CalloutNumberInvalid;
        }

        // Validate Intent Code
        fieldLength = getLength(paymentParams.SifraNamjene);
        if (fieldLength > MaxLengths.IntentCode) {
            result = ValidationResult.IntentCodeMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.SifraNamjene) || !isIntentCodeValid(paymentParams.SifraNamjene)) {
            result = ValidationResult.IntentCodeInvalid;
        }

        // Validate Description
        fieldLength = getLength(paymentParams.OpisPlacanja);
        if (fieldLength > MaxLengths.Description) {
            result = ValidationResult.DescriptionMaxLengthExceeded;
        }
        if (!isValidString(paymentParams.OpisPlacanja)) {
            result = ValidationResult.DescriptionInvalid;
        }

        return result;
    }

    public String getEncodedText(PaymentParams paymentParams) {
        if (paymentParams == null || validatePaymentParams(paymentParams) != ValidationResult.OK) {
            return null;
        }

        return String.join(DELIMITER,
                HEADER,
                CURRENCY,
                padLeft(paymentParams.Iznos.replace(",", ""), PRICE_FIELD_LENGTH, '0'),
                paymentParams.ImePlatitelja,
                paymentParams.AdresaPlatitelja,
                paymentParams.SjedistePlatitelja,
                paymentParams.Primatelj,
                paymentParams.AdresaPrimatelja,
                paymentParams.SjedistePrimatelja,
                paymentParams.IBAN,
                PAYMENT_MODEL_PREFIX + paymentParams.ModelPlacanja,
                paymentParams.PozivNaBroj,
                paymentParams.SifraNamjene,
                paymentParams.OpisPlacanja
        );
    }

    private boolean isValidString(String str) {
        return str != null && !str.isEmpty() && getLength(str) != -1;
    }

    private String padLeft(String str, int length, char padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < length) {
            padded.insert(0, padChar);
        }
        return padded.toString();
    }

    // Additional methods for intent codes and payment models can be added here
}
