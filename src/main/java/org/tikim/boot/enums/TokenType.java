package org.tikim.boot.enums;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("TokenType")
public enum TokenType {
	USER,
	REUSABLE,
	DISPOSABLE
}
