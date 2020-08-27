DROP TABLE IF EXISTS TB_USER_INFO;
DROP SEQUENCE IF EXISTS user_sequence_info;
CREATE SEQUENCE user_sequence_info;
CREATE TABLE TB_USER_INFO (
	 ID 						BIGINT 	default nextval('user_sequence_info')
	,ACCOUNT  			        VARCHAR(64) NOT NULL
	,PASSWORD  			        VARCHAR(512) NOT NULL
	,NAME  			            VARCHAR(64) NOT NULL
	,STUDENT_NUMBER  			VARCHAR(64)
	,STUDENT_EMAIL  			VARCHAR(128) NOT NULL
	,MAJOR        			    VARCHAR(64)
	,PHONE_NUMBER  			    VARCHAR(64)
	,PROFILE_IMAGE_URL  		VARCHAR(512) NOT NULL
	,SALT  			            VARCHAR(256) NOT NULL
	,GENDER					    BOOLEAN
	,GRADUATES		    		BOOLEAN   NOT NULL DEFAULT FALSE
    ,CERTIFIES					BOOLEAN   NOT NULL DEFAULT FALSE
    ,DELETES					BOOLEAN   NOT NULL DEFAULT FALSE
  	,CREATED_AT					TIMESTAMP DEFAULT now()
  	,UPDATED_AT					TIMESTAMP DEFAULT now()
	,PRIMARY KEY (ID)
	,UNIQUE(ACCOUNT)
	,UNIQUE(STUDENT_EMAIL)
);