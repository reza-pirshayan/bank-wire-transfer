package com.pirshayan.domain.model;

import java.math.BigInteger;

import com.pirshayan.domain.exception.InvalidDomainObjectException;

public interface Validator {
	public static final Long MIN_TRANSFER_AMOUNT = 10001L;
	public static final Long MAX_TRANSFER_AMOUNT = 50000000000L;

	static Boolean isValidString(String value) {
		if (!value.matches("[!\\\"%\\'‍‍‍‍‍\\`\\*\\+\\.\\?:;<=>@\\[\\]\\^\\{|\\}~&\\#\\$\\\\\\/]"))
			return true;
		else
			return false;
	}

	static Boolean isValidIban(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}

		// Remove spaces and convert to uppercase
		value = value.replaceAll("\\s", "").toUpperCase();

		// Check length for the country code
//		String countryCode = value.substring(0, 2);
		// IR IBAN length is 26
		Integer expectedLength = 26;
		if (expectedLength == null || value.length() != expectedLength) {
			return false;
		}

		// Rearrange the IBAN: Move the first four characters to the end
		String rearrangedIban = value.substring(4) + value.substring(0, 4);

		// Replace letters with numbers (A=10, B=11, ..., Z=35)
		StringBuilder numericIban = new StringBuilder();
		for (char c : rearrangedIban.toCharArray()) {
			if (Character.isDigit(c)) {
				numericIban.append(c);
			} else if (Character.isLetter(c)) {
				numericIban.append(c - 'A' + 10);
			} else {
				return false; // Invalid character
			}
		}

		// Perform the modulo 97 check
		BigInteger ibanNumber = new BigInteger(numericIban.toString());
		return ibanNumber.mod(BigInteger.valueOf(97)).intValue() == 1;
	}

	static Boolean isValidMaxPermittedAmount(Long value) {
		if (value >= 0 && value <= MAX_TRANSFER_AMOUNT)
			return true;
		else
			return false;
	}
	
	static Boolean isValidTransferAmount(Long value) {
		if (value != null && value >= MIN_TRANSFER_AMOUNT && value <= MAX_TRANSFER_AMOUNT)
			return true;
		else
			return false;
	}

	static String validateBankAccountOwnerName(String value) {
		if (isValidString(value) && value.matches("^([^0-9])+$") && value.length() >= 2 && value.length() <= 200) {
			return value;
		} else {
			throw new InvalidDomainObjectException("invalid account owner's name", "BankAccountOwner", "name");
		}
	}

	static String validateNaturalNationalCode(String value) {

		// Calculate checksum
		int checksum = 0;
		for (int i = 0; i < 9; i++) {
			checksum += Character.getNumericValue(value.charAt(i)) * (10 - i);
		}
		checksum %= 11;

		// Get the 10th digit (checksum) and validate
		int lastDigit = Character.getNumericValue(value.charAt(9));
		Boolean result = false;
		if (checksum < 2) {
			result = checksum == lastDigit;
		} else {
			result = lastDigit == (11 - checksum);
		}
		if (result) {
			return value;
		} else {
			throw new InvalidDomainObjectException("Invalid national code.", "BankAccountOwner", "id");
		}
	}

	static String validateLegalNationalCode(String value) {
		if (value == null || !value.matches("\\d{1,20}")) {
			throw new InvalidDomainObjectException("Invalid national code.", "BankAccountOwner", "id");
		}
		return value;
	}

	static String validateBankAccountOwnerMobileNumber(String value) {
		if (value == null) {
			return null;
		}

		if (value.matches("^09\\d{9}$"))
			return value;
		else
			throw new InvalidDomainObjectException("Invalid mobile number.", "BankAccountOwner", "mobileNumber");
	}

	static String validateTransferOwnerName(String value) {
		if (isValidString(value) && value.length() <= 28) {
			boolean wavFlag = false;
			int wavPosition = 0;
			String[] words = value.split("[\\s|\\t]+");

			if (words.length >= 2) {
				for (int i = 0; i < words.length; i++) {
					// Arabic letter W-a-w has the UNICODE value 1608 (U+0648)
					if (words[i].length() == 1 && words[i].charAt(0) == 0x0648) {
						wavFlag = true;
						wavPosition = i;
					} else if (words[i].length() < 2) {
						break;
					}
				}

				if (wavFlag) {
					if (wavPosition > 0 && wavPosition < words.length - 1) {
						return value; // Valid case with W-a-w positioned properly
					}
				} else {
					return value; // Valid case without W-a-w
				}
			}
		}
		throw new InvalidDomainObjectException("invalid ACH transfer owner's name", "TransferOwner", "name");
	}

	static String validateIban(String value) {
		if (isValidIban(value))
			return value;
		else
			throw new InvalidDomainObjectException("invalid iban", "DestinationBankAccount", "iban");
	}

	static String validateTransferId(String value) {
		if (value.matches("^[a-zA-Z0-9_/]+$") && value.length() < 28)
			return value;
		else
			throw new InvalidDomainObjectException("invalid  transfer id", "Transfer", "transferId");
	}

	static Long validateTransferAmount(Long value) {
		if (isValidTransferAmount(value))
			return value;
		else
			throw new InvalidDomainObjectException("invalid amount", "Transfer", "amount");
	}
	


	static Integer validateTransferChecksum(Integer value) {
		if (value >= 0 && value <= 9) {
			return value;
		} else
			throw new InvalidDomainObjectException("invalid checksum", "AchTransfer", "checksum");
	}

	static String validateDescription(String value) {
		if (value == null)
			return null;

		if (isValidString(value))
			return value;
		else
			throw new InvalidDomainObjectException("invalid description", "AchTransfer", "description");
	}

	static String validatePayId(String value) {
		if (value == null)
			return null;

		if (value.matches("^[0-9]{1,30}$"))
			return value;
		else
			throw new InvalidDomainObjectException("invalid pay id", "AchTransfer", "payId");
	}

	static String validateAchTransferOrderId(String value) {

		if (value.matches("[0-9]{15,17}") || value.matches("[0-9a-zA-Z_]{9,13}"))
			return value;
		else
			throw new InvalidDomainObjectException("invalid ACH transfer order id", "AchTransferOrder", "id");
	}
	
	static Long validateMaxSendAmount(Long value) {
		if (isValidMaxPermittedAmount(value)) {
			return value;
		} else {
			throw new InvalidDomainObjectException("invalid maxSendAmount", "FinanceOfficeRule", "maxSendAmount");
		}
	}
	
	static Long validateMaxFirstSignAmount(Long value) {
		if (isValidMaxPermittedAmount(value)) {
			return value;
		} else {
			throw new InvalidDomainObjectException("invalid maxFirstSignAmount", "FinanceOfficeRule", "maxFirstSignAmount");
		}
	}
	
	static Long validateMaxSecondSignAmount(Long value) {
		if (isValidMaxPermittedAmount(value)) {
			return value;
		} else {
			throw new InvalidDomainObjectException("invalid maxSecondSignAmount", "FinanceOfficerRule", "maxSecondSignAmount");
		}
	}
}
