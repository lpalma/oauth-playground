package com.codurance.oauthPlayground;

import com.google.gson.annotations.SerializedName;

public class User {

   private static final String CODURANCE_EMAIL = "@codurance.com";

   private String issuedTo;

   private String audience;

   @SerializedName("user_id")
   private String userId;

   private String scope;

   private int expiresIn;

   private String email;

   private boolean verifiedEmail;

   private String accesType;

   public String getIssuedTo() {
      return issuedTo;
   }

   public void setIssuedTo(String issuedTo) {
      this.issuedTo = issuedTo;
   }

   public String getAudience() {
      return audience;
   }

   public void setAudience(String audience) {
      this.audience = audience;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getScope() {
      return scope;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public int getExpiresIn() {
      return expiresIn;
   }

   public void setExpiresIn(int expiresIn) {
      this.expiresIn = expiresIn;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public boolean isVerifiedEmail() {
      return verifiedEmail;
   }

   public void setVerifiedEmail(boolean verifiedEmail) {
      this.verifiedEmail = verifiedEmail;
   }

   public String getAccesType() {
      return accesType;
   }

   public void setAccesType(String accesType) {
      this.accesType = accesType;
   }

    public boolean isFromCodurance() {
        return getEmail().endsWith(CODURANCE_EMAIL);
    }
}
