package org.tikim.boot.enums;

public enum ErrorMessage {
	EXCEPTION_FOR_TEST(-1,"TEST를 위한 에러 메시지입니다."),
	UNDEFINED_EXCEPTION(0,"정의되지 않은 에러입니다."),
	NULL_POINTER_EXCEPTION(1,"NULL 여부를 확인해주세요"),
	JSON_PARSE_EXCEPTION(2,"JSON Parse 과정에 문제가 있습니다. 데이터를 확인해주세요"),
	AOP_XSS_SETTER_NO_EXSISTS_EXCEPTION(3,"해당 필드에 SETTER가 존재하지 않습니다."),
	AOP_XSS_FIELD_NO_EXSISTS_EXCEPTION(3,"해당 필드에 FIELD가 존재하지 않습니다."),

	/*
	 * DB_CONSTRAIN INVALID
	 */
	DB_CONSTRAINT_INVALID(1080,"데이터베이스의 고유 제약 조건을 위반하였습니다."),
	
	/*
	 * @VALID INVALID
	 */
	VALID_ANNOTATION_INVALID_EXCEPTION(1100,"@Validation 에러가 발생하였습니다."),
	PAGENATION_INVALID(1120,"pagenation의 범위를 확인해주세요. 0 < page , 0 < per_count"),
	JWT_NULL_POINTER_EXCEPTION(1050,"해당 JWT가 NULL입니다."),
	JWT_EXPIRED_EXCEPTION(1050,"해당 JWT는 만료되었습니다."),
	JWT_FORMAT_EXCEPTION(1050,"JWT의 FORMAT이 올바르지 않습니다."),
	JWT_PAYLOAD_EXCEPTION(1050,"JWT의 PAYLOAD가 올바르지 않습니다."),
	JWT_SUBJECT_INVALID_EXCEPTION(1050,"JWT의 Subject가 올바르지 않습니다."),
	JWT_REFRESH_INVALID_EXCEPTION(1050,"Refresh TOKEN이 올바르지 않습니다."),
	JWT_SALTNULL_EXCEPTION(1050,"해당 JWT의 SALT값이 NULL입니다."),
	JWT_NON_TYPE_EXCEPTION(1050,"타입이 존재하지 않습니다."),
	USER_NULL_POINTER_EXCEPTION(1050,"존재하지 않거나 삭제된 유저입니다."),
	USER_ACCOUNT_NULL_POINTER_EXCEPTION(111,"존재하지 않는 아이디입니다."),
	USER_PASSWORD_INVAILD_EXCEPTION(111,"올바르지 않은 비밀번호입니다."),
	USER_ACCOUNT_DUPLICATE(1050,"이미 사용중인 Account 입니다."),
	USER_STUDENT_EMAIL_DUPLICATE(1050,"이미 사용중인 Student Email 입니다."),
	;
	
	
	Integer code;
	String errorMessage;
	ErrorMessage(int code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}
	
	
	public Integer getCode() {
		return code;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	
}
