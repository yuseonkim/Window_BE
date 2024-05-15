package org.example.maeum2_be._core;


public enum MessageCode {

  NOT_ALLOWED_FILE_EXT("4003", "파일 확장명은 pdf, jpg, jpeg, png만 가능합니다."),
  INVALID_SIZE_PARAMETER("400", "size 파라미터는 0보다 커야합니다."),
  INVALID_LAST_POST_ID_PARAMETER("400", "lastPostId 파라미터는 0보다 커야합니다."),
  USER_NOT_AUTHENTICATED("401", "유저가 인증되지 않았습니다."),
  NEED_LOGIN("401", "로그인이 필요합니다."),

  NEED_REGISTER("401", "회원가입이 필요합니다"),

  INVALIDATE_ACCESS_TOKEN("401", "잘못된 엑세스 토큰입니다."),

  EXPIRED_ACCESS_TOKEN("4111", "만료된 토큰입니다."),
  MEMBER_NOT_FOUND("401", "회원정보가 존재하지 않습니다"),

  REQUEST_ACCESS_DENIED("400","잘못된 요청 이름입니다"),

  Verification_Not_Equal("400","인증코드가 틀렸습니다."),
  Verification_Not_Found("400","인증번호 발급 후 클릭해주세요.")

  ;

  private final String code;
  private final String value;

  MessageCode(String code, String value) {
    this.code = code;
    this.value = value;
  }

  public String getCode() {
    return this.code;
  }

  public String getValue() {
    return this.value;
  }


}

